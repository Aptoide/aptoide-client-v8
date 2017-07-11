package cm.aptoide.pt.v8engine.social.data;

import java.util.Date;

/**
 * Created by jdandrade on 26/06/2017.
 */

abstract class AppPost implements Post {
  protected final String cardId;
  protected final String appName;
  protected final String appIcon;
  protected final Date timestamp;
  protected final CardType cardType;
  protected final String packageName;
  protected final String abUrl;
  protected final long appId;
  protected final float appAverageRating;

  AppPost(String cardId, String appIcon, String appName, long appId, String packageName,
      Date timestamp, String abUrl, CardType cardType, float appAverageRating) {
    this.abUrl = abUrl;
    this.cardId = cardId;
    this.timestamp = timestamp;
    this.appName = appName;
    this.appId = appId;
    this.packageName = packageName;
    this.cardType = cardType;
    this.appIcon = appIcon;
    this.appAverageRating = appAverageRating;
  }

  public float getAppAverageRating() {
    return appAverageRating;
  }

  public long getAppId() {
    return appId;
  }

  public String getAppName() {
    return appName;
  }

  public String getAppIcon() {
    return appIcon;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public String getPackageName() {
    return packageName;
  }

  @Override public String getCardId() {
    return this.cardId;
  }

  @Override public CardType getType() {
    return this.cardType;
  }

  @Override public String getAbUrl() {
    return abUrl;
  }
}
