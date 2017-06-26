package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class PopularApp extends AppPost {
  private final List<Comment.User> users;
  private final float ratingAverage;

  public PopularApp(String cardId, String appIcon, String appName, String packageName,
      Date timestamp, List<Comment.User> users, float ratingAverage, long appId, String abUrl,
      CardType cardType) {
    super(cardId, null, appIcon, appName, appId, packageName, timestamp, abUrl, cardType);
    this.users = users;
    this.ratingAverage = ratingAverage;
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
    return cardId;
  }

  @Override public CardType getType() {
    return cardType;
  }
}
