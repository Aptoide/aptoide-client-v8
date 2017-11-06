package cm.aptoide.pt.notification.sync;

import cm.aptoide.pt.notification.NotificationService;
import cm.aptoide.pt.notification.NotificationProvider;
import cm.aptoide.pt.sync.Sync;
import rx.Completable;

public class CampaignNotificationSync extends Sync {

  private final NotificationService networkService;
  private final NotificationProvider provider;

  public CampaignNotificationSync(String id, NotificationService networkService,
      NotificationProvider provider, boolean periodic, boolean exact, long interval, long trigger) {
    super(id, periodic, exact, trigger, interval);
    this.networkService = networkService;
    this.provider = provider;
  }

  @Override public Completable execute() {
    return networkService.getCampaignNotifications()
        .flatMapCompletable(aptoideNotification -> provider.save(aptoideNotification));
  }
}
