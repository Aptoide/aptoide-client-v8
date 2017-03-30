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
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.InstallEvent;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.exception.InstallationException;
import eu.chainfire.libsuperuser.Shell;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 7/18/16.
 */
public class DefaultInstaller implements Installer {

  public static final String OBB_FOLDER =
      Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/obb/";
  private static final String TAG = DefaultInstaller.class.getSimpleName();
  @Getter(AccessLevel.PACKAGE) private final PackageManager packageManager;
  private final InstallationProvider installationProvider;
  private FileUtils fileUtils;
  private Analytics analytics;

  public DefaultInstaller(PackageManager packageManager, InstallationProvider installationProvider,
      FileUtils fileUtils, Analytics analytics) {
    this.packageManager = packageManager;
    this.installationProvider = installationProvider;
    this.fileUtils = fileUtils;
    this.analytics = analytics;
  }

  @Override public Observable<Boolean> isInstalled(String md5) {
    return installationProvider.getInstallation(md5)
        .map(installation -> isInstalled(installation.getPackageName(),
            installation.getVersionCode()))
        .onErrorReturn(throwable -> false);
  }

  @Override public Observable<Void> install(Context context, String md5) {
    Analytics.RootInstall.installationType(ManagerPreferences.allowRootInstallation(),
        AptoideUtils.SystemU.isRooted());
    return installationProvider.getInstallation(md5)
        .observeOn(Schedulers.computation())
        .doOnNext(installation -> moveInstallationFiles(installation))
        .flatMap(installation -> {
          if (isInstalled(installation.getPackageName(), installation.getVersionCode())) {
            return Observable.just(null);
          } else {
            return systemInstall(context, installation.getFile()).onErrorResumeNext(
                Observable.fromCallable(
                    () -> rootInstall(installation.getFile(), installation.getPackageName(),
                        installation.getVersionCode())))
                .onErrorResumeNext(
                    defaultInstall(context, installation.getFile(), installation.getPackageName()));
          }
        })
        .doOnError((throwable) -> {
          CrashReport.getInstance().log(throwable);
        });
  }

  @Override public Observable<Void> update(Context context, String md5) {
    return install(context, md5);
  }

  @Override public Observable<Void> downgrade(Context context, String md5) {
    return installationProvider.getInstallation(md5)
        .first()
        .flatMap(installation -> uninstall(context, installation.getPackageName(),
            installation.getVersionName()))
        .flatMap(success -> install(context, md5));
  }

  @Override
  public Observable<Void> uninstall(Context context, String packageName, String versionName) {
    final Uri uri = Uri.fromParts("package", packageName, null);
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
    intentFilter.addDataScheme("package");
    return Observable.<Void> fromCallable(() -> {
      startUninstallIntent(context, packageName, uri);
      return null;
    }).flatMap(uninstallStarted -> waitPackageIntent(context, intentFilter, packageName));
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
      CrashReport.getInstance().log(e);
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
    installation.save();
  }

  private Observable<Void> systemInstall(Context context, File file) {
    return Observable.create(
        new SystemInstallOnSubscribe(context, packageManager, Uri.fromFile(file)));
  }

  private Void rootInstall(File file, String packageName, int versionCode)
      throws InstallationException {
    if (!AptoideUtils.SystemU.isRooted()) {
      throw new InstallationException("No root permissions");
    } else if (!ManagerPreferences.allowRootInstallation()) {
      throw new InstallationException("User doesn't allow root installation");
    }

    try {
      //if (Shell.SU.available()) {

      Shell.Builder shellBuilder = new Shell.Builder();
      Shell.Interactive interactiveShell = shellBuilder.useSU().setWatchdogTimeout(10) // seconds
          .addCommand("pm install -r " + file.getAbsolutePath(), 0,
              (commandCode, exitCode, output) -> {
                CrashReport.getInstance().log(new Exception("install -r exitCode: " + exitCode));
                Observable.fromCallable(() -> exitCode)
                    .observeOn(Schedulers.computation())
                    .delay(20, TimeUnit.SECONDS)
                    .subscribe(exitCodeToSend -> {
                      boolean installed = isInstalled(packageName, versionCode);
                      if (!installed) {
                        sendErrorEvent(packageName, versionCode,
                            new Exception("Root install not succeeded. Exit code = " + exitCode));
                      }
                      Analytics.RootInstall.rootInstallCompleted(exitCodeToSend, installed);
                    });
                if (exitCode == 0) {
                  Logger.v(TAG, "app successfully installed using root");
                } else {
                  Logger.e(TAG, "Error using su to install package " + packageName);
                  for (String s : output) {
                    Logger.e(TAG, "su command result: " + s);
                  }
                }
              }).open();

      interactiveShell.waitForIdle();

      //if (!isInstalled(packageName, versionCode)) {
      //  throw new RuntimeException("Could not verify installation.");
      //}

      // app sucessfully installed using root
      return null;
      //} else {
      //  throw new RuntimeException("Device not rooted.");
      //}
    } catch (Exception e) {
      CrashReport.getInstance().log(e);
      sendErrorEvent(packageName, versionCode, e);
      throw new InstallationException("Installation with root failed for "
          + packageName
          + ". Error message: "
          + e.getMessage());
    }
  }

  private Observable<Void> defaultInstall(Context context, File file, String packageName) {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
    intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
    intentFilter.addDataScheme("package");
    return Observable.<Void> fromCallable(() -> {
      startInstallIntent(context, file);
      return null;
    }).flatMap(installStarted -> waitPackageIntent(context, intentFilter, packageName));
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
      photoURI =
          FileProvider.getUriForFile(context, V8Engine.getConfiguration().getAppId() + ".provider",
              file);
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
        .first(intent -> intent.getData().toString().contains(packageName))
        .map(intent -> null);
  }

  private boolean isInstalled(String packageName, int versionCode) {
    final PackageInfo info;
    try {
      info = packageManager.getPackageInfo(packageName, 0);
      return (info != null && info.versionCode == versionCode);
    } catch (PackageManager.NameNotFoundException e) {
      CrashReport.getInstance().log(e);
      return false;
    }
  }
}
