package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.link.Link;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import cm.aptoide.pt.v8engine.social.data.publisher.Publisher;
import java.util.Date;

/**
 * Created by jdandrade on 27/06/2017.
 */

public class SocialMedia extends Media {
  private final Poster poster;

  public SocialMedia(String cardId, Poster poster, String mediaTitle, String mediaThumbnailUrl,
      Date date, App app, String abTestURL, Publisher publisher, Link publisherLink, Link mediaLink,
      CardType cardType) {
    super(cardId, mediaTitle, mediaThumbnailUrl, date, app, abTestURL, publisher, publisherLink,
        mediaLink, cardType);
    this.poster = poster;
  }

  public Poster getPoster() {
    return poster;
  }
}
