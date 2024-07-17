/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.install.installer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.room.RoomFileToDownload;
import cm.aptoide.pt.database.room.RoomInstalled;
import cm.aptoide.pt.install.AppInstallerStatusReceiver;
import cm.aptoide.pt.install.AptoideInstalledAppsRepository;
import cm.aptoide.pt.install.Installer;
import cm.aptoide.pt.install.InstallerAnalytics;
import cm.aptoide.pt.install.RootCommandTimeoutException;
import cm.aptoide.pt.install.RootInstallerProvider;
import cm.aptoide.pt.install.exception.InstallationException;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.packageinstaller.AppInstall;
import cm.aptoide.pt.packageinstaller.AppInstaller;
import cm.aptoide.pt.packageinstaller.InstallStatus;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.root.RootAvailabilityManager;
import cm.aptoide.pt.root.RootShell;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.utils.FileUtils;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by marcelobenites on 7/18/16.
 */
public class DefaultInstaller implements Installer {

  public static final String OBB_FOLDER = Environment.getExternalStorageDirectory()
      .getAbsolutePath() + "/Android/obb/";
  private static final String TAG = DefaultInstaller.class.getSimpleName();
  private final PackageManager packageManager;
  private final InstallationProvider installationProvider;
  private final SharedPreferences sharedPreferences;
  private final AppInstaller appInstaller;
  private final AppInstallerStatusReceiver appInstallerStatusReceiver;
  private final FileUtils fileUtils;
  private final RootAvailabilityManager rootAvailabilityManager;
  private final AptoideInstalledAppsRepository aptoideInstalledAppsRepository;
  private final InstallerAnalytics installerAnalytics;
  private final RootInstallerProvider rootInstallerProvider;
  private final int installingStateTimeout;

  private final Context context;
  private final CompositeSubscription dispatchInstallationsSubscription =
      new CompositeSubscription();
  private final PublishSubject<InstallationCandidate> installCandidateSubject =
      PublishSubject.create();

  public DefaultInstaller(PackageManager packageManager, InstallationProvider installationProvider,
      AppInstaller appInstaller, FileUtils fileUtils, boolean debug,
      AptoideInstalledAppsRepository aptoideInstalledAppsRepository, int rootTimeout,
      RootAvailabilityManager rootAvailabilityManager, SharedPreferences sharedPreferences,
      InstallerAnalytics installerAnalytics, int installingStateTimeout,
      AppInstallerStatusReceiver appInstallerStatusReceiver,
      RootInstallerProvider rootInstallerProvider, Context context) {
    this.packageManager = packageManager;
    this.installationProvider = installationProvider;
    this.appInstaller = appInstaller;
    this.fileUtils = fileUtils;
    this.aptoideInstalledAppsRepository = aptoideInstalledAppsRepository;
    this.installerAnalytics = installerAnalytics;
    this.appInstallerStatusReceiver = appInstallerStatusReceiver;
    this.rootInstallerProvider = rootInstallerProvider;
    RootShell.debugMode = debug;
    RootShell.defaultCommandTimeout = rootTimeout;
    this.rootAvailabilityManager = rootAvailabilityManager;
    this.sharedPreferences = sharedPreferences;
    this.installingStateTimeout = installingStateTimeout;
    this.context = context;
  }

  public PackageManager getPackageManager() {
    return packageManager;
  }

  @Override public synchronized void dispatchInstallations() {
    // Responsible starting the installation process
    dispatchInstallationsSubscription.add(installCandidateSubject
        .flatMap(candidate -> Observable.just(isInstalled(candidate.getInstallation()
                .getPackageName(), candidate.getInstallation()
                .getVersionCode()))
            .onErrorReturn(throwable -> false)
            .first()
            .flatMap(isInstalled -> {
              Installation installation = candidate.getInstallation();
              if (isInstalled) {
                installation.setStatus(RoomInstalled.STATUS_COMPLETED);
                return installation.save()
                    .toObservable()
                    .map(__ -> installation);
              } else {
                if (candidate.getForceDefaultInstall()) {
                  return startDefaultInstallation(context, installation, false);
                } else {
                  return startInstallation(context, installation,
                      candidate.getShouldSetPackageInstaller());
                }
              }
            }))
        .doOnError((throwable) -> CrashReport.getInstance()
            .log(throwable))
        .retry()
        .subscribe(__ -> {
        }, Throwable::printStackTrace));

    //Responsible for moving the obbs when an app is being installed
    dispatchInstallationsSubscription.add(
        installCandidateSubject.map(InstallationCandidate::getInstallation)
            .flatMap(
                installation -> aptoideInstalledAppsRepository.get(installation.getPackageName(),
                        installation.getVersionCode())
                    .filter(installed -> installed.getStatus() == RoomInstalled.STATUS_COMPLETED)
                    .flatMapCompletable(installation1 -> moveInstallationFiles(installation)))
            .subscribe(__ -> {
            }, Throwable::printStackTrace));

    // Responsible for removing installation files when an app is installed
    dispatchInstallationsSubscription.add(
        installCandidateSubject.map(InstallationCandidate::getInstallation)
            .flatMap(
                installation -> aptoideInstalledAppsRepository.get(installation.getPackageName(),
                        installation.getVersionCode())
                    .filter(installed -> installed.getStatus() == RoomInstalled.STATUS_COMPLETED)
                    .map(__ -> installation))
            .doOnNext(this::removeInstallationFiles)
            .doOnError((throwable) -> CrashReport.getInstance()
                .log(throwable))
            .retry()
            .subscribe(__ -> {
            }, Throwable::printStackTrace));
  }

