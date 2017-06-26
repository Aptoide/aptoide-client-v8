package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class PopularApp implements Post {
  private final String cardId;
  private final String appIcon;
  private final String appName;
  private final String packageName;
  private final Date timestamp;
  private final List<Comment.User> users;
  private final float ratingAverage;
  private final long appId;
  private final String abUrl;
  private final CardType cardType;

  public PopularApp(String cardId, String appIcon, String appName, String packageName,
      Date timestamp, List<Comment.User> users, float ratingAverage, long appId, String abUrl,
      CardType cardType) {
    this.cardId = cardId;
    this.appIcon = appIcon;
    this.appName = appName;
    this.packageName = packageName;
    this.timestamp = timestamp;
    this.users = users;
    this.ratingAverage = ratingAverage;
    this.appId = appId;
    this.abUrl = abUrl;
    this.cardType = cardType;
  }

  public String getAppIcon() {
    return appIcon;
  }

  public String getAppName() {
    return appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public List<Comment.User> getUsers() {
    return users;
  }

  public float getRatingAverage() {
    return ratingAverage;
  }

  public long getAppId() {
    return appId;
  }

  public String getAbUrl() {
    return abUrl;
  }

  @Override public String getCardId() {
    return cardId;
  }

  @Override public CardType getType() {
    return cardType;
  }
}
