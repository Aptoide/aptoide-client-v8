package cm.aptoide.pt.v8engine.social.data;

import java.util.Date;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class Recommendation implements Card {
  private final String cardId;
  private final String appName;
  private final String appIcon;
  private final String relatedToAppName;
  private final Date timestamp;
  private final CardType cardType;
  private final String packageName;
  private final String abUrl;
  private final long appId;

  public Recommendation(String cardId, long appId, String packageName, String appName,
      String appIcon, String relatedToAppName, Date timestamp, String abUrl, CardType cardType) {
    this.cardId = cardId;
    this.appId = appId;
    this.packageName = packageName;
    this.appName = appName;
    this.appIcon = appIcon;
    this.relatedToAppName = relatedToAppName;
    this.timestamp = timestamp;
    this.abUrl = abUrl;
    this.cardType = cardType;
  }

  public long getAppId() {
    return appId;
  }

  public String getAbUrl() {
    return abUrl;
  }

  public String getAppName() {
    return appName;
  }

  public String getAppIcon() {
    return appIcon;
  }

  public String getRelatedToAppName() {
    return relatedToAppName;
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
}
