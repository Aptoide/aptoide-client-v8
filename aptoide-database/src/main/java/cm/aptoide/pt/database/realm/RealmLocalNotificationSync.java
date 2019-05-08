package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmLocalNotificationSync extends RealmObject {

  public static String PRIMARY_KEY_NAME = "notificationId";

  @PrimaryKey private String notificationId;
  private String title;
  private String body;
  private String image;
  private String navigationUrl;
  private long trigger;
  private String id;

  public RealmLocalNotificationSync(String notificationId, String title, String body, String image,
      String navigationUrl, long trigger, String id) {

    this.notificationId = notificationId;
    this.title = title;
    this.body = body;
    this.image = image;
    this.navigationUrl = navigationUrl;
    this.trigger = trigger;
    this.id = id;
  }

  public RealmLocalNotificationSync() {

  }

  public String getNotificationId() {
    return notificationId;
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  public String getImage() {
    return image;
  }

  public String getNavigationUrl() {
    return navigationUrl;
  }

  public long getTrigger() {
    return trigger;
  }

  public String getId() {
    return id;
  }
}
