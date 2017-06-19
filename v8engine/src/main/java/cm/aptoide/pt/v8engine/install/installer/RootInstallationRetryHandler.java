package cm.aptoide.pt.v8engine.install.installer;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
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
  private final int notificationId;
  private final SystemNotificationShower systemNotificationShower;
  private final InstallManager installManager;
  private final PublishRelay<RootInstallErrorNotification> handler;
  private final NotificationCompat.Action notificationAction;
  private int count;
  private Bitmap icon;
  private Context context;
  private Subscription subscription;

  public RootInstallationRetryHandler(int notificationId,
      SystemNotificationShower systemNotificationShower, InstallManager installManager,
      PublishRelay<RootInstallErrorNotification> handler, int initialCount, Context context,
      Bitmap icon, NotificationCompat.Action notificationAction) {
    this.notificationId = notificationId;
    this.systemNotificationShower = systemNotificationShower;
    this.installManager = installManager;
    this.handler = handler;
    this.count = initialCount;
    this.icon = icon;
    this.context = context;
    this.notificationAction = notificationAction;
  }

  public void start() {
    subscription = installManager.getTimedOutInstallations()
        .flatMap(installationProgresses -> {
          switch (installationProgresses.size()) {
            case 0:
              return dismissNotification();
            default:
              return showErrorNotification(installationProgresses);
          }
        })
        .subscribe(rootInstallErrorNotification -> {
        }, throwable -> throwable.printStackTrace());
  }

  private Observable<Void> dismissNotification() {
    if (count == 0) {
      systemNotificationShower.dismissNotification(notificationId);
    }
    return Observable.empty();
  }

  @NonNull public Observable<RootInstallErrorNotification> showErrorNotification(
      List<InstallationProgress> installationProgresses) {
    return Observable.just(new RootInstallErrorNotification(notificationId, icon,
        getNotificationTitle(installationProgresses), notificationAction))
        .flatMapCompletable(installations -> {
          if (count == 0) {
            return systemNotificationShower.showNotification(context, installations);
          } else {
            return Completable.fromAction(() -> handler.call(installations));
          }
        });
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
