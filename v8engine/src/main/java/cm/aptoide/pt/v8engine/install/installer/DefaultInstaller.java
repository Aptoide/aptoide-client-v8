/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.v8engine.install.installer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.root.RootShell;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.download.InstallEvent;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.RootAvailabilityManager;
import cm.aptoide.pt.v8engine.install.exception.InstallationException;
import java.io.File;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 7/18/16.
 */
public class DefaultInstaller implements Installer {

  public static final String OBB_FOLDER = Environment.getExternalStorageDirectory()
      .getAbsolutePath() + "/Android/obb/";
  public static final String ROOT_INSTALL_COMMAND = "pm install -r ";
  private static final String TAG = DefaultInstaller.class.getSimpleName();
  @Getter(AccessLevel.PACKAGE) private final PackageManager packageManager;
  private final InstallationProvider installationProvider;
  private FileUtils fileUtils;
  private Analytics analytics;
  private RootAvailabilityManager rootAvailabilityManager;
  private InstalledRepository installedRepository;

  public DefaultInstaller(PackageManager packageManager, InstallationProvider installationProvider,
      FileUtils fileUtils, Analytics analytics, boolean debug,
      InstalledRepository installedRepository, int rootTimeout,
      RootAvailabilityManager rootAvailabilityManager) {
    this.packageManager = packageManager;
    this.installationProvider = installationProvider;
    this.fileUtils = fileUtils;
    this.analytics = analytics;
    this.installedRepository = installedRepository;
    RootShell.debugMode = debug;
    RootShell.defaultCommandTimeout = rootTimeout;
    this.rootAvailabilityManager = rootAvailabilityManager;
  }

  @Override public Observable<Boolean> isInstalled(String md5) {
    return installationProvider.getInstallation(md5)
        .map(installation -> isInstalled(installation.getPackageName(),
            installation.getVersionCode()))
        .onErrorReturn(throwable -> false);
  }

  @Override public Completable install(Context context, String md5) {

    return rootAvailabilityManager.isRootAvailable()
        .doOnSuccess(isRoot -> Analytics.RootInstall.installationType(
            ManagerPreferences.allowRootInstallation(), isRoot))
        .flatMapObservable(isRoot -> installationProvider.getInstallation(md5)
            .first())
        .observeOn(Schedulers.computation())
        .doOnNext(installation -> {
          installation.setStatus(Installed.STATUS_INSTALLING);
          installation.setType(Installed.TYPE_UNKNOWN);
          moveInstallationFiles(installation);
        })
        .flatMap(installation -> isInstalled(md5).first()
            .flatMap(isInstalling -> {
              if (isInstalling) {
                return Observable.just(null);
              } else {
                return startInstallation(context, installation);
              }
            }))
        .doOnError((throwable) -> CrashReport.getInstance()
            .log(throwable))
        .toCompletable();
  }

  @Override public Completable update(Context context, String md5) {
    return install(context, md5);
  }

  @Override public Completable downgrade(Context context, String md5) {
    return installationProvider.getInstallation(md5)
        .first()
        .flatMapCompletable(installation -> uninstall(context, installation.getPackageName(),
            installation.getVersionName()))
        .toCompletable()
        .andThen(install(context, md5));
  }

  @Override public Completable uninstall(Context context, String packageName, String versionName) {
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
    return installedRepository.getAsList(packageName, versionCode)
        .map(installed -> {
          if (installed != null) {
            return new InstallationState(installed.getPackageName(), installed.getVersionCode(),
                installed.getStatus(), installed.getType(), installed.getName(),
                installed.getIcon());
          } else {
            return new InstallationState(packageName, versionCode, Installed.STATUS_UNINSTALLED,
                Installed.TYPE_UNKNOWN);
          }
        });
  }

