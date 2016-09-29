package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.repository.RollbackRepository;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trinkes on 9/8/16.
 */

@AllArgsConstructor public class RollbackInstallManager implements Installer {

  private final InstallManager installManager;
  private final RollbackRepository repository;
  private final RollbackInstallationFactory rollbackProvider;
  private final InstallationProvider installationProvider;

  @Override public Observable<Boolean> isInstalled(long installationId) {
    return installManager.isInstalled(installationId);
  }

  @Override public Observable<Void> install(Context context, PermissionRequest permissionRequest,
      long installationId) {
    return installationProvider.getInstallation(installationId)
        .flatMap(
            installation -> rollbackProvider.createRollback(installation, Rollback.Action.INSTALL))
        .map(rollback -> {
          repository.save(rollback);
          return rollback;
        })
        // TODO: 9/9/16 trinkes remove
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(rollback -> installManager.install(context, permissionRequest, installationId));
  }

  @Override public Observable<Void> install(Context context, PermissionRequest permissionRequest,
      String md5) {
    return installationProvider.getInstallation(md5)
        .flatMap(
            installation -> rollbackProvider.createRollback(installation, Rollback.Action.INSTALL))
        .map(rollback -> {
          repository.save(rollback);
          return rollback;
        })
        // TODO: 9/9/16 trinkes remove
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(rollback -> installManager.install(context, permissionRequest, md5));
  }

  @Override public Observable<Void> update(Context context, PermissionRequest permissionRequest,
      long installationId) {

    return installationProvider.getInstallation(installationId)
        .concatMap(installation -> getRollbackObservable(installation.getPackageName(),
            Rollback.Action.UPDATE, installation.getIcon()))
        .observeOn(AndroidSchedulers.mainThread())
        .concatWith(installManager.update(context, permissionRequest, installationId));
  }

  @Override public Observable<Void> downgrade(Context context, PermissionRequest permissionRequest,
      long installationId) {
    return installationProvider.getInstallation(installationId)
        .concatMap(installation -> getRollbackObservable(installation.getPackageName(),
            Rollback.Action.DOWNGRADE, installation.getIcon()))
        .observeOn(AndroidSchedulers.mainThread())
        .concatWith(installManager.downgrade(context, permissionRequest, installationId));
  }

  @Override public Observable<Void> uninstall(Context context, String packageName) {
    return rollbackProvider.createRollback(context, packageName, Rollback.Action.UNINSTALL, null)
        .map(rollback -> {
          repository.save(rollback);
          return rollback;
        })
        .concatMap(rollback -> installManager.uninstall(context, packageName));
  }

  private Observable<Void> getRollbackObservable(String packageName, Rollback.Action action,
      String icon) {
    return rollbackProvider.createRollback(Application.getContext(), packageName, action, icon)
        .map(rollback -> {
          repository.save(rollback);
          return null;
        });
  }
}
