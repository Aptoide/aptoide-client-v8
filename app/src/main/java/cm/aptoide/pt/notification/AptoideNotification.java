package cm.aptoide.pt.notification;

import android.support.annotation.IntDef;
import cm.aptoide.pt.database.realm.Notification;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import lombok.ToString;

/**
 * Created by trinkes on 03/05/2017.
 */

@ToString(of = { "title" }) public class AptoideNotification {
  public static final int CAMPAIGN = 0;
  public static final int COMMENT = 1;
  public static final int LIKE = 2;
  public static final int POPULAR = 3;
  public static final int NEW_SHARE = 5;
  public static final int NEW_ACTIVITY = 6;
  public static final int NOT_DISMISSED = Notification.NOT_DISMISSED;
  private final Long expire;
  private String appName;
  private String graphic;
  private long dismissed;
  private String abTestingGroup;
  private String body;
  private int campaignId;
  private String img;
  private String lang;
  private String title;
  private String url;
  private String urlTrack;
  private String ownerId;
  private @NotificationType int type;
  private long timeStamp;
  private String notificationCenterUrlTrack;
  private boolean processed;

  public AptoideNotification(String body, String img, String title, String url, int type,
      long timeStamp, String appName, String graphic, long dismissed, String ownerId,
      Long expireSecsUtc, String urlTrack, String notificationCenterUrlTrack, boolean processed) {
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
  }

  public AptoideNotification(String body, String img, String title, String url, int type,
      String appName, String graphic, long dismissed, String ownerId, Long expireSecsUtc,
      String urlTrack, String notificationCenterUrlTrack, boolean processed) {
    this(body, img, title, url, type, System.currentTimeMillis(), appName, graphic, dismissed,
        ownerId, expireSecsUtc * 1000, urlTrack, notificationCenterUrlTrack, processed);
  }

  public AptoideNotification(String abTestingGroup, String body, int campaignId, String img,
      String lang, String title, String url, String urlTrack, String appName, String graphic,
      String ownerId, Long expireSecsUtc, String notificationCenterUrlTrack, boolean processed) {
    this(abTestingGroup, body, campaignId, img, lang, title, url, urlTrack,
        System.currentTimeMillis(), CAMPAIGN, NOT_DISMISSED, appName, graphic, ownerId,
        expireSecsUtc, notificationCenterUrlTrack, processed);
  }

  public AptoideNotification(String abTestingGroup, String body, int campaignId, String img,
      String lang, String title, String url, String urlTrack, long timeStamp, int type,
      long dismissed, String appName, String graphic, String ownerId, Long expireSecsUtc,
      String notificationCenterUrlTrack, boolean processed) {
    this(body, img, title, url, type, timeStamp, appName, graphic, dismissed, ownerId,
        expireSecsUtc, urlTrack, notificationCenterUrlTrack, processed);
    this.abTestingGroup = abTestingGroup;
    this.campaignId = campaignId;
    this.lang = lang;
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

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ CAMPAIGN, COMMENT, LIKE, POPULAR, NEW_SHARE, NEW_ACTIVITY })
  public @interface NotificationType {
  }
}
