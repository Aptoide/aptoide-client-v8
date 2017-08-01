package cm.aptoide.pt.sync.alarm;

import cm.aptoide.pt.notification.NotificationNetworkService;
import cm.aptoide.pt.notification.NotificationProvider;
import rx.Completable;

/**
 * Created by trinkes on 09/05/2017.
 */

class NotificationSync {

  private final NotificationProvider notificationProvider;
  private final NotificationNetworkService notificationNetworkService;

  public NotificationSync(NotificationProvider notificationProvider,
      NotificationNetworkService notificationNetworkService) {
    this.notificationProvider = notificationProvider;
    this.notificationNetworkService = notificationNetworkService;
  }

  public Completable syncCampaigns() {
    return notificationNetworkService.getCampaignNotifications()
        .flatMapCompletable(aptoideNotification -> notificationProvider.save(aptoideNotification));
  }

  public Completable syncSocial() {
    return notificationNetworkService.getSocialNotifications()
        .flatMapCompletable(aptoideNotification -> notificationProvider.save(aptoideNotification));
  }
}
