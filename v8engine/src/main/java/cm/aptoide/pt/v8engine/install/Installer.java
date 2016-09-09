package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import cm.aptoide.pt.actions.PermissionRequest;
import rx.Observable;

/**
 * Created by trinkes on 9/8/16.
 */
public interface Installer {

  Observable<Boolean> isInstalled(long installationId);

  Observable<Void> install(Context context, PermissionRequest permissionRequest,
      long installationId);

  Observable<Void> update(Context context, PermissionRequest permissionRequest,
      long installationId);

  Observable<Void> downgrade(Context context, PermissionRequest permissionRequest,
      long installationId);

  Observable<Void> uninstall(Context context, String packageName);
}
