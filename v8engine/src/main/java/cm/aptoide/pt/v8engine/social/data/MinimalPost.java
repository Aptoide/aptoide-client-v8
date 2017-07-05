package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import java.util.Date;
import java.util.List;

/**
 * Created by Jdandrade on 7/4/2017.
 */

public class MinimalPost implements Post {
  private final String cardId;
  private final List<Poster> minimalPostPosters;
  private final Date date;
  private final CardType cardType;
  private final boolean liked;

  public MinimalPost(String cardId, List<Poster> minimalPostPosters, Date date, boolean liked,
      CardType cardType) {
    this.cardId = cardId;
    this.minimalPostPosters = minimalPostPosters;
    this.date = date;
    this.cardType = cardType;
    this.liked = liked;
  }

  public Date getDate() {
    return date;
  }

  public List<Poster> getMinimalPostPosters() {
    return minimalPostPosters;
  }

  public boolean isLiked() {
    return liked;
  }

  @Override public String getCardId() {
    return cardId;
  }

  @Override public CardType getType() {
    return cardType;
  }
}
