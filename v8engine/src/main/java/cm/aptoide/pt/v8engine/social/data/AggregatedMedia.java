package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.link.Link;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import cm.aptoide.pt.v8engine.social.data.publisher.Publisher;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 28/06/2017.
 */

public class AggregatedMedia extends Media {
  private final List<Poster> posters;
  private final List<Post> minimalCards;

  public AggregatedMedia(String cardId, String userContent, List<Poster> posters, String mediaTitle,
      String mediaThumbnailUrl, Date date, App app, String abTestURL, Publisher publisher,
      Link publisherLink, Link mediaLink, List<Post> minimalPosts, CardType cardType) {
    super(cardId, userContent, mediaTitle, mediaThumbnailUrl, date, app, abTestURL, publisher,
        publisherLink, mediaLink, false, cardType);
    this.posters = posters;
    this.minimalCards = minimalPosts;
  }

  public List<Post> getMinimalCards() {
    return minimalCards;
  }

  public List<Poster> getPosters() {
    return posters;
  }

  @Override public String getCardId() {
    throw new RuntimeException("Aggregated cards have NO card id");
  }
}
