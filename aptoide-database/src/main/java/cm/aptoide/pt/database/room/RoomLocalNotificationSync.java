package cm.aptoide.pt.database.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "localNotificationSync") public class RoomLocalNotificationSync {

  @NonNull @PrimaryKey private final String notificationId;
  private final String title;
  private final String body;
  private final String image;
  private final String navigationUrl;
  private final long trigger;
  private final String id;
  private final int actionStringRes;
  private final int type;

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

  public int getActionStringRes() {
    return actionStringRes;
  }

  public int getType() {
    return type;
  }
}
