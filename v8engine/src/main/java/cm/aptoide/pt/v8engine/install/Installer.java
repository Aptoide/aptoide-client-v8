package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import rx.Observable;

/**
 * Created by trinkes on 9/8/16.
 */
public interface Installer {

  Observable<Boolean> isInstalled(String md5);

  Observable<Void> install(Context context, String md5);

  Observable<Void> update(Context context, String md5);

  Observable<Void> downgrade(Context context, String md5);

  Observable<Void> uninstall(Context context, String packageName, String versionName);
}
