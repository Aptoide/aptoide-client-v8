package cm.aptoide.pt.model.v7.timeline;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialInstall extends SocialInstall {

  private final List<MinimalCard> minimalCardList;
  private final List<UserTimeline> sharers;

  public AggregatedSocialInstall(@JsonProperty("uid") String cardId,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab,
      @JsonProperty("user_sharer") Comment.User userSharer, @JsonProperty("user") Comment.User user,
      @JsonProperty("likes") List<UserTimeline> likes,
      @JsonProperty("comments") List<CardComment> comments,
      @JsonProperty("stats") SocialCardStats stats, @JsonProperty("my") My my,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("store") Store store,
      @JsonProperty("cards_shared") List<MinimalCard> minimalCardList,
      @JsonProperty("sharers") List<UserTimeline> sharers) {
    super(cardId, apps, ab, userSharer, user, likes, comments, stats, my, date, store);
    this.minimalCardList = minimalCardList;
    this.sharers = sharers;
  }

  public List<MinimalCard> getMinimalCardList() {
    return minimalCardList;
  }

  public List<UserTimeline> getSharers() {
    return sharers;
  }
}
