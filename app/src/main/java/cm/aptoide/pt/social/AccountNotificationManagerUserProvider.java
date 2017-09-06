package cm.aptoide.pt.social;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.notification.NotificationCenter;
import cm.aptoide.pt.social.data.User;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 06/09/2017.
 */
public class AccountNotificationManagerUserProvider implements TimelineUserProvider {

  private final NotificationCenter notificationCenter;
  private final AptoideAccountManager accountManager;

  public AccountNotificationManagerUserProvider(NotificationCenter notificationCenter,
      AptoideAccountManager accountManager) {
    this.notificationCenter = notificationCenter;
    this.accountManager = accountManager;
  }

  private Observable<Boolean> isLoggedIn() {
    return accountManager.accountStatus()
        .map(account -> account.isLoggedIn());
  }

  private Observable<TimelineNotification> getNotification() {
    return notificationCenter.getUnreadNotifications()
        .map(aptoideNotifications -> {
          if (aptoideNotifications.isEmpty()) {
            return null;
          }
          AptoideNotification notification = aptoideNotifications.get(0);
          return new TimelineNotification(notification.getBody(), notification.getImg(),
              notification.getUrl(), mapNotificationType(notification.getType()));
        });
  }

  @Override public Completable notificationRead(NotificationType notificationType) {
    return notificationCenter.notificationDismissed(mapToAptoideNotificationType(notificationType));
  }

  @Override public Observable<User> getUser() {
    return Observable.combineLatest(isLoggedIn(), getNotification(),
        (isLoggedIn, timelineNotification) -> {
          if (isLoggedIn) {
            if (timelineNotification == null) {
              return new User(isLoggedIn);
            } else {
              return new User(timelineNotification.getBody(), timelineNotification.getImg(),
                  timelineNotification.getUrl(), timelineNotification.getType(), isLoggedIn);
            }
          }
          return new User(isLoggedIn);
        });
  }

  private @AptoideNotification.NotificationType Integer[] mapToAptoideNotificationType(
      NotificationType notificationType) {

    switch (notificationType) {
      case CAMPAIGN:
        return new Integer[] { AptoideNotification.CAMPAIGN };
      case COMMENT:
        return new Integer[] { AptoideNotification.COMMENT };
      case LIKE:
        return new Integer[] { AptoideNotification.LIKE };
      case POPULAR:
        return new Integer[] { AptoideNotification.POPULAR };
    }
    return null;
  }

  private NotificationType mapNotificationType(@AptoideNotification.NotificationType int type) {
    switch (type) {
      case AptoideNotification.CAMPAIGN:
        return NotificationType.CAMPAIGN;
      case AptoideNotification.COMMENT:
        return NotificationType.COMMENT;
      case AptoideNotification.LIKE:
        return NotificationType.LIKE;
      case AptoideNotification.POPULAR:
        return NotificationType.POPULAR;
    }
    throw new IllegalStateException(type + " notification type is unsuported");
  }
}
