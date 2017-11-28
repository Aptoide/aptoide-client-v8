package cm.aptoide.pt.notification;

/**
 * Created by pedroribeiro on 15/11/17.
 */

public class NotificationInfo {
  private @AptoideNotification.NotificationType int notificationType;
  private String notificationTrackUrl;
  private String notificationUrl;
  private String action;

  public NotificationInfo(int notificationType, String notificationTrackUrl,
      String notificationUrl) {
    this.notificationType = notificationType;
    this.notificationTrackUrl = notificationTrackUrl;
    this.notificationUrl = notificationUrl;
  }

  public int getNotificationType() {
    return notificationType;
  }

  public String getNotificationTrackUrl() {
    return notificationTrackUrl;
  }

  public String getNotificationUrl() {
    return notificationUrl;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
