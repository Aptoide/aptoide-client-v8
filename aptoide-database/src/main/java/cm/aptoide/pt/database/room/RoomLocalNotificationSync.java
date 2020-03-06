package cm.aptoide.pt.database.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "localNotificationSync") public class RoomLocalNotificationSync {

  @NonNull @PrimaryKey private String notificationId;
  private String title;
  private String body;
  private String image;
  private String navigationUrl;
  private long trigger;
  private String id;
  private int actionStringRes;
  private int type;

  public RoomLocalNotificationSync(String notificationId, String title, String body, String image,
      int actionStringRes, String navigationUrl, long trigger, String id, int type) {
    this.notificationId = notificationId;
    this.title = title;
    this.body = body;
    this.image = image;
    this.navigationUrl = navigationUrl;
    this.trigger = trigger;
    this.id = id;
    this.actionStringRes = actionStringRes;
    this.type = type;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public void setNotificationId(@NonNull String notificationId) {
    this.notificationId = notificationId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getNavigationUrl() {
    return navigationUrl;
  }

  public void setNavigationUrl(String navigationUrl) {
    this.navigationUrl = navigationUrl;
  }

  public long getTrigger() {
    return trigger;
  }

  public void setTrigger(long trigger) {
    this.trigger = trigger;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getActionStringRes() {
    return actionStringRes;
  }

  public void setActionStringRes(int actionStringRes) {
    this.actionStringRes = actionStringRes;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}
