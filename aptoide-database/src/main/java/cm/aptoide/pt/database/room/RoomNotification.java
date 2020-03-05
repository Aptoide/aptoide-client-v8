package cm.aptoide.pt.database.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification") public class RoomNotification {

  public final static String KEY = "key";
  public static final int NOT_DISMISSED = -1;

  private Long expire;
  @NonNull @PrimaryKey private String key;
  private String abTestingGroup;
  private String body;
  private int campaignId;
  private String img;
  private String lang;
  private String title;
  private String url;
  private String urlTrack;
  private String notificationCenterUrlTrack;
  private int type;
  private long timeStamp;
  private long dismissed;
  private String appName;
  private String graphic;
  private String ownerId;
  private boolean processed;
  private int actionStringRes;

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

  public RoomNotification() {
    expire = 0L;
  }

  public Long getExpire() {
    return expire;
  }

  public void setExpire(Long expire) {
    this.expire = expire;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getGraphic() {
    return graphic;
  }

  public void setGraphic(String graphic) {
    this.graphic = graphic;
  }

  public long getDismissed() {
    return dismissed;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getAbTestingGroup() {
    return abTestingGroup;
  }

  public void setAbTestingGroup(String abTestingGroup) {
    this.abTestingGroup = abTestingGroup;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public int getCampaignId() {
    return campaignId;
  }

  public void setCampaignId(int campaignId) {
    this.campaignId = campaignId;
  }

  public String getImg() {
    return img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrlTrack() {
    return urlTrack;
  }

  public void setUrlTrack(String urlTrack) {
    this.urlTrack = urlTrack;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
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

  public void setNotificationCenterUrlTrack(String notificationCenterUrlTrack) {
    this.notificationCenterUrlTrack = notificationCenterUrlTrack;
  }

  public boolean isProcessed() {
    return processed;
  }

  public void setProcessed(boolean processed) {
    this.processed = processed;
  }

  public int getActionStringRes() {
    return actionStringRes;
  }

  public void setActionStringRes(int actionStringRes) {
    this.actionStringRes = actionStringRes;
  }
}
