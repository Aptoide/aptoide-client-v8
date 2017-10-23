package cm.aptoide.pt.notification;

import cm.aptoide.pt.sync.Sync;
import rx.Completable;

public class PushNotificationSync extends Sync {

  private final NotificationService networkService;
  private final NotificationProvider provider;

  public PushNotificationSync(String id, NotificationService networkService,
      NotificationProvider provider, boolean periodic, boolean exact, long interval, long trigger) {
    super(id, periodic, exact, trigger, interval);
    this.networkService = networkService;
    this.provider = provider;
  }

  @Override public Completable execute() {
    return networkService.getSocialNotifications()
        .flatMapCompletable(aptoideNotification -> provider.save(aptoideNotification));
  }
}
