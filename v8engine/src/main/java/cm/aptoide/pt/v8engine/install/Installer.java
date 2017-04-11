package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import cm.aptoide.pt.v8engine.install.installer.InstallationState;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 9/8/16.
 */
public interface Installer {

  Observable<Boolean> isInstalled(String md5);

  Completable install(Context context, String md5);

  Completable update(Context context, String md5);

  Completable downgrade(Context context, String md5);

  Completable uninstall(Context context, String packageName, String versionName);

  Observable<InstallationState> getState(String packageName, int versionCode);
}
