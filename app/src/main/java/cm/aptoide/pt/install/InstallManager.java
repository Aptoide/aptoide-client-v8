/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 04/10/2016.
 */

package cm.aptoide.pt.install;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadNotFoundException;
import cm.aptoide.pt.downloadmanager.DownloadsRepository;
import cm.aptoide.pt.install.installer.DefaultInstaller;
import cm.aptoide.pt.install.installer.InstallationState;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.utils.FileUtils;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
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
  private final SharedPreferences sharedPreferences;
  private final SharedPreferences securePreferences;
  private final String cachePath;
  private final String apkPath;
  private final String obbPath;
  private final FileUtils fileUtils;
  private final Context context;
  @Inject DownloadsRepository downloadRepository;
  @Inject InstalledRepository installedRepository;
  private RootAvailabilityManager rootAvailabilityManager;

  public InstallManager(Context context, AptoideDownloadManager aptoideDownloadManager,
      Installer installer, RootAvailabilityManager rootAvailabilityManager,
      SharedPreferences sharedPreferences, SharedPreferences securePreferences,
      DownloadsRepository downloadRepository, InstalledRepository installedRepository,
      String cachePath, String apkPath, String obbPath, FileUtils fileUtils) {
    this.aptoideDownloadManager = aptoideDownloadManager;
    this.installer = installer;
    this.context = context;
    this.rootAvailabilityManager = rootAvailabilityManager;
    this.downloadRepository = downloadRepository;
    this.installedRepository = installedRepository;
    this.sharedPreferences = sharedPreferences;
    this.securePreferences = securePreferences;
    this.cachePath = cachePath;
    this.apkPath = apkPath;
    this.obbPath = obbPath;
    this.fileUtils = fileUtils;
  }

  public void stopAllInstallations() {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_STOP_ALL_INSTALLS);
    context.startService(intent);
  }

  public void removeInstallationFile(String md5, String packageName, int versionCode) {
    stopInstallation(md5);
    installedRepository.remove(packageName, versionCode)
        .andThen(aptoideDownloadManager.removeDownload(md5))
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

  public Observable<List<Install>> getTimedOutInstallations() {
    return getInstallations().flatMap(installs -> Observable.from(installs)
        .filter(install -> install.getState()
            .equals(Install.InstallationStatus.INSTALLATION_TIMEOUT))
        .toList());
  }

  public Observable<List<Install>> getInstalledApps() {
    return installedRepository.getAllInstalled()
        .concatMap(downloadList -> Observable.from(downloadList)
            .flatMap(download -> getInstall(download.getPackageName(),
                download.getVersionCode()).first())
            .toList());
  }

  private Observable<Install> getInstall(String packageName, int versionCode) {
    return installedRepository.get(packageName, versionCode)
        .map(installed -> new Install(100, Install.InstallationStatus.INSTALLED,
            Install.InstallationType.INSTALLED, false, -1, null, installed.getPackageName(),
            installed.getVersionCode(), installed.getVersionName(), installed.getName(),
            installed.getIcon()));
  }

  public Observable<List<Install>> getInstallations() {
    return Observable.combineLatest(aptoideDownloadManager.getDownloadsList(),
        installedRepository.getAllInstalled(), (downloads, installeds) -> downloads)
        .observeOn(Schedulers.io())
        .concatMap(downloadList -> Observable.from(downloadList)
            .flatMap(download -> getInstall(download.getMd5(), download.getPackageName(),
                download.getVersionCode()).first())
            .toList())
        .distinctUntilChanged()
        .map(installs -> sortList(installs));
  }

  private List<Install> sortList(List<Install> installs) {
    Collections.sort(installs, (install, t1) -> {
      int toReturn;
      if (install.getState() == Install.InstallationStatus.DOWNLOADING
          && !install.isIndeterminate()) {
        toReturn = 1;
      } else if (t1.getState() == Install.InstallationStatus.DOWNLOADING && !t1.isIndeterminate()) {
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
        .filter(install -> install.getState() == Install.InstallationStatus.DOWNLOADING));
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
        .doOnNext(storedDownload -> {
          if (storedDownload.getOverallDownloadStatus() == Download.ERROR) {
            storedDownload.setOverallDownloadStatus(Download.INVALID_STATUS);
            downloadRepository.save(storedDownload);
            Logger.getInstance()
                .d("install.installmanager.install",
                    "save " + "status:" + storedDownload.getOverallDownloadStatus());
          }
        })
        .flatMap(storedDownload -> getInstall(download.getMd5(), download.getPackageName(),
            download.getVersionCode()))
        .flatMap(install -> installInBackground(install, forceDefaultInstall, download.hasAppc()))
        .first()
        .toCompletable();
  }

  public Observable<Install> getInstall(String md5, String packageName, int versioncode) {
    return Observable.combineLatest(aptoideDownloadManager.getDownloadsByMd5(md5),
        installer.getState(packageName, versioncode), getInstallationType(packageName, versioncode),
        (download, installationState, installationType) -> createInstall(download,
            installationState, md5, packageName, versioncode, installationType));
  }

  private Install createInstall(Download download, InstallationState installationState, String md5,
      String packageName, int versioncode, Install.InstallationType installationType) {
    return new Install(mapInstallation(download),
        mapInstallationStatus(download, installationState), installationType,
        mapIndeterminateState(download, installationState), getSpeed(download), md5, packageName,
        versioncode, getVersionName(download, installationState),
        getAppName(download, installationState), getAppIcon(download, installationState));
  }

  private String getVersionName(Download download, InstallationState installationState) {
    if (download != null) {
      return download.getVersionName();
    } else {
      return installationState.getVersionName();
    }
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
      return Install.InstallationStatus.DOWNLOADING;
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
        case Download.INVALID_STATUS:
          status = Install.InstallationStatus.INITIAL_STATE;
          break;
        case Download.FILE_MISSING:
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
          status = Install.InstallationStatus.DOWNLOADING;
          break;
        case Download.IN_QUEUE:
          status = Install.InstallationStatus.IN_QUEUE;
          break;
      }
    }
    if (download != null) {
      Logger.getInstance()
          .d("install.installmanager.mapDownloadState",
              " " + status + " true downloadstatus: " + download.getOverallDownloadStatus());
    } else {
      Logger.getInstance()
          .d("install.installmanager.mapDownloadState", " " + status + " false downloadstatus: ");
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
    if (download != null && download.getOverallDownloadStatus() == Download.INVALID_STATUS) {
      isIndeterminate = true;
    }
    return isIndeterminate;
  }

  @NonNull private Download updateDownloadAction(Download download, Download storedDownload) {
    if (storedDownload.getAction() != download.getAction()) {
      storedDownload.setAction(download.getAction());
      downloadRepository.save(storedDownload);
      Logger.getInstance()
          .d("install.installmanager.updateDownloadAction",
              "save " + "status:" + storedDownload.getOverallDownloadStatus());
    }
    return storedDownload;
  }

  private Observable<Throwable> createDownloadAndRetry(Observable<? extends Throwable> errors,
      Download download) {
    return errors.flatMap(throwable -> {
      if (throwable instanceof DownloadNotFoundException) {
        downloadRepository.save(download);
        Logger.getInstance()
            .d("install.installmanager.createDownloadAndRetry",
                "save " + "status:" + download.getOverallDownloadStatus());
        return Observable.just(throwable);
      } else {
        return Observable.error(throwable);
      }
    });
  }

  private Observable<Install> installInBackground(Install install, boolean forceDefaultInstall,
      boolean shouldSetPackageInstaller) {
    return getInstall(install.getMd5(), install.getPackageName(),
        install.getVersionCode()).mergeWith(
        startBackgroundInstallationAndWait(install, forceDefaultInstall,
            shouldSetPackageInstaller));
  }

  @NonNull private Observable<Install> startBackgroundInstallationAndWait(Install install,
      boolean forceDefaultInstall, boolean shouldSetPackageInstaller) {
    return waitBackgroundInstallationResult(install.getMd5()).doOnSubscribe(
        () -> startBackgroundInstallation(install.getMd5(), forceDefaultInstall,
            shouldSetPackageInstaller))
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

  private void startBackgroundInstallation(String md5, boolean forceDefaultInstall,
      boolean shouldSetPackageInstaller) {
    Intent intent = new Intent(context, InstallService.class);
    intent.setAction(InstallService.ACTION_START_INSTALL);
    intent.putExtra(InstallService.EXTRA_INSTALLATION_MD5, md5);
    intent.putExtra(InstallService.EXTRA_FORCE_DEFAULT_INSTALL, forceDefaultInstall);
    intent.putExtra(InstallService.EXTRA_SET_PACKAGE_INSTALLER, shouldSetPackageInstaller);
    if (installer instanceof DefaultInstaller) {
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
    return downloadRepository.getDownload(md5)
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

  public Observable<List<Installed>> fetchInstalled() {
    return installedRepository.getAllInstalledSorted()
        .first()
        .flatMapIterable(list -> list)
        .filter(item -> !item.isSystemApp())
        .toList();
  }

  public Observable<Boolean> isInstalled(String packageName) {
    return Observable.just(installedRepository.contains(packageName));
  }

  public Observable<Install> filterInstalled(Install item) {
    return Observable.just(installedRepository.contains(item.getPackageName()))
        .flatMap(isInstalled -> {
          if (isInstalled) {
            return Observable.empty();
          }
          return Observable.just(item);
        });
  }

  public Observable<Install> filterNonInstalled(Install item) {
    return Observable.just(installedRepository.contains(item.getPackageName()))
        .flatMap(isInstalled -> {
          if (isInstalled) {
            return Observable.just(item);
          } else {
            return Observable.empty();
          }
        });
  }

  public boolean wasAppEverInstalled(String packageName) {
    return installedRepository.getInstallationsHistory()
        .first()
        .flatMapIterable(installation -> installation)
        .filter(installation -> packageName.equals(installation.getPackageName()))
        .toList()
        .flatMap(installations -> {
          if (installations.isEmpty()) {
            return Observable.just(Boolean.FALSE);
          } else {
            return Observable.just(Boolean.TRUE);
          }
        })
        .toBlocking()
        .first();
  }

  public void moveCompletedDownloadFiles(Download download) {
    for (final FileToDownload fileToDownload : download.getFilesToDownload()) {
      Logger.getInstance()
          .d("AptoideDownloadManager", "trying to move file : "
              + fileToDownload.getFileName()
              + " "
              + fileToDownload.getPackageName());
      String newFilePath = getFilePathFromFileType(fileToDownload);
      fileUtils.copyFile(fileToDownload.getPath(), newFilePath, fileToDownload.getFileName());
      fileToDownload.setPath(newFilePath);
    }
    downloadRepository.save(download);
    Logger.getInstance()
        .d("install.installmanager.moveCompletedDownloadFiles",
            "save " + "status:" + download.getOverallDownloadStatus());
  }

  @NonNull private String getFilePathFromFileType(FileToDownload fileToDownload) {
    String path;
    switch (fileToDownload.getFileType()) {
      case FileToDownload.APK:
        path = apkPath;
        break;
      case FileToDownload.OBB:
        path = obbPath + fileToDownload.getPackageName() + "/";
        break;
      case FileToDownload.GENERIC:
      default:
        path = cachePath;
        break;
    }
    return path;
  }
}
