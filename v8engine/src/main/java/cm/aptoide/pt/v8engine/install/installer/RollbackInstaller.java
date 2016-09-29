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

public class RollbackInstaller extends Installer {

  private final DefaultInstaller defaultInstaller;
  private final RollbackRepository repository;
  private final RollbackFactory rollbackFactory;

  public RollbackInstaller(InstallationProvider installationProvider,
      DefaultInstaller defaultInstaller, RollbackRepository repository,
      RollbackFactory rollbackFactory) {
    super(installationProvider);
    this.defaultInstaller = defaultInstaller;
    this.repository = repository;
    this.rollbackFactory = rollbackFactory;
  }

  @Override public Observable<Boolean> isInstalled(long installationId) {
    return defaultInstaller.isInstalled(installationId);
  }

  @Override public Observable<Void> install(Context context, long installationId) {
    return getInstallation(installationId)
        .cast(RollbackInstallation.class)
        .flatMap(
            installation -> rollbackFactory.createRollback(installation, Rollback.Action.INSTALL))
        .map(rollback -> {
          repository.save(rollback);
          return rollback;
        })
        // TODO: 9/9/16 trinkes remove
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(rollback -> defaultInstaller.install(context, installationId));
  }

  @Override public Observable<Void> update(Context context, long installationId) {

    return getInstallation(installationId)
        .cast(RollbackInstallation.class)
        .concatMap(installation -> getRollbackObservable(installation.getPackageName(),
            Rollback.Action.UPDATE, installation.getIcon()))
        .observeOn(AndroidSchedulers.mainThread())
        .concatWith(defaultInstaller.update(context, installationId));
  }

  @Override public Observable<Void> downgrade(Context context, long installationId) {
    return getInstallation(installationId)
        .cast(RollbackInstallation.class)
        .concatMap(installation -> getRollbackObservable(installation.getPackageName(),
            Rollback.Action.DOWNGRADE, installation.getIcon()))
        .observeOn(AndroidSchedulers.mainThread())
        .concatWith(defaultInstaller.downgrade(context, installationId));
  }

  @Override public Observable<Void> uninstall(Context context, String packageName) {
    return rollbackFactory.createRollback(context, packageName, Rollback.Action.UNINSTALL, null)
        .map(rollback -> {
          repository.save(rollback);
          return rollback;
        })
        .concatMap(rollback -> defaultInstaller.uninstall(context, packageName));
  }

  private Observable<Void> getRollbackObservable(String packageName, Rollback.Action action,
      String icon) {
    return rollbackFactory.createRollback(Application.getContext(), packageName, action, icon)
        .map(rollback -> {
          repository.save(rollback);
          return null;
        });
  }
}
