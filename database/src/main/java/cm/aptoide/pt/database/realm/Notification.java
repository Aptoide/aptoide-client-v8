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
  private @NotificationType int notificationType;

  public Notification() {
  }

  public Notification(String body, String img, String title, String url) {
    this(body, img, title, url, DIRECT);
  }

  private Notification(String body, String img, String title, String url, int notificationType) {
    this.body = body;
    this.img = img;
    this.title = title;
    this.url = url;
    this.notificationType = notificationType;
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
    notification.notificationType = NOT_EXISTS;
    return notification;
  }

  public @NotificationType int getNotificationType() {
    return notificationType;
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

  @Retention(RetentionPolicy.SOURCE) @IntDef({ CAMPAIGN, DIRECT, NOT_EXISTS })
  public @interface NotificationType {
  }
}
