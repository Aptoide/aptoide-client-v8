package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.v8engine.social.data.publisher.Publisher;
import java.util.Date;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class Recommendation extends AppPost {
  private final String relatedToAppName;
  private final int publisherDrawableId;

  public Recommendation(String cardId, long appId, String packageName, String appName,
      String appIcon, String relatedToAppName, Publisher publisher, Date timestamp, String abUrl,
      CardType cardType) {
    super(cardId, publisher, appIcon, appName, appId, packageName, timestamp, abUrl, cardType);
    this.publisherDrawableId = getPublisherAvatar().getDrawableId();
    this.relatedToAppName = relatedToAppName;
  }

  public String getRelatedToAppName() {
    return relatedToAppName;
  }

  public int getPublisherDrawableId() {
    return publisherDrawableId;
  }
}
