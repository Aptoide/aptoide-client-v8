package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.v8engine.social.data.publisher.Publisher;
import java.util.Date;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class Recommendation extends AppPost {
  private final String relatedToAppName;
  private final String relatedToPackageName;
  private final String publisherName;
  private final int publisherDrawableId;

  public Recommendation(String cardId, long appId, String packageName, String appName,
      String appIcon, float appAverageRating, Long storeId, String relatedToAppName,
      String relatedToPackageName, Publisher publisher, Date timestamp, String abUrl,
      boolean isLiked, CardType cardType) {
    super(cardId, appIcon, appName, appId, packageName, timestamp, abUrl, cardType,
        appAverageRating, storeId, isLiked);
    this.relatedToPackageName = relatedToPackageName;
    this.publisherName = publisher.getPublisherName();
    this.publisherDrawableId = publisher.getPublisherAvatar()
        .getDrawableId();
    this.relatedToAppName = relatedToAppName;
  }

  public String getRelatedToPackageName() {
    return relatedToPackageName;
  }

  public String getPublisherName() {
    return publisherName;
  }

  public String getRelatedToAppName() {
    return relatedToAppName;
  }

  public int getPublisherDrawableId() {
    return publisherDrawableId;
  }
}
