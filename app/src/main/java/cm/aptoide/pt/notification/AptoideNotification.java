package cm.aptoide.pt.notification;

import androidx.annotation.IntDef;
import cm.aptoide.pt.database.room.RoomNotification;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by trinkes on 03/05/2017.
 */

public class AptoideNotification {
  public static final int CAMPAIGN = 0;
  public static final int COMMENT = 1;
  public static final int LIKE = 2;
  public static final int POPULAR = 3;
  public static final int NEW_FOLLOWER = 4;
  public static final int NEW_SHARE = 5;
  public static final int NEW_ACTIVITY = 6;
  public static final int APPC_PROMOTION = 7;
  public static final int NEW_FEATURE = 8;
  public static final int APPS_READY_TO_INSTALL = 9;
  public static final int NOT_DISMISSED = RoomNotification.NOT_DISMISSED;
  private Long expire;
  private String appName;
  private String graphic;
  private long dismissed;
  private String abTestingGroup;
  private String body;
  private int campaignId;
  private String img;
  private String lang;
  private List<String> whitelistedPackages;
  private String title;
  private int actionStringRes = -1;
  private String url;
  private String urlTrack;
  private String ownerId;
  private @NotificationType int type;
  private long timeStamp;
  private String notificationCenterUrlTrack;
  private boolean processed;

  public AptoideNotification(String body, String img, String title, String url, int type,
      long timeStamp, String appName, String graphic, long dismissed, String ownerId,
      String urlTrack, String notificationCenterUrlTrack, boolean processed, Long expireSecsUtc,
      int actionStringRes) {
    this.body = body;
    this.img = img;
    this.title = title;
    this.url = url;
    this.type = type;
    this.timeStamp = timeStamp;
    this.appName = appName;
    this.graphic = graphic;
    this.dismissed = dismissed;
    this.ownerId = ownerId;
    this.expire = expireSecsUtc;
    this.urlTrack = urlTrack;
    this.notificationCenterUrlTrack = notificationCenterUrlTrack;
    this.processed = processed;
    this.actionStringRes = actionStringRes;
  }

  public AptoideNotification(String body, String img, String title, String url, int type,
      String appName, String graphic, long dismissed, String ownerId, String urlTrack,
      String notificationCenterUrlTrack, boolean processed, long timeStamp, Long expireSecsUtc,
      String abTestingGroup, int campaignId, String lang, int actionStringRes,
      List<String> whitelistedPackages) {
    this(body, img, title, url, type, timeStamp, appName, graphic, dismissed, ownerId, urlTrack,
        notificationCenterUrlTrack, processed, expireSecsUtc, actionStringRes);
    this.abTestingGroup = abTestingGroup;
    this.campaignId = campaignId;
    this.lang = lang;
    this.whitelistedPackages = whitelistedPackages;
  }

  public AptoideNotification(String img, String title, String url, String urlTrack, String graphic,
      int type, int campaignId, long timeStamp, String ownerId) {
    this.img = img;
    this.title = title;
    this.url = url;
    this.urlTrack = urlTrack;
    this.graphic = graphic;
    this.type = type;
    this.campaignId = campaignId;
    this.timeStamp = timeStamp;
    this.ownerId = ownerId;
  }

  public AptoideNotification(String img, String appName, String url, String graphic, int type) {
    this.img = img;
    this.url = url;
    this.graphic = graphic;
    this.type = type;
    this.appName = appName;
  }

  @Override public String toString() {
    return "AptoideNotification{" + "title='" + title + '\'' + '}';
  }

  public boolean isProcessed() {
    return processed;
  }

  public void setProcessed(boolean processed) {
    this.processed = processed;
  }

  public long getDismissed() {
    return dismissed;
  }

  public String getAppName() {
    return appName;
  }

  public String getGraphic() {
    return graphic;
  }

  public @NotificationType int getType() {
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

  public Long getExpire() {
    return expire;
  }

  public String getNotificationCenterUrlTrack() {
    return notificationCenterUrlTrack;
  }

  public int getActionStringRes() {
    return actionStringRes;
  }

  public List<String> getWhitelistedPackages() {
    return whitelistedPackages;
  }

  public void setWhitelistedPackages(List<String> whitelistedPackages) {
    this.whitelistedPackages = whitelistedPackages;
  }

  @Retention(RetentionPolicy.SOURCE) @IntDef({
      CAMPAIGN, COMMENT, LIKE, POPULAR, NEW_FOLLOWER, NEW_SHARE, NEW_ACTIVITY, APPC_PROMOTION,
      NEW_FEATURE, APPS_READY_TO_INSTALL
  }) public @interface NotificationType {
  }
}
