package cm.aptoide.pt.v8engine.pull;

import cm.aptoide.pt.database.accessors.NotificationAccessor;
import rx.Completable;
import rx.Single;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationStatusManager {
  private NotificationAccessor notificationAccessor;

  public NotificationStatusManager(NotificationAccessor notificationAccessor) {
    this.notificationAccessor = notificationAccessor;
  }

  Completable setShowed(int notificationType) {
    return null;
  }

  Single<Boolean> isVisible(@AptoideNotification.NotificationType int notificationType) {
    return Single.just(false);
  }
}