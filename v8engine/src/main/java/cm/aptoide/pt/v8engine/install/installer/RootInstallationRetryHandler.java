package cm.aptoide.pt.v8engine.install.installer;

import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.notification.AptoideNotification;
import cm.aptoide.pt.v8engine.notification.SystemNotificationShower;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Completable;
import rx.Observable;
import rx.Subscription;

/**
 * Created by trinkes on 14/06/2017.
 */

public class RootInstallationRetryHandler {
  private final SystemNotificationShower systemNotificationShower;
  private final InstallManager installManager;
  private final PublishRelay<Void> handler;
  private int count;
  private Subscription subscription;

  public RootInstallationRetryHandler(SystemNotificationShower systemNotificationShower,
      InstallManager installManager, PublishRelay<Void> handler, int initialCount) {
    this.systemNotificationShower = systemNotificationShower;
    this.installManager = installManager;
    this.handler = handler;
    this.count = initialCount;
  }

  public void start() {
    subscription = installManager.getTimedOutInstallations()
        .map(installationProgresses -> new AptoideNotification("this is a test",
            "http://pool.img.aptoide.com/apps/8f04ea56b7d09d138fefa94e479fd97f_fgraphic_705x345.png",
            "this is a title", "https://www.google.com", "the app name"))
        .flatMapCompletable(installations -> {
          if (count == 0) {
            return systemNotificationShower.showNotification(installations);
          } else {
            return Completable.fromAction(() -> handler.call(null));
          }
        })
        .subscribe();
  }

  public void stop() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  public Observable<Void> retries() {
    return handler.doOnSubscribe(() -> count++)
        .doOnUnsubscribe(() -> count--);
  }
}
