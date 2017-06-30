package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 29/06/2017.
 */

public class AggregatedRecommendation extends AppPost {
  private final List<Poster> posters;
  private final List<MinimalCard> minimalCards;
  private final float appAverageRating;

  AggregatedRecommendation(String cardId, List<Poster> posters, List<MinimalCard> minimalCards,
      String appIcon, String appName, long appId, float appAverageRating, String packageName,
      Date timestamp, String abUrl, CardType cardType) {
    super(cardId, appIcon, appName, appId, packageName, timestamp, abUrl, cardType);
    this.posters = posters;
    this.minimalCards = minimalCards;
    this.appAverageRating = appAverageRating;
  }

  public List<MinimalCard> getMinimalCards() {
    return minimalCards;
  }

  public List<Poster> getPosters() {
    return posters;
  }

  @Override public String getCardId() {
    throw new RuntimeException("Aggregated cards have NO card id");
  }

  public float getAppAverageRating() {
    return appAverageRating;
  }
}
