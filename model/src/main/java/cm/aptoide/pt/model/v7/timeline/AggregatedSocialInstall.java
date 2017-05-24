package cm.aptoide.pt.model.v7.timeline;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.Getter;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialInstall implements TimelineCard {

  private final List<MinimalCard> minimalCardList;
  private final List<UserSharerTimeline> sharers;
  @Getter private final String cardId;
  @Getter private final App app;
  @Getter private final Ab ab;
  @Getter private final Store store;
  @Getter private final Comment.User user;
  @Getter private final Comment.User userSharer;
  @Getter private final Date date;

  public AggregatedSocialInstall(@JsonProperty("uid") String cardId,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab,
      @JsonProperty("user_sharer") Comment.User userSharer, @JsonProperty("user") Comment.User user,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("store") Store store,
      @JsonProperty("cards_shared") List<MinimalCard> minimalCardList,
      @JsonProperty("sharers") List<UserSharerTimeline> sharers) {
    this.minimalCardList = minimalCardList;
    this.sharers = sharers;
    this.cardId = cardId;
    this.ab = ab;
    this.store = store;
    this.user = user;
    this.userSharer = userSharer;
    this.date = date;
    if (!apps.isEmpty()) {
      this.app = apps.get(0);
    } else {
      this.app = null;
    }
  }

  public List<MinimalCard> getMinimalCardList() {
    return minimalCardList;
  }

  public List<UserSharerTimeline> getSharers() {
    return sharers;
  }
}
