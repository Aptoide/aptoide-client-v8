package cm.aptoide.pt.notification;

public class NotificationIdsMapper {
  public NotificationIdsMapper() {
  }

  int getNotificationId(@AptoideNotification.NotificationType int notificationType)
      throws RuntimeException {
    switch (notificationType) {
      case AptoideNotification.CAMPAIGN:
        return 0;
      case AptoideNotification.COMMENT:
      case AptoideNotification.LIKE:
      case AptoideNotification.NEW_SHARE:
      case AptoideNotification.NEW_ACTIVITY:
        return 1;
      case AptoideNotification.POPULAR:
        return 2;
      default:
        throw new IllegalArgumentException("unknown notification type " + notificationType);
    }
  }

  @AptoideNotification.NotificationType Integer[] getNotificationType(int notificationId)
      throws RuntimeException {
    switch (notificationId) {
      case 0:
        return new Integer[] {
            AptoideNotification.CAMPAIGN
        };
      case 1:
        return new Integer[] {
            AptoideNotification.LIKE, AptoideNotification.COMMENT, AptoideNotification.NEW_SHARE,
            AptoideNotification.NEW_ACTIVITY
        };
      case 2:
        return new Integer[] {
            AptoideNotification.POPULAR,
        };
      default:
        throw new IllegalArgumentException("unknown notification notificationId " + notificationId);
    }
  }
}