/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 04/10/2016.
 */

package cm.aptoide.pt.v8engine;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
  private final SharedPreferences sharedPreferences;
  private final SharedPreferences securePreferences;
  private final Context context;
  private RootAvailabilityManager rootAvailabilityManager;

  public InstallManager(Context context, AptoideDownloadManager aptoideDownloadManager,
      Installer installer, RootAvailabilityManager rootAvailabilityManager,
      SharedPreferences sharedPreferences, SharedPreferences securePreferences,
      DownloadRepository downloadRepository, InstalledRepository installedRepository) {
    this.aptoideDownloadManager = aptoideDownloadManager;
    this.installer = installer;
    this.context = context;
    this.rootAvailabilityManager = rootAvailabilityManager;
    this.downloadRepository = downloadRepository;
    this.installedRepository = installedRepository;
    this.sharedPreferences = sharedPreferences;
    this.securePreferences = securePreferences;
  }

  public void stopAllInstallations() {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_STOP_ALL_INSTALLS);
    context.startService(intent);
  }

  public void removeInstallationFile(String md5, String packageName, int versionCode) {
    stopInstallation(md5);
    installedRepository.remove(packageName, versionCode)
        .andThen(Completable.fromAction(() -> aptoideDownloadManager.removeDownload(md5)))
        .subscribe(() -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable));
  }

  public void stopInstallation(String md5) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_STOP_INSTALL);
    intent.putExtra(InstallService.EXTRA_INSTALLATION_MD5, md5);
    context.startService(intent);
  }

  public Completable uninstall(String packageName, String versionName) {
    return installer.uninstall(context, packageName, versionName);
  }

  public Observable<List<Install>> getTimedOutInstallations() {
    return getInstallations().flatMap(installs -> Observable.from(installs)
        .filter(install -> install.getState()
            .equals(Install.InstallationStatus.INSTALLATION_TIMEOUT))
        .toList());
  }

  public Observable<List<Install>> getInstallations() {
    return Observable.combineLatest(aptoideDownloadManager.getDownloads(),
        installedRepository.getAllInstalled(), (downloads, installeds) -> downloads)
        .observeOn(Schedulers.io())
        .concatMap(downloadList -> Observable.from(downloadList)
            .flatMap(download -> getInstall(download.getMd5(), download.getPackageName(),
                download.getVersionCode()).first())
            .toList())
        .map(installs -> sortList(installs));
  }

  private List<Install> sortList(List<Install> installs) {
    Collections.sort(installs, (install, t1) -> {
      int toReturn;
      if (install.getState() == Install.InstallationStatus.INSTALLING
          && !install.isIndeterminate()) {
        toReturn = 1;
      } else if (t1.getState() == Install.InstallationStatus.INSTALLING && !t1.isIndeterminate()) {
        toReturn = -1;
      } else {
        int diff = install.getState()
            .ordinal() - t1.getState()
            .ordinal();
        if (diff == 0) {
          toReturn = install.getPackageName()
              .compareTo(t1.getPackageName());
        } else {
          toReturn = diff;
        }
      }
      return toReturn;
    });
    Collections.reverse(installs);
    return installs;
  }

  public Observable<Install> getCurrentInstallation() {
    return getInstallations().flatMap(installs -> Observable.from(installs)
        .filter(install -> install.getState() == Install.InstallationStatus.INSTALLING));
  }

  public Completable install(Download download) {
    return install(download, false);
  }

  public Completable defaultInstall(Download download) {
    return install(download, true);
  }

  public Completable install(Download download, boolean forceDefaultInstall) {
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
        .flatMap(download1 -> getInstall(download.getMd5(), download.getPackageName(),
            download.getVersionCode()))
        .flatMap(install -> installInBackground(install, forceDefaultInstall))
        .first()
        .toCompletable();
  }

  public Observable<Install> getInstall(String md5, String packageName, int versioncode) {
    return Observable.combineLatest(aptoideDownloadManager.getAsListDownload(md5),
        installer.getState(packageName, versioncode), getInstallationType(packageName, versioncode),
        (download, installationState, installationType) -> createInstall(download,
            installationState, md5, packageName, versioncode, installationType));
  }

  private Install createInstall(Download download, InstallationState installationState, String md5,
      String packageName, int versioncode, Install.InstallationType installationType) {
    return new Install(mapInstallation(download),
        mapInstallationStatus(download, installationState), installationType,
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
        installationState.getType(), download);
  }

  private Install.InstallationStatus mapInstallationStatus(Download download,
      InstallationState installationState) {

    if (installationState.getStatus() == Installed.STATUS_COMPLETED) {
      return Install.InstallationStatus.INSTALLED;
    }

    if (installationState.getStatus() == Installed.STATUS_INSTALLING
        && installationState.getType() != Installed.TYPE_DEFAULT) {
      return Install.InstallationStatus.INSTALLING;
    }

    if (installationState.getStatus() == Installed.STATUS_WAITING
        && download != null
        && download.getOverallDownloadStatus() == Download.COMPLETED) {
      return Install.InstallationStatus.INSTALLING;
    }

    if (installationState.getStatus() == Installed.STATUS_ROOT_TIMEOUT) {
      return Install.InstallationStatus.INSTALLATION_TIMEOUT;
    }

    return mapDownloadState(download);
  }

  private int mapInstallation(Download download) {
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

  private Install.InstallationStatus mapDownloadState(Download download) {
    Install.InstallationStatus status = Install.InstallationStatus.UNINSTALLED;
    if (download != null) {
      switch (download.getOverallDownloadStatus()) {
        case Download.FILE_MISSING:
        case Download.INVALID_STATUS:
        case Download.NOT_DOWNLOADED:
        case Download.COMPLETED:
          status = Install.InstallationStatus.UNINSTALLED;
          break;
        case Download.PAUSED:
          status = Install.InstallationStatus.PAUSED;
          break;
        case Download.ERROR:
          switch (download.getDownloadError()) {
            case Download.GENERIC_ERROR:
              status = Install.InstallationStatus.GENERIC_ERROR;
              break;
            case Download.NOT_ENOUGH_SPACE_ERROR:
              status = Install.InstallationStatus.NOT_ENOUGH_SPACE_ERROR;
              break;
          }
          break;
        case Download.RETRY:
        case Download.STARTED:
        case Download.WARN:
        case Download.CONNECTED:
        case Download.BLOCK_COMPLETE:
        case Download.PROGRESS:
        case Download.PENDING:
          status = Install.InstallationStatus.INSTALLING;
          break;
        case Download.IN_QUEUE:
          status = Install.InstallationStatus.IN_QUEUE;
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
        isIndeterminate = type != Installed.TYPE_DEFAULT;
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

  private Observable<Install> installInBackground(Install install, boolean forceDefaultInstall) {
    return getInstall(install.getMd5(), install.getPackageName(),
        install.getVersionCode()).mergeWith(
        startBackgroundInstallationAndWait(install, forceDefaultInstall));
  }

  @NonNull private Observable<Install> startBackgroundInstallationAndWait(Install install,
      boolean forceDefaultInstall) {
    return waitBackgroundInstallationResult(install.getMd5()).doOnSubscribe(
        () -> startBackgroundInstallation(install.getMd5(), forceDefaultInstall))
        .map(aVoid -> install);
  }

  private Observable<Void> waitBackgroundInstallationResult(String md5) {
    return Observable.create(new BroadcastRegisterOnSubscribe(context,
        new IntentFilter(InstallService.ACTION_INSTALL_FINISHED), null, null))
        .filter(intent -> intent != null && InstallService.ACTION_INSTALL_FINISHED.equals(
            intent.getAction()))
        .first(intent -> md5.equals(intent.getStringExtra(InstallService.EXTRA_INSTALLATION_MD5)))
        .map(intent -> null);
  }

  private void startBackgroundInstallation(String md5, boolean forceDefaultInstall) {
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
    boolean wasRootDialogShowed = SecurePreferences.isRootDialogShowed(securePreferences);
    boolean isRooted = rootAvailabilityManager.isRootAvailable()
        .toBlocking()
        .value();
    boolean canGiveRoot = ManagerPreferences.allowRootInstallation(securePreferences);
    return isRooted && !wasRootDialogShowed && !canGiveRoot;
  }

  public void rootInstallAllowed(boolean allowRoot) {
    SecurePreferences.setRootDialogShowed(true, securePreferences);
    ManagerPreferences.setAllowRootInstallation(allowRoot, sharedPreferences);
  }

  public Observable<Boolean> startInstalls(List<Download> downloads) {
    return Observable.from(downloads)
        .map(download -> install(download).toObservable())
        .toList()
        .flatMap(observables -> Observable.merge(observables))
        .toList()
        .map(installs -> true)
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

  private Observable<Install.InstallationType> getInstallationType(String packageName,
      int versionCode) {
    return installedRepository.getInstalled(packageName)
        .map(installed -> {
          if (installed == null) {
            return Install.InstallationType.INSTALL;
          } else if (installed.getVersionCode() == versionCode) {
            return Install.InstallationType.INSTALLED;
          } else if (installed.getVersionCode() > versionCode) {
            return Install.InstallationType.DOWNGRADE;
          } else {
            return Install.InstallationType.UPDATE;
          }
        });
  }

  public Completable onUpdateConfirmed(Installed installed) {
    return onAppInstalled(installed);
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

  public Completable retryTimedOutInstallations() {
    return getTimedOutInstallations().first()
        .flatMapIterable(installs -> installs)
        .flatMapSingle(install -> getDownload(install.getMd5()))
        .flatMapCompletable(download -> defaultInstall(download))
        .toCompletable();
  }

  public Completable cleanTimedOutInstalls() {
    return getTimedOutInstallations().first()
        .flatMap(installs -> Observable.from(installs)
            .flatMap(install -> installedRepository.get(install.getPackageName(),
                install.getVersionCode())
                .first()
                .doOnNext(installed -> {
                  installed.setStatus(Installed.STATUS_UNINSTALLED);
                  installedRepository.save(installed);
                })))
        .toList()
        .toCompletable();
  }
}
