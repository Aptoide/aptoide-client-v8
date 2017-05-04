package cm.aptoide.pt.database.realm;

import android.support.annotation.IntDef;
import io.realm.RealmObject;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import lombok.ToString;

/**
 * Created by trinkes on 03/05/2017.
 */

@ToString(of = { "title" }) public class Notification extends RealmObject {
  public static final int NOT_EXISTS = -1;
  public static final int CAMPAIGN = 0;
  public static final int DIRECT = 1;

  private String abTestingGroup;
  private String body;
  private int campaignId;
  private String img;
  private String lang;
  private String title;
  private String url;
  private String urlTrack;
  private @NotificationType int type;
  private long timeStamp;
  private boolean showed;

  public Notification() {
  }

  public Notification(String body, String img, String title, String url) {
    this(body, img, title, url, DIRECT);
  }

  private Notification(String body, String img, String title, String url, int type) {
    this.body = body;
    this.img = img;
    this.title = title;
    this.url = url;
    this.type = type;
    this.timeStamp = System.currentTimeMillis();
  }

  public Notification(String abTestingGroup, String body, int campaignId, String img, String lang,
      String title, String url, String urlTrack) {
    this(body, img, title, url, CAMPAIGN);
    this.abTestingGroup = abTestingGroup;
    this.campaignId = campaignId;
    this.lang = lang;
    this.urlTrack = urlTrack;
  }

  public static Notification createEmptyNotification() {
    Notification notification = new Notification();
    notification.type = NOT_EXISTS;
    return notification;
  }

  public boolean isShowed() {
    return showed;
  }

  public void setShowed(boolean showed) {
    this.showed = showed;
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

  public long getCampaignId() {
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

  @Retention(RetentionPolicy.SOURCE) @IntDef({ CAMPAIGN, DIRECT, NOT_EXISTS })
  public @interface NotificationType {
  }
}
