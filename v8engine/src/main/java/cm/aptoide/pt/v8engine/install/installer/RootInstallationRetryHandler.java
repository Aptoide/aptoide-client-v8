package cm.aptoide.pt.v8engine.install.installer;

import android.content.Context;
import android.support.annotation.NonNull;
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
  private final PublishRelay<RootInstallErrorNotification> handler;
  private int count;
  private Context context;
  private Subscription subscription;
  private RootInstallErrorNotificationFactory rootInstallErrorNotificationFactory;

  public RootInstallationRetryHandler(int notificationId,
      SystemNotificationShower systemNotificationShower, InstallManager installManager,
      PublishRelay<RootInstallErrorNotification> handler, int initialCount, Context context,
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
        .flatMap(installationProgresses -> {
          switch (installationProgresses.size()) {
            case 0:
              return dismissNotification();
            default:
              return showErrorNotification(installationProgresses);
          }
        })
        .subscribe(rootInstallErrorNotification -> {
        }, throwable -> Logger.e(TAG, "start: " + throwable));
  }

  private Observable<Void> dismissNotification() {
    return Observable.fromCallable(() -> {
      systemNotificationShower.dismissNotification(notificationId);
      return null;
    });
  }

  @NonNull public Observable<RootInstallErrorNotification> showErrorNotification(
      List<InstallationProgress> installationProgresses) {
    return Observable.just(
        rootInstallErrorNotificationFactory.create(context, installationProgresses))
        .flatMapCompletable(installations -> {
          if (count == 0) {
            return systemNotificationShower.showNotification(context, installations);
          } else {
            return Completable.fromAction(() -> handler.call(installations));
          }
        });
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
