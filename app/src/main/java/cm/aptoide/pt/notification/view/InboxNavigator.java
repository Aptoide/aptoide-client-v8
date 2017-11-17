package cm.aptoide.pt.notification.view;

import cm.aptoide.pt.notification.AptoideNotification;

public class InboxNavigator {

  private final NotificationNavigator notificationNavigator;

  public InboxNavigator(NotificationNavigator notificationNavigator) {
    this.notificationNavigator = notificationNavigator;
  }

  public void navigateToNotification(AptoideNotification notification) {
    notificationNavigator.navigateToNotification(notification);
  }
}
