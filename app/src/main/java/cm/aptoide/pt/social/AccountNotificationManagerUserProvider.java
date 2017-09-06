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
              notification.getUrl(), notification.getType());
        });
  }

  @Override public Completable notificationRead(int notificationId) {
    return notificationCenter.notificationDismissed(mapToAptoideNotificationType(notificationId));
  }

  @Override public Observable<User> getUser(boolean refresh) {
    return Observable.combineLatest(isLoggedIn(), getNotification(),
        (isLoggedIn, timelineNotification) -> {
          if (isLoggedIn) {
            if (timelineNotification == null) {
              return new User(isLoggedIn);
            } else {
              return new User(timelineNotification.getBody(), timelineNotification.getImg(),
                  timelineNotification.getUrl(), timelineNotification.getNotificationId(),
                  isLoggedIn);
            }
          }
          return new User(isLoggedIn);
        });
  }

  private @AptoideNotification.NotificationType Integer[] mapToAptoideNotificationType(
      int notificationType) {
    switch (notificationType) {
      case AptoideNotification.CAMPAIGN:
        return new Integer[] { AptoideNotification.CAMPAIGN };
      case AptoideNotification.COMMENT:
        return new Integer[] { AptoideNotification.COMMENT };
      case AptoideNotification.LIKE:
        return new Integer[] { AptoideNotification.LIKE };
      case AptoideNotification.POPULAR:
        return new Integer[] { AptoideNotification.POPULAR };
    }
    return null;
  }
}
