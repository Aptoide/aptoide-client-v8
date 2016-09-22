/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.utils.CrashReports;
import cm.aptoide.pt.v8engine.install.exception.InstallationException;
import eu.chainfire.libsuperuser.Shell;
import java.io.File;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 7/18/16.
 */
@AllArgsConstructor public class InstallManager implements Installer {

  private static final String TAG = InstallManager.class.getSimpleName();

  private final PermissionManager permissionManager;
  @Getter(AccessLevel.PACKAGE) private final PackageManager packageManager;
  private final InstallationProvider installationProvider;

  @Override public Observable<Boolean> isInstalled(long installationId) {
    return installationProvider.getInstallation(installationId)
        .map(installation -> isInstalled(installation.getPackageName(),
            installation.getVersionCode()))
        .onErrorReturn(throwable -> false);
  }

  @Override public Observable<Void> install(Context context, PermissionRequest permissionRequest,
      long installationId) {
    return permissionManager.requestExternalStoragePermission(permissionRequest)
        .ignoreElements()
        .concatWith(installationProvider.getInstallation(installationId)
            .observeOn(Schedulers.computation())
            .flatMap(installation -> {
              if (isInstalled(installation.getPackageName(), installation.getVersionCode())) {
                return Observable.just(null);
              } else {
                return systemInstall(context, installation.getFile()).onErrorResumeNext(
                    Observable.fromCallable(
                        () -> rootInstall(installation.getFile(), installation.getPackageName(),
                            installation.getVersionCode())))
                    .onErrorResumeNext(defaultInstall(context, installation.getFile(),
                        installation.getPackageName()));
              }
            }).doOnError(CrashReports::logException));
  }

  @Override public Observable<Void> update(Context context, PermissionRequest permissionRequest,
      long installationId) {
    return install(context, permissionRequest, installationId);
  }

  @Override public Observable<Void> downgrade(Context context, PermissionRequest permissionRequest,
      long installationId) {
    return installationProvider.getInstallation(installationId)
        .first()
        .concatMap(installation -> uninstall(context, installation.getPackageName()))
        .concatWith(install(context, permissionRequest, installationId));
  }

  @Override public Observable<Void> uninstall(Context context, String packageName) {
    final Uri uri = Uri.fromParts("package", packageName, null);
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
    intentFilter.addDataScheme("package");
    return Observable.<Void>fromCallable(() -> {
      startUninstallIntent(context, packageName, uri);
      return null;
    }).ignoreElements()
        .concatWith(packageIntent(context, intentFilter, packageName))
        .subscribeOn(Schedulers.computation());
  }

  private Observable<Void> defaultInstall(Context context, File file, String packageName) {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
    intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
    intentFilter.addDataScheme("package");
    return Observable.<Void>fromCallable(() -> {
      startInstallIntent(context, file);
      return null;
    }).ignoreElements().concatWith(packageIntent(context, intentFilter, packageName));
  }

  private void startInstallIntent(Context context, File file) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  private Observable<Void> systemInstall(Context context, File file) {
    return Observable.create(
        new SystemInstallOnSubscribe(context, packageManager, Uri.fromFile(file)));
  }

  private Void rootInstall(File file, String packageName, int versionCode)
      throws InstallationException {
    try {
      //if (Shell.SU.available()) {

      Shell.Builder shellBuilder = new Shell.Builder();
      Shell.Interactive interactiveShell = shellBuilder.useSU().setWatchdogTimeout(10) // seconds
          .addCommand("pm install -r " + file.getAbsolutePath(), 0,
              (commandCode, exitCode, output) -> {
                if (exitCode == 0) {
                  Logger.v(TAG, "app successfully installed using root");
                  return;
                }

                Logger.e(TAG, "Error using su to install package " + packageName);
                for (String s : output) {
                  Logger.e(TAG, "su command result: " + s);
                }
              }).open();

      interactiveShell.waitForIdle();

      if (!isInstalled(packageName, versionCode)) {
        throw new RuntimeException("Could not verify installation.");
      }

      // app sucessfully installed using root
      return null;
      //} else {
      //  throw new RuntimeException("Device not rooted.");
      //}
    } catch (Exception e) {
      CrashReports.logException(e);
      throw new InstallationException("Installation with root failed for "
          + packageName
          + ". Error message: "
          + e.getMessage());
    }
  }

  private void startUninstallIntent(Context context, String packageName, Uri uri)
      throws InstallationException {
    try {
      // Check if package is installed first
      packageManager.getPackageInfo(packageName, 0);
      Intent intent = new Intent(Intent.ACTION_DELETE, uri);
      context.startActivity(intent);
    } catch (PackageManager.NameNotFoundException e) {
      CrashReports.logException(e);
      throw new InstallationException(e);
    }
  }

  @NonNull private Observable<Void> packageIntent(Context context, IntentFilter intentFilter,
      String packageName) {
    return Observable.create(new BroadcastRegisterOnSubscribe(context, intentFilter, null, null))
        .first(intent -> intent.getData().toString().contains(packageName)).<Void>map(
            intent -> null);
  }

  private boolean isInstalled(String packageName, int versionCode) {
    final PackageInfo info;
    try {
      info = packageManager.getPackageInfo(packageName, 0);
      return (info != null && info.versionCode == versionCode);
    } catch (PackageManager.NameNotFoundException e) {
      CrashReports.logException(e);
      return false;
    }
  }
}