  @NonNull
  private Observable<Installation> startInstallation(Context context, Installation installation) {
    return systemInstall(context, installation).onErrorResumeNext(
        throwable -> rootInstall(installation))
        .onErrorResumeNext(throwable -> defaultInstall(context, installation))
        .doOnNext(installation1 -> installation1.save());
  }

  private Observable<Installation> rootInstall(Installation installation) {
    if (ManagerPreferences.allowRootInstallation()) {
      return Observable.create(new RootCommandOnSubscribe(installation.getId()
          .hashCode(), ROOT_INSTALL_COMMAND + installation.getFile()
          .getAbsolutePath()))
          .subscribeOn(Schedulers.computation())
          .map(success -> installation)
          .startWith(
              updateInstallation(installation, Installed.TYPE_ROOT, Installed.STATUS_INSTALLING));
    } else {
      return Observable.error(new InstallationException("User doesn't allow root installation"));
    }
  }

  private void startUninstallIntent(Context context, String packageName, Uri uri)
      throws InstallationException {
    try {
      // Check if package is installed first
      packageManager.getPackageInfo(packageName, 0);
      Intent intent = new Intent(Intent.ACTION_DELETE, uri);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
    } catch (PackageManager.NameNotFoundException e) {
      CrashReport.getInstance()
          .log(e);
      throw new InstallationException(e);
    }
  }

  private void moveInstallationFiles(RollbackInstallation installation) {
    List<FileToDownload> files = installation.getFiles();
    for (int i = 0; i < files.size(); i++) {
      FileToDownload file = files.get(i);
      if (file != null && file.getFileType() == FileToDownload.OBB) {
        String newPath = OBB_FOLDER + installation.getPackageName() + "/";
        fileUtils.copyFile(file.getPath(), newPath, file.getFileName());
        FileUtils.removeFile(file.getPath());
        file.setPath(newPath);
      }
    }
    installation.saveFileChanges();
  }

  private Observable<Installation> systemInstall(Context context, Installation installation) {
    return Observable.create(
        new SystemInstallOnSubscribe(context, packageManager, Uri.fromFile(installation.getFile())))
        .subscribeOn(Schedulers.computation())
        .map(success -> installation)
        .startWith(
            updateInstallation(installation, Installed.TYPE_SYSTEM, Installed.STATUS_INSTALLING));
  }

  @NonNull
  private Installation updateInstallation(Installation installation, int type, int status) {
    installation.setType(type);
    installation.setStatus(status);
    return installation;
  }

  private Observable<Installation> defaultInstall(Context context, Installation installation) {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
    intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
    intentFilter.addDataScheme("package");
    return Observable.<Void>fromCallable(() -> {
      startInstallIntent(context, installation.getFile());
      return null;
    }).subscribeOn(Schedulers.computation())
        .flatMap(installStarted -> waitPackageIntent(context, intentFilter,
            installation.getPackageName()))
        .map(success -> installation)
        .startWith(
            updateInstallation(installation, Installed.TYPE_DEFAULT, Installed.STATUS_INSTALLING));
  }

  private void sendErrorEvent(String packageName, int versionCode, Exception e) {
    InstallEvent report =
        (InstallEvent) analytics.get(packageName + versionCode, InstallEvent.class);
    if (report != null) {
      report.setResultStatus(DownloadInstallAnalyticsBaseBody.ResultStatus.FAIL);
      report.setError(e);
      analytics.sendEvent(report);
    }
  }

  private void startInstallIntent(Context context, File file) {
    Intent intent = new Intent(Intent.ACTION_VIEW);

    Uri photoURI = null;
    //read: https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
    if (Build.VERSION.SDK_INT > 23) {
      //content://....apk for nougat
      photoURI = FileProvider.getUriForFile(context, V8Engine.getConfiguration()
          .getAppId() + ".provider", file);
    } else {
      //file://....apk for < nougat
      photoURI = Uri.fromFile(file);
    }
    Logger.v(TAG, photoURI.toString());

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
      CrashReport.getInstance()
          .log(e);
      return false;
    }
  }
}
