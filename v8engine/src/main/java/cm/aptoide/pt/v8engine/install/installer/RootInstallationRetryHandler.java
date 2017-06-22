package cm.aptoide.pt.v8engine.install.installer;

import android.content.Context;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.InstallationProgress;
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
  private static final String TAG = RootInstallationRetryHandler.class.getSimpleName();
  private final int notificationId;
  private final SystemNotificationShower systemNotificationShower;
  private final InstallManager installManager;
  private final PublishRelay<List<InstallationProgress>> handler;
  private int count;
  private Context context;
  private Subscription subscription;
  private RootInstallErrorNotificationFactory rootInstallErrorNotificationFactory;

  public RootInstallationRetryHandler(int notificationId,
      SystemNotificationShower systemNotificationShower, InstallManager installManager,
      PublishRelay<List<InstallationProgress>> handler, int initialCount, Context context,
      RootInstallErrorNotificationFactory rootInstallErrorNotificationFactory) {
    this.notificationId = notificationId;
    this.systemNotificationShower = systemNotificationShower;
    this.installManager = installManager;
    this.handler = handler;
    this.count = initialCount;
    this.context = context;
    this.rootInstallErrorNotificationFactory = rootInstallErrorNotificationFactory;
  }

  public void start() {
    subscription = installManager.getTimedOutInstallations()
        .flatMapCompletable(installationProgresses -> {
          if (count == 0) {
            return handleNotifications(installationProgresses);
          } else {
            return Completable.fromAction(() -> handler.call(installationProgresses));
          }
        })
        .subscribe(rootInstallErrorNotification -> {
        }, throwable -> Logger.e(TAG, "start: " + throwable));
  }

  private Completable handleNotifications(List<InstallationProgress> installationProgresses) {
    if (installationProgresses.isEmpty()) {
      return Completable.fromAction(() -> dismissNotification());
    } else {
      return systemNotificationShower.showNotification(context,
          rootInstallErrorNotificationFactory.create(context, installationProgresses));
    }
  }

  private Observable<Void> dismissNotification() {
    return Observable.fromCallable(() -> {
      systemNotificationShower.dismissNotification(notificationId);
      return null;
    });
  }

  public void stop() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  public Observable<List<InstallationProgress>> retries() {
    return handler.doOnSubscribe(() -> count++)
        .doOnUnsubscribe(() -> count--);
  }
}
