package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserTimeline;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 26/06/2017.
 *
 * This class represents the Social Recommendation + Social Install cards.
 */

public class RatedRecommendation extends AppPost {
  private final Poster poster;
  private final boolean isLiked;
  private final long commentsNumber;
  private final long likesNumber;
  private final List<UserTimeline> likes;
  private final List<SocialCard.CardComment> comments;

  public RatedRecommendation(String cardId, Poster poster, long appId, String packageName,
      String appName, String appIcon, float ratingAverage, Date timestamp, String abUrl,
      boolean isLiked, long commentsNumber, long likesNumber, List<UserTimeline> likes,
      List<SocialCard.CardComment> comments, CardType cardType) {
    super(cardId, appIcon, appName, appId, packageName, timestamp, abUrl, cardType, ratingAverage);
    this.poster = poster;
    this.isLiked = isLiked;
    this.commentsNumber = commentsNumber;
    this.likesNumber = likesNumber;
    this.likes = likes;
    this.comments = comments;
  }

  public long getCommentsNumber() {
    return commentsNumber;
  }

  public long getLikesNumber() {
    return likesNumber;
  }

  public Poster getPoster() {
    return poster;
  }

  public boolean isLiked() {
    return isLiked;
  }

  public List<UserTimeline> getLikes() {
    return likes;
  }

  public List<SocialCard.CardComment> getComments() {
    return comments;
  }
}
