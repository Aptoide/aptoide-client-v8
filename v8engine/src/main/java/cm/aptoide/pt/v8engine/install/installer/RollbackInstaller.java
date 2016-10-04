/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.v8engine.install.installer;

import android.content.Context;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.install.provider.RollbackFactory;
import cm.aptoide.pt.v8engine.repository.RollbackRepository;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trinkes on 9/8/16.
 */

@AllArgsConstructor public class RollbackInstaller implements Installer {

  private final DefaultInstaller defaultInstaller;
  private final RollbackRepository repository;
  private final RollbackFactory rollbackFactory;
  private final InstallationProvider installationProvider;

  @Override public Observable<Boolean> isInstalled(long installationId) {
    return defaultInstaller.isInstalled(installationId);
  }

  @Override public Observable<Void> install(Context context, long installationId) {
    return installationProvider.getInstallation(installationId)
        .cast(RollbackInstallation.class)
        .flatMap(installation -> saveRollback(installation, Rollback.Action.INSTALL))
        .flatMap(success -> defaultInstaller.install(context, installationId));
  }

  @Override public Observable<Void> update(Context context, long installationId) {
    return installationProvider.getInstallation(installationId)
        .cast(RollbackInstallation.class)
        .flatMap(installation -> saveRollback(installation.getPackageName(), Rollback.Action.UPDATE, installation.getIcon()))
        .flatMap(success -> defaultInstaller.update(context, installationId));
  }

  @Override public Observable<Void> downgrade(Context context, long installationId) {
    return installationProvider.getInstallation(installationId)
        .cast(RollbackInstallation.class)
        .flatMap(installation -> saveRollback(installation.getPackageName(), Rollback.Action.DOWNGRADE, installation.getIcon()))
        .flatMap(success -> defaultInstaller.downgrade(context, installationId));
  }

  @Override public Observable<Void> uninstall(Context context, String packageName) {
    return saveRollback(packageName, Rollback.Action.UNINSTALL, null)
        .flatMap(rollback -> defaultInstaller.uninstall(context, packageName));
  }

  private Observable<Void> saveRollback(String packageName, Rollback.Action action,
      String icon) {
    return rollbackFactory.createRollback(Application.getContext(), packageName, action, icon)
        .map(rollback -> {
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
