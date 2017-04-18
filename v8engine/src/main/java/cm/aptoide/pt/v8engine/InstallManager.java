/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 04/10/2016.
 */

package cm.aptoide.pt.v8engine;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import cm.aptoide.pt.database.exceptions.DownloadNotFoundException;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.installer.DefaultInstaller;
import cm.aptoide.pt.v8engine.install.installer.InstallationState;
import cm.aptoide.pt.v8engine.install.installer.RollbackInstaller;
import cm.aptoide.pt.v8engine.install.root.RootShell;
import cm.aptoide.pt.v8engine.repository.DownloadRepository;
import cm.aptoide.pt.v8engine.repository.InstalledRepository;
import cm.aptoide.pt.v8engine.repository.Repository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 9/29/16.
 */

public class InstallManager {

  private final AptoideDownloadManager aptoideDownloadManager;
  private final Installer installer;
  private final DownloadRepository downloadRepository;
  private final InstalledRepository installedRepository;

  /**
   * Uses the default {@link Repository} for {@link Download} and {@link Installed}
   */
  public InstallManager(AptoideDownloadManager aptoideDownloadManager, Installer installer) {
    this.aptoideDownloadManager = aptoideDownloadManager;
    this.installer = installer;
    this.downloadRepository = RepositoryFactory.getDownloadRepository();
    this.installedRepository = RepositoryFactory.getInstalledRepository();
  }

