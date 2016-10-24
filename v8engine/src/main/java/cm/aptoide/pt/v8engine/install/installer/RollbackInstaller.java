/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.v8engine.install.installer;

import android.content.Context;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.provider.RollbackFactory;
import cm.aptoide.pt.v8engine.repository.RollbackRepository;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by trinkes on 9/8/16.
 */

@AllArgsConstructor public class RollbackInstaller implements Installer {

  private final DefaultInstaller defaultInstaller;
  private final RollbackRepository repository;
  private final RollbackFactory rollbackFactory;
  private final InstallationProvider installationProvider;

  @Override public Observable<Boolean> isInstalled(String md5) {
    return defaultInstaller.isInstalled(md5);
  }

  @Override public Observable<Void> install(Context context, String md5) {
    return installationProvider.getInstallation(md5)
        .cast(RollbackInstallation.class)
        .flatMap(installation -> saveRollback(installation, Rollback.Action.INSTALL))
        .flatMap(success -> defaultInstaller.install(context, md5));
  }

  @Override public Observable<Void> update(Context context, String md5) {
    return installationProvider.getInstallation(md5)
        .cast(RollbackInstallation.class)
        .flatMap(installation -> saveRollback(context, installation.getPackageName(),
            Rollback.Action.UPDATE, installation.getIcon(), installation.getVersionName()))
        .flatMap(success -> defaultInstaller.update(context, md5));
  }

  @Override public Observable<Void> downgrade(Context context, String md5) {
    return installationProvider.getInstallation(md5)
        .cast(RollbackInstallation.class)
        .flatMap(installation -> saveRollback(context, installation.getPackageName(),
            Rollback.Action.DOWNGRADE, installation.getIcon(), installation.getVersionName()))
        .flatMap(success -> defaultInstaller.downgrade(context, md5));
  }

  @Override public Observable<Void> uninstall(Context context, String packageName, String versionName) {
    return saveRollback(context, packageName, Rollback.Action.UNINSTALL, null, versionName).flatMap(
        rollback -> defaultInstaller.uninstall(context, packageName, versionName));
  }

  private Observable<Void> saveRollback(Context context, String packageName, Rollback.Action action,
      String icon, String versionName) {
    return rollbackFactory.createRollback(context, packageName, action, icon, versionName).map(rollback -> {
      repository.save(rollback);
      return null;
    });
  }

  private Observable<Void> saveRollback(RollbackInstallation installation, Rollback.Action action) {
    return Observable.fromCallable(() -> {
      repository.save(rollbackFactory.createRollback(installation, action));
      return null;
    });
  }
}
