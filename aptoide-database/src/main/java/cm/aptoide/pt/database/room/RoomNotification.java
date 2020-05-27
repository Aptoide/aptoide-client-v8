package cm.aptoide.pt.database.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification") public class RoomNotification {

  @Ignore public final static String KEY = "key";
  @Ignore public static final int NOT_DISMISSED = -1;

  private final Long expire;
  private final String abTestingGroup;
  private final String body;
  private final int campaignId;
  private final String img;
  private final String lang;
  private final String title;
  private final String url;
  private final String urlTrack;
  private final String notificationCenterUrlTrack;
  private final int type;
  private final long timeStamp;
  private final String appName;
  private final String graphic;
  private final String ownerId;
  private final boolean processed;
  private final int actionStringRes;
  @NonNull @PrimaryKey private String key;
  private long dismissed;

  public RoomNotification(Long expire, String abTestingGroup, String body, int campaignId,
      String img, String lang, String title, String url, String urlTrack,
      String notificationCenterUrlTrack, long timeStamp, int type, long dismissed, String appName,
      String graphic, String ownerId, boolean processed, int actionStringRes) {
    this.expire = expire;
    this.body = body;
    this.img = img;
    this.title = title;
    this.url = url;
    this.notificationCenterUrlTrack = notificationCenterUrlTrack;
    this.type = type;
    this.abTestingGroup = abTestingGroup;
    this.campaignId = campaignId;
    this.lang = lang;
    this.urlTrack = urlTrack;
    this.timeStamp = timeStamp;
    this.dismissed = dismissed;
    this.appName = appName;
    this.graphic = graphic;
    this.ownerId = ownerId;
    this.processed = processed;
    this.actionStringRes = actionStringRes;
    key = String.valueOf(timeStamp + type);
  }

  public Long getExpire() {
    return expire;
  }

  public String getAppName() {
    return appName;
  }

  public String getGraphic() {
    return graphic;
  }

  public long getDismissed() {
    return dismissed;
  }

  public int getType() {
    return type;
  }

  public String getAbTestingGroup() {
    return abTestingGroup;
  }

  public String getBody() {
    return body;
  }

  public int getCampaignId() {
    return campaignId;
  }

  public String getImg() {
    return img;
  }

  public String getLang() {
    return lang;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public String getUrlTrack() {
    return urlTrack;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public String getKey() {
    return key;
  }

  public void setKey(@NonNull String key) {
    this.key = key;
  }

  public boolean isDismissed() {
    return dismissed != NOT_DISMISSED;
  }

  public void setDismissed(long dismissed) {
    this.dismissed = dismissed;
  }

  public String getNotificationCenterUrlTrack() {
    return notificationCenterUrlTrack;
  }

  public boolean isProcessed() {
    return processed;
  }

  public int getActionStringRes() {
    return actionStringRes;
  }
}
