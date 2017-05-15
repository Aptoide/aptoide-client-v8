package cm.aptoide.pt.v8engine.notification;

import rx.Completable;

/**
 * Created by trinkes on 09/05/2017.
 */

class NotificationSync {
  private NotificationProvider notificationProvider;
  private NotificationNetworkService notificationNetworkService;

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
