package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.timeline.UserSharerTimeline;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class PopularApp extends AppPost {
  private final float ratingAverage;
  private final List<UserSharerTimeline.User> users;

  public PopularApp(String cardId, long appId, String packageName, String appName, String appIcon,
      float ratingAverage, List<UserSharerTimeline.User> users, Date timestamp, String abUrl,
      CardType cardType) {
    super(cardId, appIcon, appName, appId, packageName, timestamp, abUrl, cardType);
    this.ratingAverage = ratingAverage;
    this.users = users;
  }

  public List<UserSharerTimeline.User> getUsers() {
    return users;
  }

  public float getRatingAverage() {
    return ratingAverage;
  }
}
