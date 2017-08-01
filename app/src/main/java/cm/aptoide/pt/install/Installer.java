package cm.aptoide.pt.install;

import android.content.Context;
import cm.aptoide.pt.install.installer.InstallationState;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 9/8/16.
 */
public interface Installer {

  Completable install(Context context, String md5, boolean forceDefaultInstall);

  Completable update(Context context, String md5, boolean forceDefaultInstall);

  Completable downgrade(Context context, String md5, boolean forceDefaultInstall);

  Completable uninstall(Context context, String packageName, String versionName);

  Observable<InstallationState> getState(String packageName, int versionCode);
}
