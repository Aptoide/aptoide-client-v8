package cm.aptoide.pt.v8engine.pull;

import cm.aptoide.pt.database.accessors.NotificationAccessor;
import rx.Completable;

/**
 * Created by trinkes on 09/05/2017.
 */

class NotificationSync {
  private NotificationAccessor notificationAccessor;
  private NotificationNetworkService notificationNetworkService;

  public NotificationSync(NotificationAccessor notificationAccessor,
      NotificationNetworkService notificationNetworkService) {

    this.notificationAccessor = notificationAccessor;
    this.notificationNetworkService = notificationNetworkService;
  }

  public Completable syncCampaigns() {
    return notificationNetworkService.getCampaignNotifications()
        .flatMapCompletable(aptideNotifications -> Completable.fromAction(
            () -> notificationAccessor.insertAll(aptideNotifications)));
  }

  public Completable syncSocial() {
    return notificationNetworkService.getSocialNotifications()
        .flatMapCompletable(aptideNotifications -> Completable.fromAction(
            () -> notificationAccessor.insertAll(aptideNotifications)));
  }
}
