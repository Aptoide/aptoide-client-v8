package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserTimeline;
import cm.aptoide.pt.v8engine.link.Link;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import cm.aptoide.pt.v8engine.social.data.publisher.Publisher;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 27/06/2017.
 */

public class SocialMedia extends Media {
  private final Poster poster;
  private final long commentsNumber;
  private final long likesNumber;
  private final List<UserTimeline> likes;
  private final List<SocialCard.CardComment> comments;
  private final String content;
  private String sharedByName;

  public SocialMedia(String cardId, Poster poster, String mediaTitle, String mediaThumbnailUrl,
      Date date, App app, String abTestURL, Publisher publisher, Link publisherLink, Link mediaLink,
      boolean isLiked, long commentsNumber, long likesNumber, List<UserTimeline> likes,
      List<SocialCard.CardComment> comments, String sharedByName, String content,
      CardType cardType) {
    super(cardId, mediaTitle, mediaThumbnailUrl, date, app, abTestURL, publisher, publisherLink,
        mediaLink, isLiked, cardType);
    this.poster = poster;
    this.commentsNumber = commentsNumber;
    this.likesNumber = likesNumber;
    this.likes = likes;
    this.comments = comments;
    this.sharedByName = sharedByName;
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public long getLikesNumber() {
    return likesNumber;
  }

  public List<UserTimeline> getLikes() {
    return likes;
  }

  public List<SocialCard.CardComment> getComments() {
    return comments;
  }

  public long getCommentsNumber() {
    return commentsNumber;
  }

  public Poster getPoster() {
    return poster;
  }

  public String getSharedByName() {
    return sharedByName;
  }
}
