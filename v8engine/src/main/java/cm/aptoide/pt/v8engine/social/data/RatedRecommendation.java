package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import java.util.Date;

/**
 * Created by jdandrade on 26/06/2017.
 *
 * This class represents the Social Recommendation + Social Install cards.
 */

public class RatedRecommendation extends AppPost {
  private final Poster poster;
  private final float ratingAverage;
  private final boolean isLiked;

  public RatedRecommendation(String cardId, Poster poster, long appId, String packageName,
      String appName, String appIcon, float ratingAverage, Date timestamp, String abUrl,
      boolean isLiked, CardType cardType) {
    super(cardId, appIcon, appName, appId, packageName, timestamp, abUrl, cardType);
    this.poster = poster;
    this.ratingAverage = ratingAverage;
    this.isLiked = isLiked;
  }

  public Poster getPoster() {
    return poster;
  }

  public float getRatingAverage() {
    return ratingAverage;
  }

  public boolean isLiked() {
    return isLiked;
  }
}
