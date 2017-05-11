package cm.aptoide.pt.v8engine.pull;

import cm.aptoide.pt.database.accessors.NotificationAccessor;
import rx.Completable;
import rx.Single;

/**
 * Created by trinkes on 09/05/2017.
 */

public class NotificationStatusManager {
  private NotificationAccessor notificationAccessor;
  private NotificationIdsMapper notificationIdsMapper;

  public NotificationStatusManager(NotificationAccessor notificationAccessor,
      NotificationIdsMapper notificationIdsMapper) {
    this.notificationAccessor = notificationAccessor;
    this.notificationIdsMapper = notificationIdsMapper;
  }

  Completable setShowed(int notificationId) {
    return notificationAccessor.getLastShowed(
        notificationIdsMapper.getNotificationType(notificationId)).doOnSuccess(notification -> {
      if (notification != null) {
        notification.setShowed(true);
        notificationAccessor.insert(notification);
      }
    }).toCompletable();
  }

  Single<Boolean> isVisible(@AptoideNotification.NotificationType int notificationType) {
    return Single.just(false);
  }
}