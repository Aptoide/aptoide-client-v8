package cm.aptoide.pt.v8engine.social.data;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserTimeline;
import cm.aptoide.pt.v8engine.social.data.publisher.Poster;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 28/06/2017.
 */

public class SocialStore extends StoreLatestApps {
  private final Poster poster;
  private final long commentsNumber;
  private final long likesNumber;
  private final List<UserTimeline> likes;
  private final List<SocialCard.CardComment> comments;
  private final String sharedByName;

  public SocialStore(String cardId, Poster poster, Long storeId, String storeName,
      String storeAvatar, String socialStore, int subscribers, int appsNumber, Date latestUpdate,
      List<App> apps, String abUrl, boolean isLiked, long commentsNumber, long likesNumber,
      List<UserTimeline> likes, List<SocialCard.CardComment> comments, String sharedByName,
      CardType cardType) {
    super(cardId, storeId, storeName, storeAvatar, socialStore, subscribers, appsNumber,
        latestUpdate, apps, abUrl, isLiked, cardType);
    this.poster = poster;
    this.commentsNumber = commentsNumber;
    this.likesNumber = likesNumber;
    this.likes = likes;
    this.comments = comments;
    this.sharedByName = sharedByName;
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
