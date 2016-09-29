/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.v8engine.install.installer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import cm.aptoide.pt.v8engine.install.Installer;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by marcelobenites on 9/29/16.
 */

public class BackgroundInstaller extends Installer {

  private final Context context;
  private final Installer installer;

  public BackgroundInstaller(InstallationProvider installationProvider, Context context,
      Installer installer) {
    super(installationProvider);
    this.context = context;
    this.installer = installer;
  }

  public void startBackgroundService() {
    context.startService(new Intent(context, InstallService.class));
  }

  @Override public Observable<Boolean> isInstalled(long installationId) {
    return installer.isInstalled(installationId);
  }

  @Override public Observable<Void> install(Context context, long installationId) {
    return serviceInstall(context, installationId);
  }


  @Override public Observable<Void> update(Context context, long installationId) {
    return serviceInstall(context, installationId);
  }

  @Override public Observable<Void> downgrade(Context context, long installationId) {
    return serviceInstall(context, installationId);
  }

  @Override public Observable<Void> uninstall(Context context, String packageName) {
    return installer.uninstall(context, packageName);
  }

  private Observable<Void> serviceInstall(Context context, long installationId) {
    return getInstallation(installationId)
        .flatMap(installation -> packageIntent(context, getInstallFilter(), installation.getPackageName()));
  }
}
