package cm.aptoide.pt.install;

import cm.aptoide.pt.install.installer.InstallationState;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 9/8/16.
 */
public interface Installer {

  void dispatchInstallations();

  Completable install(String md5, boolean forceDefaultInstall, boolean shouldSetPackageInstaller);

  Completable update(String md5, boolean forceDefaultInstall, boolean shouldSetPackageInstaller);

  Completable downgrade(String md5, boolean forceDefaultInstall, boolean shouldSetPackageInstaller);

  Completable uninstall(String packageName);

  Observable<InstallationState> getState(String packageName, int versionCode);

  void stopDispatching();
}
