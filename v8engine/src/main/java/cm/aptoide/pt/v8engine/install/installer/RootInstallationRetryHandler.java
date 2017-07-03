package cm.aptoide.pt.v8engine.install.installer;

import android.content.Context;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.Install;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.notification.SystemNotificationShower;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by trinkes on 14/06/2017.
 */

public class RootInstallationRetryHandler {
  private static final String TAG = RootInstallationRetryHandler.class.getSimpleName();
  private final int notificationId;
  private final SystemNotificationShower systemNotificationShower;
  private final InstallManager installManager;
  private final PublishRelay<List<Install>> handler;
  private int count;
  private Context context;
  private Subscription subscription;
  private RootInstallErrorNotificationFactory rootInstallErrorNotificationFactory;

  public RootInstallationRetryHandler(int notificationId,
      SystemNotificationShower systemNotificationShower, InstallManager installManager,
      PublishRelay<List<Install>> handler, int initialCount, Context context,
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
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(installationProgresses -> {
          if (installationProgresses.isEmpty()) {
            dismissNotifications();
          } else {
            showNotification(installationProgresses);
          }
        })
        .subscribe(rootInstallErrorNotification -> {
        }, throwable -> Logger.e(TAG, "start: " + throwable));
  }

  private void showNotification(List<Install> installs) {
    if (count == 0) {
      showSystemNotification(installs);
    } else {
      handler.call(installs);
    }
  }

  private void showSystemNotification(List<Install> installs) {
    systemNotificationShower.showNotification(context,
        rootInstallErrorNotificationFactory.create(context, installs));
  }

  private void dismissNotifications() {
    systemNotificationShower.dismissNotification(notificationId);
    handler.call(Collections.emptyList());
  }

  public void stop() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  public Observable<List<Install>> retries() {
    return handler.doOnSubscribe(() -> count++)
        .doOnUnsubscribe(() -> count--);
  }
}