  public void stopAllInstallations(Context context) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_STOP_ALL_INSTALLS);
    context.startService(intent);
  }

  public void removeInstallationFile(String md5, Context context) {
    stopInstallation(context, md5);
    aptoideDownloadManager.removeDownload(md5);
  }

  public void stopInstallation(Context context, String md5) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_STOP_INSTALL);
    intent.putExtra(InstallService.EXTRA_INSTALLATION_MD5, md5);
    context.startService(intent);
  }

  public Completable uninstall(Context context, String packageName, String versionName) {
    return installer.uninstall(context, packageName, versionName);
  }

  @Deprecated public Observable<List<Progress<Download>>> getInstallationsDeprecated() {
    return aptoideDownloadManager.getDownloads()
        .observeOn(Schedulers.io())
        .concatMap(downloadList -> Observable.from(downloadList)
            .flatMap(download -> convertToProgress(download))
            .toList());
  }

  public Observable<List<InstallationProgress>> getInstallations() {
    return Observable.combineLatest(aptoideDownloadManager.getDownloads(),
        installedRepository.getAll(), (downloads, installeds) -> downloads)
        .observeOn(Schedulers.io())
        .concatMap(downloadList -> Observable.from(downloadList)
            .flatMap(
                download -> getInstallationProgress(download.getMd5(), download.getPackageName(),
                    download.getVersionCode()).first())
            .toList());
  }

  private Observable<Progress<Download>> convertToProgress(Download currentDownload) {
    return installedRepository.get(currentDownload.getPackageName())
        .first()
        .flatMap(installed -> convertToProgressStatus(currentDownload, installed).map(status -> {
          int installationType = installed == null ? Installed.TYPE_UNKNOWN : installed.getType();
          return new Progress<>(currentDownload,
              currentDownload.getOverallDownloadStatus() == Download.COMPLETED,
              AptoideDownloadManager.PROGRESS_MAX_VALUE, currentDownload.getOverallProgress(),
              currentDownload.getDownloadSpeed(), status, installationType);
        }));
  }

  private Observable<Integer> convertToProgressStatus(Download download, Installed installed) {
    return Observable.just(installed)
        .map(installation -> installation != null
            && installation.getVersionCode() == download.getVersionCode()
            && installation.getStatus() == Installed.STATUS_COMPLETED)
        .map(isInstalled -> {

          if (isInstalled) {
            return Progress.DONE;
          }

          final int progressStatus;
          switch (download.getOverallDownloadStatus()) {
            case Download.PROGRESS:
            case Download.PENDING:
            case Download.IN_QUEUE:
            case Download.INVALID_STATUS:
            case Download.COMPLETED:
              progressStatus = Progress.ACTIVE;
              break;
            case Download.PAUSED:
              progressStatus = Progress.INACTIVE;
              break;
            case Download.WARN:
            case Download.BLOCK_COMPLETE:
            case Download.CONNECTED:
            case Download.RETRY:
            case Download.STARTED:
            case Download.NOT_DOWNLOADED:
            case Download.ERROR:
            case Download.FILE_MISSING:
              progressStatus = Progress.ERROR;
              break;
            default:
              progressStatus = Progress.INACTIVE;
              break;
          }
          return progressStatus;
        });
  }

  @Deprecated public Observable<Progress<Download>> getCurrentInstallation() {
    return getInstallationsDeprecated().flatMap(
        progresses -> Observable.from(progresses).filter(progress -> isInstalling(progress)));
  }

  @Deprecated public boolean isInstalling(Progress<Download> progress) {
    return isDownloading(progress) || (progress.getState() != Progress.DONE
        && progress.getRequest().getOverallDownloadStatus() == Download.COMPLETED);
  }

  @Deprecated public boolean isDownloading(Progress<Download> progress) {
    return progress.getRequest().getOverallDownloadStatus() == Download.PROGRESS;
  }

  @Deprecated public Observable<Progress<Download>> getInstallation(String md5) {
    return aptoideDownloadManager.getDownload(md5).flatMap(download -> convertToProgress(download));
  }

  public Observable<Progress<Download>> install(Context context, Download download) {
    return getInstallation(download.getMd5()).first()
        .map(progress -> updateDownloadAction(download, progress))
        .retryWhen(errors -> createDownloadAndRetry(errors, download))
        .doOnNext(downloadProgress -> {
          if (downloadProgress.getRequest().getOverallDownloadStatus() == Download.ERROR) {
            downloadProgress.getRequest().setOverallDownloadStatus(Download.INVALID_STATUS);
            downloadRepository.save(downloadProgress.getRequest());
          }
        })
        .flatMap(progress -> installInBackground(context, progress));
  }

  public Observable<InstallationProgress> getInstallationProgress(String md5, String packageName,
      int versioncode) {
    return Observable.combineLatest(aptoideDownloadManager.getAsListDownload(md5),
        installer.getState(packageName, versioncode),
        (download, installationState) -> createInstallationProgress(download, installationState,
            md5, packageName, versioncode));
  }

  private InstallationProgress createInstallationProgress(Download download,
      InstallationState installationState, String md5, String packageName, int versioncode) {
    return new InstallationProgress(mapInstallationProgress(download),
        mapInstallationStatus(download, installationState),
        mapIndeterminateState(download, installationState), getSpeed(download), md5, packageName,
        versioncode, getAppName(download, installationState),
        getAppIcon(download, installationState));
  }

  private String getAppIcon(Download download, InstallationState installationState) {
    if (download != null) {
      return download.getIcon();
    } else {
      return installationState.getIcon();
    }
  }

  private String getAppName(Download download, InstallationState installationState) {
    if (download != null) {
      return download.getAppName();
    } else {
      return installationState.getName();
    }
  }

  private int getSpeed(Download download) {
    if (download != null) {
      return download.getDownloadSpeed();
    } else {
      return 0;
    }
  }

  private boolean mapIndeterminateState(Download download, InstallationState installationState) {
    return mapIndeterminate(download) || mapInstallIndeterminate(installationState.getStatus(),
        installationState.getType());
  }

  private InstallationProgress.InstallationStatus mapInstallationStatus(Download download,
      InstallationState installationState) {

    if (installationState.getStatus() == Installed.STATUS_COMPLETED) {
      return InstallationProgress.InstallationStatus.INSTALLED;
    }

    if (installationState.getStatus() == Installed.STATUS_INSTALLING
        && installationState.getType() != Installed.TYPE_DEFAULT) {
      return InstallationProgress.InstallationStatus.INSTALLING;
    }

    return mapDownloadState(download);
  }

  private int mapInstallationProgress(Download download) {
    int progress = 0;
    if (download != null) {
      progress = download.getOverallProgress();
    }
    return progress;
  }

  private boolean mapIndeterminate(Download download) {
    boolean isIndeterminate = false;
    if (download != null) {
      switch (download.getOverallDownloadStatus()) {
        case Download.IN_QUEUE:
          isIndeterminate = true;
          break;
        case Download.BLOCK_COMPLETE:
        case Download.COMPLETED:
        case Download.CONNECTED:
        case Download.ERROR:
        case Download.FILE_MISSING:
        case Download.INVALID_STATUS:
        case Download.NOT_DOWNLOADED:
        case Download.PAUSED:
        case Download.PENDING:
        case Download.PROGRESS:
        case Download.RETRY:
        case Download.STARTED:
        case Download.WARN:
          isIndeterminate = false;
          break;
        default:
          isIndeterminate = false;
      }
    }
    return isIndeterminate;
  }

  private InstallationProgress.InstallationStatus mapDownloadState(Download download) {
    InstallationProgress.InstallationStatus status =
        InstallationProgress.InstallationStatus.UNINSTALLED;
    if (download != null) {
      switch (download.getOverallDownloadStatus()) {
        case Download.FILE_MISSING:
        case Download.INVALID_STATUS:
        case Download.NOT_DOWNLOADED:
        case Download.COMPLETED:
          status = InstallationProgress.InstallationStatus.UNINSTALLED;
          break;
        case Download.PAUSED:
          status = InstallationProgress.InstallationStatus.PAUSED;
          break;
        case Download.ERROR:
          status = InstallationProgress.InstallationStatus.FAILED;
          break;
        case Download.RETRY:
        case Download.STARTED:
        case Download.WARN:
        case Download.CONNECTED:
        case Download.BLOCK_COMPLETE:
        case Download.PROGRESS:
        case Download.IN_QUEUE:
        case Download.PENDING:
          status = InstallationProgress.InstallationStatus.INSTALLING;
          break;
      }
    }
    return status;
  }

  private boolean mapInstallIndeterminate(int status, int type) {
    boolean isIndeterminate = false;
    switch (status) {
      case Installed.STATUS_UNINSTALLED:
      case Installed.STATUS_COMPLETED:
        isIndeterminate = false;
        break;
      case Installed.STATUS_INSTALLING:
        if (type == Installed.TYPE_DEFAULT) {
          isIndeterminate = false;
        } else {
          isIndeterminate = true;
        }
        break;
    }
    return isIndeterminate;
  }

  @NonNull
  private Progress<Download> updateDownloadAction(Download download, Progress<Download> progress) {
    if (progress.getRequest().getAction() != download.getAction()) {
      progress.getRequest().setAction(download.getAction());
      downloadRepository.save(progress.getRequest());
    }
    return progress;
  }

  private Observable<Throwable> createDownloadAndRetry(Observable<? extends Throwable> errors,
      Download download) {
    return errors.flatMap(throwable -> {
      if (throwable instanceof DownloadNotFoundException) {
        downloadRepository.save(download);
        return Observable.just(throwable);
      } else {
        return Observable.error(throwable);
      }
    });
  }

  private Observable<Progress<Download>> installInBackground(Context context,
      Progress<Download> progress) {
    return getInstallation(progress.getRequest().getMd5()).mergeWith(
        startBackgroundInstallationAndWait(context, progress));
  }

  @NonNull
  private Observable<Progress<Download>> startBackgroundInstallationAndWait(Context context,
      Progress<Download> progress) {
    return waitBackgroundInstallationResult(context, progress.getRequest().getMd5()).doOnSubscribe(
        () -> startBackgroundInstallation(context, progress.getRequest().getMd5())).map(success -> {
      progress.setState(Progress.DONE);
      return progress;
    });
  }

  private Observable<Void> waitBackgroundInstallationResult(Context context, String md5) {
    return Observable.create(new BroadcastRegisterOnSubscribe(context,
        new IntentFilter(InstallService.ACTION_INSTALL_FINISHED), null, null))
        .filter(intent -> intent != null && InstallService.ACTION_INSTALL_FINISHED.equals(
            intent.getAction()))
        .first(intent -> md5.equals(intent.getStringExtra(InstallService.EXTRA_INSTALLATION_MD5)))
        .map(intent -> null);
  }

  private void startBackgroundInstallation(Context context, String md5) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_START_INSTALL);
    intent.putExtra(InstallService.EXTRA_INSTALLATION_MD5, md5);
    if (installer instanceof RollbackInstaller) {
      intent.putExtra(InstallService.EXTRA_INSTALLER_TYPE, InstallService.INSTALLER_TYPE_ROLLBACK);
    } else if (installer instanceof DefaultInstaller) {
      intent.putExtra(InstallService.EXTRA_INSTALLER_TYPE, InstallService.INSTALLER_TYPE_DEFAULT);
    }
    context.startService(intent);
  }

  public boolean showWarning() {
    boolean wasRootDialogShowed = SecurePreferences.isRootDialogShowed();
    boolean isRooted = RootShell.isRootAvailable();
    boolean canGiveRoot = ManagerPreferences.allowRootInstallation();
    return isRooted && !wasRootDialogShowed && !canGiveRoot;
  }

  public void rootInstallAllowed(boolean allowRoot) {
    SecurePreferences.setRootDialogShowed(true);
    ManagerPreferences.setAllowRootInstallation(allowRoot);
    if (allowRoot) {
      RootShell.isAccessGiven();
    }
  }

  /**
   * @return true if all downloads started with success, false otherwise
   */
  public Observable<Boolean> startInstalls(List<Download> downloads, Context context) {
    return Observable.from(downloads)
        .map(download -> install(context, download))
        .toList()
        .flatMap(observables -> Observable.merge(observables))
        .filter(downloading -> downloading.getState() == Progress.DONE)
        .toList()
        .map(progresses -> true)
        .onErrorReturn(throwable -> false);
  }

  public Completable onAppInstalled(Installed installed) {
    return installedRepository.getAsList(installed.getPackageName())
        .first()
        .flatMapIterable(installeds -> {
          //in case of installation made outside of aptoide
          if (installeds.isEmpty()) {
            installeds.add(installed);
          }
          return installeds;
        })
        .flatMapCompletable(databaseInstalled -> {
          if (databaseInstalled.getVersionCode() == installed.getVersionCode()) {
            installed.setType(databaseInstalled.getType());
            installed.setStatus(Installed.STATUS_COMPLETED);
            return Completable.fromAction(() -> installedRepository.save(installed));
          } else {
            return installedRepository.remove(databaseInstalled.getPackageName(),
                databaseInstalled.getVersionCode());
          }
        })
        .toCompletable();
  }

  public Completable onAppRemoved(String packageName) {
    return installedRepository.getAsList(packageName)
        .first()
        .flatMapIterable(installeds -> installeds)
        .flatMapCompletable(
            installed -> installedRepository.remove(packageName, installed.getVersionCode()))
        .toCompletable();
  }

  public Observable<InstallationType> getInstallationType(String packageName, int versionCode) {
    return installedRepository.get(packageName).map(installed -> {
      if (installed == null) {
        return InstallationType.INSTALL;
      } else if (installed.getVersionCode() == versionCode) {
        return InstallationType.INSTALLED;
      } else if (installed.getVersionCode() > versionCode) {
        return InstallationType.DOWNGRADE;
      } else {
        return InstallationType.UPDATE;
      }
    });
  }

  public Completable onUpdateConfirmed(Installed installed) {
    return onAppInstalled(installed);
  }

  public Single<Error> getError(String md5) {
    return aptoideDownloadManager.getDownload(md5).first().map(download -> {
      Error error = Error.GENERIC_ERROR;
      switch (download.getDownloadError()) {
        case Download.GENERIC_ERROR:
          error = Error.GENERIC_ERROR;
          break;
        case Download.NOT_ENOUGH_SPACE_ERROR:
          error = Error.NOT_ENOUGH_SPACE_ERROR;
          break;
      }
      return error;
    }).toSingle();
  }

  /**
   * this method should only be used when a download exists already(ex: resuming)
   *
   * @return the download object to be resumed
   */
  public Single<Download> getDownload(String md5) {
    return downloadRepository.get(md5).first().toSingle();
  }

  public enum InstallationType {
    INSTALLED, INSTALL, UPDATE, DOWNGRADE
  }

  public enum Error {
    GENERIC_ERROR, NOT_ENOUGH_SPACE_ERROR
  }
}
