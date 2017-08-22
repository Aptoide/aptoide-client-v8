package cm.aptoide.pt.social.data;

import cm.aptoide.pt.social.data.publisher.Poster;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 29/06/2017.
 */

public class AggregatedRecommendation extends AppPost {
  private final List<Poster> posters;
  private final List<Post> minimalPosts;

  AggregatedRecommendation(String cardId, List<Poster> posters, List<Post> minimalPosts,
      String appIcon, String appName, long appId, float appAverageRating, Long storeId,
      String packageName, Date timestamp, String abUrl, CardType cardType, String markAsReadUrl) {
    super(cardId, appIcon, appName, appId, packageName, timestamp, abUrl, cardType,
        appAverageRating, storeId, false, markAsReadUrl);
    this.posters = posters;
    this.minimalPosts = minimalPosts;
  }

  public List<Post> getMinimalPosts() {
    return minimalPosts;
  }

  public List<Poster> getPosters() {
    return posters;
  }

  @Override public String getCardId() {
    throw new RuntimeException("Aggregated cards have NO card id");
  }
}
