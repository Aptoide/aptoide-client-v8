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
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.installer.DefaultInstaller;
import cm.aptoide.pt.v8engine.install.installer.RollbackInstaller;
import cm.aptoide.pt.v8engine.install.root.RootShell;
import cm.aptoide.pt.v8engine.repository.Repository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import java.util.List;
import rx.Completable;
import rx.Observable;
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

  public Observable<List<Progress<Download>>> getInstallations() {
    return aptoideDownloadManager.getDownloads()
        .observeOn(Schedulers.io())
        .concatMap(downloadList -> Observable.from(downloadList)
            .flatMap(download -> convertToProgress(download))
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

  public Observable<Progress<Download>> getCurrentInstallation() {
    return getInstallations().flatMap(
        progresses -> Observable.from(progresses).filter(progress -> isInstalling(progress)));
  }

  public boolean isInstalling(Progress<Download> progress) {
    return isDownloading(progress) || (progress.getState() != Progress.DONE
        && progress.getRequest().getOverallDownloadStatus() == Download.COMPLETED);
  }

  public boolean isDownloading(Progress<Download> progress) {
    return progress.getRequest().getOverallDownloadStatus() == Download.PROGRESS;
  }

  public Observable<Progress<Download>> getInstallation(String md5) {
    return aptoideDownloadManager.getDownload(md5).flatMap(download -> convertToProgress(download));
  }

  public Observable<Progress<Download>> getAsListInstallation(String md5) {
    return aptoideDownloadManager.getAsListDownload(md5).flatMap(downloads -> {
      if (downloads.isEmpty()) {
        return Observable.just(null);
      } else {
        return convertToProgress(downloads.get(0));
      }
    });
  }

  public boolean isPending(Progress<Download> progress) {
    return progress.getRequest().getOverallDownloadStatus() == Download.PENDING
        || progress.getRequest().getOverallDownloadStatus() == Download.IN_QUEUE;
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
    return Observable.merge(getDownloadProgress(md5),
        getInstallationProgress(packageName, versioncode));
  }

  private Observable<InstallationProgress> getDownloadProgress(String md5) {
    return downloadRepository.getAsList(md5)
        .map(download -> {
          if (download == null) {
            return new InstallationProgress(0, InstallationProgress.InstallationStatus.UNINSTALLED,
                false, 0);
          } else {
            return new InstallationProgress(download.getOverallProgress(),
                mapDownloadState(download.getOverallDownloadStatus()),
                mapIndeterminate(download.getOverallDownloadStatus()), download.getDownloadSpeed());
          }
        });
  }

  private boolean mapIndeterminate(@Download.DownloadState int overallDownloadStatus) {
    boolean isIndeterminate;
    switch (overallDownloadStatus) {
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
    Logger.d("damnroot", "mapIndeterminate() returned: " + isIndeterminate);
    return isIndeterminate;
  }

  private InstallationProgress.InstallationStatus mapDownloadState(
      @Download.DownloadState int overallDownloadStatus) {
    InstallationProgress.InstallationStatus status =
        InstallationProgress.InstallationStatus.UNINSTALLED;
    switch (overallDownloadStatus) {
      case Download.BLOCK_COMPLETE:
      case Download.COMPLETED:
      case Download.CONNECTED:
      case Download.FILE_MISSING:
      case Download.INVALID_STATUS:
      case Download.NOT_DOWNLOADED:
      case Download.PAUSED:
      case Download.RETRY:
      case Download.STARTED:
      case Download.WARN:
        status = InstallationProgress.InstallationStatus.UNINSTALLED;
        break;
      case Download.ERROR:
        status = InstallationProgress.InstallationStatus.FAILED;
        break;
      case Download.PROGRESS:
      case Download.IN_QUEUE:
      case Download.PENDING:
        status = InstallationProgress .InstallationStatus.INSTALLING;
        break;
    }
    return status;
  }

  private Observable<InstallationProgress> getInstallationProgress(String packageName,
      int versionCode) {
    return installer.getState(packageName, versionCode)
        .map(installationState -> new InstallationProgress(100,
            mapInstallationState(installationState.getStatus(), installationState.getType()),
            mapInstallIndeterminate(installationState.getStatus(), installationState.getType()), -1));
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

  private InstallationProgress.InstallationStatus mapInstallationState(int status, int type) {
    InstallationProgress.InstallationStatus installationStatus =
        InstallationProgress.InstallationStatus.UNINSTALLED;
    switch (status) {
      case Installed.STATUS_UNINSTALLED:
        installationStatus = InstallationProgress.InstallationStatus.UNINSTALLED;
        break;
      case Installed.STATUS_INSTALLING:
        if (type == Installed.TYPE_DEFAULT) {
          installationStatus = InstallationProgress.InstallationStatus.INSTALLED;
        } else {
          installationStatus = InstallationProgress.InstallationStatus.INSTALLING;
        }
        break;
      case Installed.STATUS_COMPLETED:
        installationStatus = InstallationProgress.InstallationStatus.INSTALLED;
        break;
    }
    Logger.d("damnroot", "mapInstallationState() returned: " + installationStatus);
    return installationStatus;
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
    //AN-1533 - temporary solution was to remove root installation, so this popup doesn't make sense
    return false;
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
}
