package cm.aptoide.pt.v8engine.notification.sync;

import cm.aptoide.pt.v8engine.notification.NotificationNetworkService;
import cm.aptoide.pt.v8engine.notification.NotificationProvider;
import cm.aptoide.pt.v8engine.sync.Sync;
import rx.Completable;

public class SocialNotificationSync extends Sync {

  private final NotificationNetworkService networkService;
  private final NotificationProvider provider;

  public SocialNotificationSync(String id, NotificationNetworkService networkService,
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