  @Override public Completable install(String md5, boolean forceDefaultInstall,
      boolean shouldSetPackageInstaller) {
    return rootAvailabilityManager.isRootAvailable()
        .doOnSuccess(isRoot -> installerAnalytics.installationType(
            ManagerPreferences.allowRootInstallation(sharedPreferences), isRoot))
        .flatMapObservable(isRoot -> installationProvider.getInstallation(md5)
            .first())
        .observeOn(Schedulers.computation())
        .doOnNext(installation -> {
          installation.setStatus(RoomInstalled.STATUS_INSTALLING);
          installation.setType(RoomInstalled.TYPE_UNKNOWN);
          installCandidateSubject.onNext(
              new InstallationCandidate(installation, forceDefaultInstall,
                  shouldSetPackageInstaller));
        })
        .doOnError(Throwable::printStackTrace)
        .first()
        .toSingle()
        .toCompletable();
  }

  @Override public Completable update(String md5, boolean forceDefaultInstall,
      boolean shouldSetPackageInstaller) {
    return install(md5, forceDefaultInstall, shouldSetPackageInstaller);
  }

  @Override public Completable downgrade(String md5, boolean forceDefaultInstall,
      boolean shouldSetPackageInstaller) {
    return installationProvider.getInstallation(md5)
        .first()
        .flatMapCompletable(installation -> uninstall(installation.getPackageName()))
        .toCompletable()
        .andThen(install(md5, forceDefaultInstall, shouldSetPackageInstaller));
  }

  @Override public Completable uninstall(String packageName) {
    final Uri uri = Uri.fromParts("package", packageName, null);
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
    intentFilter.addDataScheme("package");
    return Observable.<Void>fromCallable(() -> {
          startUninstallIntent(context, packageName, uri);
          return null;
        }).flatMap(uninstallStarted -> waitPackageIntent(context, intentFilter, packageName))
        .toCompletable();
  }

  @Override public Observable<InstallationState> getState(String packageName, int versionCode) {
    return aptoideInstalledAppsRepository.getAsList(packageName, versionCode)
        .map(installed -> {
          if (installed != null) {
            return new InstallationState(installed.getPackageName(), installed.getVersionCode(),
                installed.getVersionName(), installed.getStatus(), installed.getType(),
                installed.getName(), installed.getIcon(), installed.getAppSize());
          } else {
            return new InstallationState(packageName, versionCode, RoomInstalled.STATUS_UNINSTALLED,
                RoomInstalled.TYPE_UNKNOWN);
          }
        })
        .doOnNext(installationState -> Logger.getInstance()
            .d("AptoideDownloadManager", "creating an installation state "
                + installationState.getPackageName()
                + " state is: "
                + installationState.getStatus()))
        .distinctUntilChanged();
  }

  @Override public void stopDispatching() {
    dispatchInstallationsSubscription.clear();
    if (!dispatchInstallationsSubscription.isUnsubscribed()) {
      dispatchInstallationsSubscription.unsubscribe();
    }
  }

  private Observable<Installation> startDefaultInstallation(Context context,
      Installation installation, boolean shouldSetPackageInstaller) {
    return defaultInstall(context, installation, shouldSetPackageInstaller).flatMap(
        installation1 -> installation1.save()
            .andThen(Observable.just(installation1)));
  }

  @NonNull
  private Observable<Installation> startInstallation(Context context, Installation installation,
      boolean shouldSetPackageInstaller) {
    return systemInstall(context, installation).onErrorResumeNext(
            throwable -> rootInstall(installation))
        .onErrorResumeNext(
            throwable -> defaultInstall(context, installation, shouldSetPackageInstaller))
        .doOnError(throwable -> {
              throwable.printStackTrace();
              sendErrorEvent(installation.getPackageName(),
                  installation.getVersionCode(), new InstallationException(
                      "Installation with root failed for "
                          + installation.getPackageName()
                          + ". Error message: "
                          + throwable.getMessage()));
            }
        )
        .flatMap(installation1 -> installation1.save()
            .andThen(Observable.just(installation1)));
  }

