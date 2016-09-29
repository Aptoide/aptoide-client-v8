package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.install.installer.Installation;
import cm.aptoide.pt.v8engine.install.installer.InstallationProvider;
import rx.Observable;

/**
 * Created by trinkes on 9/8/16.
 */
public abstract class Installer {

  protected final InstallationProvider installationProvider;

  public Installer(InstallationProvider installationProvider) {
    this.installationProvider = installationProvider;
  }

  public abstract Observable<Boolean> isInstalled(long installationId);

  public abstract Observable<Void> install(Context context, long installationId);

  protected Observable<? extends Installation> getInstallation(long installationId) {
    return installationProvider.getInstallation(installationId);
  }

  public abstract Observable<Void> update(Context context, long installationId);

  public abstract Observable<Void> downgrade(Context context, long installationId);

  public abstract Observable<Void> uninstall(Context context, String packageName);

  @NonNull protected Observable<Void> packageIntent(Context context, IntentFilter intentFilter,
      String packageName) {
    return Observable.create(new BroadcastRegisterOnSubscribe(context, intentFilter, null, null))
        .first(intent -> intent.getData().toString().contains(packageName))
        .<Void>map(intent -> null);
  }

  @NonNull protected IntentFilter getInstallFilter() {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
    intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
    intentFilter.addDataScheme("package");
    return intentFilter;
  }
}
