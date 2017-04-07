package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import cm.aptoide.pt.v8engine.install.installer.DefaultInstaller;
import rx.Observable;

/**
 * Created by trinkes on 9/8/16.
 */
public interface Installer {

  Observable<Boolean> isInstalled(String md5);

  Observable<DefaultInstaller.InstallationType> install(Context context, String md5);

  Observable<DefaultInstaller.InstallationType> update(Context context, String md5);

  Observable<DefaultInstaller.InstallationType> downgrade(Context context, String md5);

  Observable<Void> uninstall(Context context, String packageName, String versionName);
}
