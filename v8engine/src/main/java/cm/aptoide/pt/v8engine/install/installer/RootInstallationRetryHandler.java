package cm.aptoide.pt.v8engine.install.installer;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.InstallationProgress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.notification.SystemNotificationShower;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Subscription;

/**
 * Created by trinkes on 14/06/2017.
 */

public class RootInstallationRetryHandler {
  public static final int NOTIFICATION_ID = 2342384;
  private final SystemNotificationShower systemNotificationShower;
  private final InstallManager installManager;
  private final PublishRelay<RootInstallErrorNotification> handler;
  private int count;
  private Bitmap icon;
  private Context context;
  private Subscription subscription;

  public RootInstallationRetryHandler(SystemNotificationShower systemNotificationShower,
      InstallManager installManager, PublishRelay<RootInstallErrorNotification> handler,
      int initialCount, Context context, Bitmap icon) {
    this.systemNotificationShower = systemNotificationShower;
    this.installManager = installManager;
    this.handler = handler;
    this.count = initialCount;
    this.icon = icon;
    this.context = context;
  }

  public void start() {
    subscription = installManager.getTimedOutInstallations()
        .filter(installationProgresses -> installationProgresses.size() > 0)
        .map(installationProgresses -> new RootInstallErrorNotification(NOTIFICATION_ID, icon,
            getNotificationTitle(installationProgresses)))
        .flatMapCompletable(installations -> {
          if (count == 0) {
            return systemNotificationShower.showNotification(context, installations);
          } else {
            return Completable.fromAction(() -> handler.call(installations));
          }
        })
        .subscribe(rootInstallErrorNotification -> {
        }, throwable -> throwable.printStackTrace());
  }

  @NonNull private String getNotificationTitle(List<InstallationProgress> installationProgresses) {
    String title;
    if (installationProgresses.size() == 1) {
      title = context.getString(
          R.string.generalscreen_short_root_install_single_app_timeout_error_message);
    } else {
      title = context.getString(R.string.generalscreen_short_root_install_timeout_error_message,
          installationProgresses.size());
    }
    return title;
  }

  public void stop() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  public Observable<RootInstallErrorNotification> retries() {
    return handler.doOnSubscribe(() -> count++)
        .doOnUnsubscribe(() -> count--);
  }
}
