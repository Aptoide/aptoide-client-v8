package cm.aptoide.pt.v8engine.pull;

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
        return 1;
      case AptoideNotification.POPULAR:
        return 2;
      default:
        throw new RuntimeException("unknown notification type ");
    }
  }

  @AptoideNotification.NotificationType Integer[] getNotificationType(int notificationId)
      throws RuntimeException {
    switch (notificationId) {
      case AptoideNotification.CAMPAIGN:
        return new Integer[] {
            AptoideNotification.CAMPAIGN
        };
      case AptoideNotification.COMMENT:
      case AptoideNotification.LIKE:
        return new Integer[] {
            AptoideNotification.LIKE, AptoideNotification.COMMENT
        };
      case AptoideNotification.POPULAR:
        return new Integer[] {
            AptoideNotification.POPULAR,
        };
      default:
        throw new RuntimeException("unknown notification notificationId ");
    }
  }
}