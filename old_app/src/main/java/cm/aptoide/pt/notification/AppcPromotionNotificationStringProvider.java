package cm.aptoide.pt.notification;

public class AppcPromotionNotificationStringProvider {

  private final String notificationTitle;
  private final String notificationBody;

  public AppcPromotionNotificationStringProvider(String notificationTitle,
      String notificationBody) {
    this.notificationTitle = notificationTitle;
    this.notificationBody = notificationBody;
  }

  public String getNotificationTitle() {
    return notificationTitle;
  }

  public String getNotificationBody() {
    return notificationBody;
  }
}
