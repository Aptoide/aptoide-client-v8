package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.v8engine.social.data.publisher.Publisher;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 26/06/2017.
 */

public class PopularApp extends AppPost {
  private final float ratingAverage;
  private final List<Publisher> publishers;

  public PopularApp(String cardId, long appId, String packageName, String appName,
      String appIcon, float ratingAverage, List<Publisher> publishers, Date timestamp, String abUrl,
      CardType cardType) {
    super(cardId, appIcon, appName, appId, packageName, timestamp, abUrl, cardType);
    this.ratingAverage = ratingAverage;
    this.publishers = publishers;
  }

  public List<Publisher> getPublishers() {
    return publishers;
  }

  public float getRatingAverage() {
    return ratingAverage;
  }
}
