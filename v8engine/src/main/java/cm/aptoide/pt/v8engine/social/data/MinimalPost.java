package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserTimeline;
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
  private final long commentsNumber;
  private final long likesNumber;
  private final List<UserTimeline> likes;
  private final List<SocialCard.CardComment> comments;
  private final CardType cardType;
  private boolean liked;
  private boolean likedFromClick;

  public MinimalPost(String cardId, List<Poster> minimalPostPosters, Date date, boolean liked,
      long commentsNumber, long likesNumber, List<UserTimeline> likes,
      List<SocialCard.CardComment> comments, CardType cardType) {
    this.cardId = cardId;
    this.minimalPostPosters = minimalPostPosters;
    this.date = date;
    this.commentsNumber = commentsNumber;
    this.likesNumber = likesNumber;
    this.likes = likes;
    this.comments = comments;
    this.cardType = cardType;
    this.liked = liked;
  }

  public long getCommentsNumber() {
    return commentsNumber;
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

  public Date getDate() {
    return date;
  }

  public List<Poster> getMinimalPostPosters() {
    return minimalPostPosters;
  }

  @Override public String getCardId() {
    return cardId;
  }

  @Override public CardType getType() {
    return cardType;
  }

  @Override public String getAbUrl() {
    //supposed to return null
    return null;
  }

  public boolean isLiked() {
    return liked;
  }

  @Override public void setLiked(boolean liked) {
    this.liked = liked;
    this.likedFromClick = true;
  }

  @Override public boolean isLikeFromClick() {
    return likedFromClick;
  }

  public void setLikedFromClick(boolean likedFromClick) {
    this.likedFromClick = likedFromClick;
  }
}