  private Observable<Installation> rootInstall(Installation installation) {
    if (ManagerPreferences.allowRootInstallation(sharedPreferences)) {
      return Observable.create(rootInstallerProvider.provideRootInstaller(installation))
          .subscribeOn(Schedulers.computation())
          .map(success -> installation)
          .startWith(updateInstallation(installation, RoomInstalled.TYPE_ROOT,
              RoomInstalled.STATUS_INSTALLING))
          .onErrorResumeNext(throwable -> {
            if (throwable instanceof RootCommandTimeoutException) {
              return updateInstallation(installation, RoomInstalled.TYPE_ROOT,
                  RoomInstalled.STATUS_ROOT_TIMEOUT).save()
                  .toObservable();
            } else {
              return Observable.error(throwable);
            }
          });
    } else {
      return Observable.error(new InstallationException("User doesn't allow root installation"));
    }
  }

  private void startUninstallIntent(Context context, String packageName, Uri uri)
      throws InstallationException {
    try {
      // Check if package is installed first
      packageManager.getPackageInfo(packageName, 0);
      Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, uri);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
    } catch (PackageManager.NameNotFoundException e) {
      CrashReport.getInstance()
          .log(e);
      throw new InstallationException(e);
    }
  }

  private Completable moveInstallationFiles(Installation installation) {
    return Completable.fromAction(() -> {
          boolean filesMoved = false;
          String destinationPath = OBB_FOLDER + installation.getPackageName() + "/";
          fileUtils.deleteDir(new File(destinationPath));
          for (RoomFileToDownload file : installation.getFiles()) {
            if (file.getFileType() == RoomFileToDownload.OBB
                && FileUtils.fileExists(file.getFilePath())
                && !file.getPath()
                .equals(destinationPath)) {
              fileUtils.copyFile(file.getPath(), destinationPath, file.getFileName());
              file.setPath(destinationPath);
              filesMoved = true;
            }
          }
        }
    ).andThen(installation.saveFileChanges()).onErrorComplete();
  }

  private void removeInstallationFiles(Installation installation) {
    for (RoomFileToDownload file : installation.getFiles()) {
      if (file.getFileType() != RoomFileToDownload.OBB) {
        FileUtils.removeFile(file.getFilePath());
        Logger.getInstance()
            .d(TAG, "removing the file " + file.getFilePath() + " " + file.getFileName());
      }
    }
  }

  private Observable<Installation> systemInstall(Context context, Installation installation) {
    if (isSystem(context)) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        return defaultInstall(context, installation, true);
      }
      return Observable.create(new SystemInstallOnSubscribe(context, packageManager,
              Uri.fromFile(installation.getFile())))
          .subscribeOn(Schedulers.computation())
          .map(success -> installation)
          .startWith(updateInstallation(installation, RoomInstalled.TYPE_SYSTEM,
              RoomInstalled.STATUS_INSTALLING));
    }
    return Observable.error(new Throwable());
  }

  private boolean isSystem(Context context) {
    try {
      ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(),
          PackageManager.PERMISSION_GRANTED);
      return (info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
    } catch (PackageManager.NameNotFoundException e) {
      throw new AssertionError("Aptoide application not found by package manager.");
    }
  }

  @NonNull
  private Installation updateInstallation(Installation installation, int type, int status) {
    installation.setType(type);
    installation.setStatus(status);
    return installation;
  }

  private Observable<Installation> defaultInstall(Context context, Installation installation,
      boolean shouldSetPackageInstaller) {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
    intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
    intentFilter.addDataScheme("package");
    return Observable.merge(
            handleInstallationResult(intentFilter, installation, shouldSetPackageInstaller),
            Observable.<Void>fromCallable(() -> {
              AppInstall appInstall = map(installation);
              if (shouldSetPackageInstaller) {
                appInstaller.install(appInstall);
              } else {
                updateInstallation(installation,
                    shouldSetPackageInstaller ? RoomInstalled.TYPE_PACKAGE_INSTALLER
                        : RoomInstalled.TYPE_DEFAULT, RoomInstalled.STATUS_INSTALLING);
                startInstallIntent(context, installation.getFile());
              }
              return null;
            }))
        .subscribeOn(Schedulers.computation())
        .map(success -> installation);
  }

  private Observable<Installation> handleInstallationResult(IntentFilter intentFilter,
      Installation installation, boolean shouldSetPackageInstaller) {
    return Observable.merge(
            waitPackageIntent(context, intentFilter, installation.getPackageName()).timeout(
                installingStateTimeout, TimeUnit.MILLISECONDS, Observable.fromCallable(() -> {
                  if (installation.getStatus() == RoomInstalled.STATUS_INSTALLING) {
                    updateInstallation(installation,
                        shouldSetPackageInstaller ? RoomInstalled.TYPE_PACKAGE_INSTALLER
                            : RoomInstalled.TYPE_DEFAULT, RoomInstalled.STATUS_UNINSTALLED);
                  }
                  return null;
                })), appInstallerStatusReceiver.getInstallerInstallStatus()
                .doOnNext(installStatus -> {
                  if (InstallStatus.Status.CANCELED.equals(installStatus.getStatus())) {
                    installerAnalytics.logInstallCancelEvent(installation.getPackageName(),
                        installation.getVersionCode());
                  }
                })
                .filter(installStatus -> installation.getPackageName()
                    .equalsIgnoreCase(installStatus.getPackageName()))
                .distinctUntilChanged()
                .doOnNext(installStatus -> {
                  Logger.getInstance()
                      .d("Installer", "status: " + installStatus.getStatus()
                          .name() + " " + installation.getPackageName());
                  updateInstallation(installation,
                      shouldSetPackageInstaller ? RoomInstalled.TYPE_PACKAGE_INSTALLER
                          : RoomInstalled.TYPE_DEFAULT, map(installStatus));
                  if (installStatus.getStatus()
                      .equals(InstallStatus.Status.FAIL) && isDeviceMIUI()) {
                    installerAnalytics.sendMiuiInstallResultEvent(InstallStatus.Status.FAIL);
                    startInstallIntent(context, installation.getFile());
                    updateInstallation(installation,
                        shouldSetPackageInstaller ? RoomInstalled.TYPE_PACKAGE_INSTALLER
                            : RoomInstalled.TYPE_DEFAULT, RoomInstalled.STATUS_INSTALLING);
                  } else if (installStatus.getStatus()
                      .equals(InstallStatus.Status.SUCCESS) && isDeviceMIUI()) {
                    installerAnalytics.sendMiuiInstallResultEvent(InstallStatus.Status.SUCCESS);
                  }
                }))
        .map(__ -> installation);
  }

  @NotNull private AppInstall map(Installation installation) {
    AppInstall.InstallBuilder installBuilder = AppInstall.builder()
        .setPackageName(installation.getPackageName())
        .setBaseApk(installation.getFile());
    for (RoomFileToDownload file : installation.getFiles()) {
      if (RoomFileToDownload.SPLIT == file.getFileType()) {
        installBuilder.addApkSplit(new File(file.getFilePath()));
      }
    }
    return installBuilder.build();
  }

  private boolean isDeviceMIUI() {
    return AptoideUtils.isDeviceMIUI();
  }

  private int map(InstallStatus installStatus) {
    switch (installStatus.getStatus()) {
      case INSTALLING:
        return RoomInstalled.STATUS_INSTALLING;
      case SUCCESS:
        return RoomInstalled.STATUS_COMPLETED;
      case WAITING_INSTALL_FEEDBACK:
        return RoomInstalled.STATUS_WAITING_INSTALL_FEEDBACK;
      case FAIL:
      case CANCELED:
      case UNKNOWN_ERROR:
      default:
        return RoomInstalled.STATUS_UNINSTALLED;
    }
  }

  private void sendErrorEvent(String packageName, int versionCode, Exception e) {
    installerAnalytics.logInstallErrorEvent(packageName, versionCode, e);
  }

  private void startInstallIntent(Context context, File file) {
    Intent intent = new Intent(Intent.ACTION_VIEW);

    Uri photoURI = null;
    //read: https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
    if (Build.VERSION.SDK_INT > 23) {
      //content://....apk for nougat
      photoURI =
          FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
    } else {
      //file://....apk for < nougat
      photoURI = Uri.fromFile(file);
    }
    Logger.getInstance()
        .v(TAG, photoURI.toString());

    intent.setDataAndType(photoURI, "application/vnd.android.package-archive");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        | Intent.FLAG_GRANT_READ_URI_PERMISSION
        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    context.startActivity(intent);
  }

  @NonNull private Observable<Void> waitPackageIntent(Context context, IntentFilter intentFilter,
      String packageName) {
    return Observable.create(new BroadcastRegisterOnSubscribe(context, intentFilter, null, null))
        .first(intent -> intent.getData()
            .toString()
            .contains(packageName))
        .map(intent -> null);
  }

  private boolean isInstalled(String packageName, int versionCode) {
    final PackageInfo info;
    try {
      info = packageManager.getPackageInfo(packageName, 0);
      return (info != null && info.versionCode == versionCode);
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }
}
