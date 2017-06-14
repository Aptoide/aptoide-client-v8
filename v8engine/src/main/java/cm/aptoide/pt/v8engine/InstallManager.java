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
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.installer.DefaultInstaller;
import cm.aptoide.pt.v8engine.install.installer.InstallationState;
import cm.aptoide.pt.v8engine.install.installer.RollbackInstaller;
import cm.aptoide.pt.v8engine.repository.DownloadRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import java.util.Collections;
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
  private RootAvailabilityManager rootAvailabilityManager;

  public InstallManager(AptoideDownloadManager aptoideDownloadManager, Installer installer,
      RootAvailabilityManager rootAvailabilityManager) {
    this.aptoideDownloadManager = aptoideDownloadManager;
    this.installer = installer;
    this.rootAvailabilityManager = rootAvailabilityManager;
    this.downloadRepository = RepositoryFactory.getDownloadRepository();
    this.installedRepository = RepositoryFactory.getInstalledRepository();
  }

  public void stopAllInstallations(Context context) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_STOP_ALL_INSTALLS);
    context.startService(intent);
  }

  public void removeInstallationFile(String md5, Context context, String packageName,
      int versionCode) {
    stopInstallation(context, md5);
    installedRepository.remove(packageName, versionCode)
        .andThen(Completable.fromAction(() -> aptoideDownloadManager.removeDownload(md5)))
        .subscribe(() -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
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

  public Observable<List<InstallationProgress>> getTimedOutInstallations() {
    return getInstallations().flatMap(
        installationProgresses -> Observable.from(installationProgresses)
            .filter(installationProgress -> installationProgress.getState()
                .equals(InstallationProgress.InstallationStatus.INSTALLATION_TIMEOUT))
            .toList());
  }

  public Observable<List<InstallationProgress>> getInstallations() {
    return Observable.combineLatest(aptoideDownloadManager.getDownloads(),
        installedRepository.getAllInstalled(), (downloads, installeds) -> downloads)
        .observeOn(Schedulers.io())
        .concatMap(downloadList -> Observable.from(downloadList)
            .flatMap(
                download -> getInstallationProgress(download.getMd5(), download.getPackageName(),
                    download.getVersionCode()).first())
            .toList())
        .map(installationProgresses -> sortList(installationProgresses));
  }

  private List<InstallationProgress> sortList(List<InstallationProgress> installationProgresses) {
    Collections.sort(installationProgresses, (installationProgress, t1) -> {
      int toReturn;
      if (installationProgress.getState() == InstallationProgress.InstallationStatus.INSTALLING
          && !installationProgress.isIndeterminate()) {
        toReturn = 1;
      } else if (t1.getState() == InstallationProgress.InstallationStatus.INSTALLING
          && !t1.isIndeterminate()) {
        toReturn = -1;
      } else {
        int diff = installationProgress.getState()
            .ordinal() - t1.getState()
            .ordinal();
        if (diff == 0) {
          toReturn = installationProgress.getPackageName()
              .compareTo(t1.getPackageName());
        } else {
          toReturn = diff;
        }
      }
      return toReturn;
    });
    Collections.reverse(installationProgresses);
    return installationProgresses;
  }

  public Observable<InstallationProgress> getCurrentInstallation() {
    return getInstallations().flatMap(progresses -> Observable.from(progresses)
        .filter(
            progress -> progress.getState() == InstallationProgress.InstallationStatus.INSTALLING));
  }

  public Completable install(Context context, Download download) {
    return install(context, download, false);
  }

  public Completable defaultInstall(Context context, Download download) {
    return install(context, download, true);
  }

  public Completable install(Context context, Download download, boolean forceDefaultInstall) {
    return aptoideDownloadManager.getDownload(download.getMd5())
        .first()
        .map(storedDownload -> updateDownloadAction(download, storedDownload))
        .retryWhen(errors -> createDownloadAndRetry(errors, download))
        .doOnNext(downloadProgress -> {
          if (downloadProgress.getOverallDownloadStatus() == Download.ERROR) {
            downloadProgress.setOverallDownloadStatus(Download.INVALID_STATUS);
            downloadRepository.save(downloadProgress);
          }
        })
        .flatMap(download1 -> getInstallationProgress(download.getMd5(), download.getPackageName(),
            download.getVersionCode()))
        .flatMap(progress -> installInBackground(context, progress, forceDefaultInstall))
        .first()
        .toCompletable();
  }

  public Observable<InstallationProgress> getInstallationProgress(String md5, String packageName,
      int versioncode) {
    return Observable.combineLatest(aptoideDownloadManager.getAsListDownload(md5),
        installer.getState(packageName, versioncode), getInstallationType(packageName, versioncode),
        (download, installationState, installationType) -> createInstallationProgress(download,
            installationState, md5, packageName, versioncode, installationType));
  }

  private InstallationProgress createInstallationProgress(Download download,
      InstallationState installationState, String md5, String packageName, int versioncode,
      InstallationProgress.InstallationType installationType) {
    return new InstallationProgress(mapInstallationProgress(download),
        mapInstallationStatus(download, installationState), installationType,
        mapIndeterminateState(download, installationState), getSpeed(download), md5, packageName,
        versioncode, getAppName(download, installationState),
        getAppIcon(download, installationState), getError(download));
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
        installationState.getType(), download);
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

    if (installationState.getStatus() == Installed.STATUS_WAITING
        && download != null
        && download.getOverallDownloadStatus() == Download.COMPLETED) {
      return InstallationProgress.InstallationStatus.INSTALLING;
    }

    if (installationState.getStatus() == Installed.STATUS_ROOT_TIMEOUT) {
      return InstallationProgress.InstallationStatus.INSTALLATION_TIMEOUT;
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

  private boolean mapInstallIndeterminate(int status, int type, Download download) {
    boolean isIndeterminate = false;
    switch (status) {
      case Installed.STATUS_UNINSTALLED:
      case Installed.STATUS_COMPLETED:
        isIndeterminate = false;
        break;
      case Installed.STATUS_INSTALLING:
      case Installed.STATUS_ROOT_TIMEOUT:
        if (type == Installed.TYPE_DEFAULT) {
          isIndeterminate = false;
        } else {
          isIndeterminate = true;
        }
        break;
      case Installed.STATUS_WAITING:
        isIndeterminate =
            download != null && download.getOverallDownloadStatus() == Download.COMPLETED;
    }
    return isIndeterminate;
  }

  @NonNull private Download updateDownloadAction(Download download, Download storedDownload) {
    if (storedDownload.getAction() != download.getAction()) {
      storedDownload.setAction(download.getAction());
      downloadRepository.save(storedDownload);
    }
    return storedDownload;
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

  private Observable<InstallationProgress> installInBackground(Context context,
      InstallationProgress progress, boolean forceDefaultInstall) {
    return getInstallationProgress(progress.getMd5(), progress.getPackageName(),
        progress.getVersionCode()).mergeWith(
        startBackgroundInstallationAndWait(context, progress, forceDefaultInstall));
  }

  @NonNull
  private Observable<InstallationProgress> startBackgroundInstallationAndWait(Context context,
      InstallationProgress progress, boolean forceDefaultInstall) {
    return waitBackgroundInstallationResult(context, progress.getMd5()).doOnSubscribe(
        () -> startBackgroundInstallation(context, progress.getMd5(), forceDefaultInstall))
        .map(aVoid -> progress);
  }

  private Observable<Void> waitBackgroundInstallationResult(Context context, String md5) {
    return Observable.create(new BroadcastRegisterOnSubscribe(context,
        new IntentFilter(InstallService.ACTION_INSTALL_FINISHED), null, null))
        .filter(intent -> intent != null && InstallService.ACTION_INSTALL_FINISHED.equals(
            intent.getAction()))
        .first(intent -> md5.equals(intent.getStringExtra(InstallService.EXTRA_INSTALLATION_MD5)))
        .map(intent -> null);
  }

  private void startBackgroundInstallation(Context context, String md5,
      boolean forceDefaultInstall) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_START_INSTALL);
    intent.putExtra(InstallService.EXTRA_INSTALLATION_MD5, md5);
    intent.putExtra(InstallService.EXTRA_FORCE_DEFAULT_INSTALL, forceDefaultInstall);
    if (installer instanceof RollbackInstaller) {
      intent.putExtra(InstallService.EXTRA_INSTALLER_TYPE, InstallService.INSTALLER_TYPE_ROLLBACK);
    } else if (installer instanceof DefaultInstaller) {
      intent.putExtra(InstallService.EXTRA_INSTALLER_TYPE, InstallService.INSTALLER_TYPE_DEFAULT);
    }
    context.startService(intent);
  }

  public boolean showWarning() {
    boolean wasRootDialogShowed = SecurePreferences.isRootDialogShowed();
    boolean isRooted = rootAvailabilityManager.isRootAvailable()
        .toBlocking()
        .value();
    boolean canGiveRoot = ManagerPreferences.allowRootInstallation();
    return isRooted && !wasRootDialogShowed && !canGiveRoot;
  }

  public void rootInstallAllowed(boolean allowRoot) {
    SecurePreferences.setRootDialogShowed(true);
    ManagerPreferences.setAllowRootInstallation(allowRoot);
  }

  public Observable<Boolean> startInstalls(List<Download> downloads, Context context) {
    return Observable.from(downloads)
        .map(download -> install(context, download).toObservable())
        .toList()
        .flatMap(observables -> Observable.merge(observables))
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

  private Observable<InstallationProgress.InstallationType> getInstallationType(String packageName,
      int versionCode) {
    return installedRepository.getInstalled(packageName)
        .map(installed -> {
          if (installed == null) {
            return InstallationProgress.InstallationType.INSTALL;
          } else if (installed.getVersionCode() == versionCode) {
            return InstallationProgress.InstallationType.INSTALLED;
          } else if (installed.getVersionCode() > versionCode) {
            return InstallationProgress.InstallationType.DOWNGRADE;
          } else {
            return InstallationProgress.InstallationType.UPDATE;
          }
        });
  }

  public Completable onUpdateConfirmed(Installed installed) {
    return onAppInstalled(installed);
  }

  public InstallationProgress.Error getError(Download download) {
    InstallationProgress.Error error = InstallationProgress.Error.NO_ERROR;
    if (download != null) {
      switch (download.getDownloadError()) {
        case Download.GENERIC_ERROR:
          error = InstallationProgress.Error.GENERIC_ERROR;
          break;
        case Download.NOT_ENOUGH_SPACE_ERROR:
          error = InstallationProgress.Error.NOT_ENOUGH_SPACE_ERROR;
          break;
        case Download.NO_ERROR:
          error = InstallationProgress.Error.NO_ERROR;
          break;
      }
    }
    return error;
  }

  /**
   * The caller is responsible to make sure that the download exists already
   * this method should only be used when a download exists already(ex: resuming)
   *
   * @return the download object to be resumed or null if doesn't exists
   */
  public Single<Download> getDownload(String md5) {
    return downloadRepository.get(md5)
        .first()
        .toSingle();
  }
}
