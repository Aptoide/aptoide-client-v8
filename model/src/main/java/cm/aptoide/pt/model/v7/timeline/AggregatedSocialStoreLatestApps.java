package cm.aptoide.pt.model.v7.timeline;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 18/05/2017.
 */

public class AggregatedSocialStoreLatestApps implements TimelineCard {

  private final String cardId;
  private final Store ownerStore;
  private final Store sharedStore;
  private final Comment.User user;
  private final Date date;
  private final Comment.User userSharer;
  private final List<App> apps;
  private final Ab ab;
  private final List<MinimalCard> minimalCardList;
  private final List<UserSharerTimeline> sharers;

  public AggregatedSocialStoreLatestApps(@JsonProperty("uid") String cardId,
      @JsonProperty("stores") SocialStoreLatestApps.Stores stores,
      @JsonProperty("user") Comment.User user,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("user_sharer") Comment.User userSharer, @JsonProperty("apps") List<App> apps,
      @JsonProperty("ab") Ab ab, @JsonProperty("cards_shared") List<MinimalCard> minimalCardList,
      @JsonProperty("sharers") List<UserSharerTimeline> sharers) {
    this.cardId = cardId;
    this.ownerStore = stores.getUser();
    this.sharedStore = stores.getCard();
    this.user = user;
    this.date = date;
    this.userSharer = userSharer;
    this.apps = apps;
    this.ab = ab;
    this.minimalCardList = minimalCardList;
    this.sharers = sharers;
  }

  @Override public String getCardId() {
    return this.cardId;
  }

  public Store getOwnerStore() {
    return ownerStore;
  }

  public Store getSharedStore() {
    return sharedStore;
  }

  public Comment.User getUser() {
    return user;
  }

  public Date getDate() {
    return date;
  }

  public Comment.User getUserSharer() {
    return userSharer;
  }

  public List<App> getApps() {
    return apps;
  }

  public Ab getAb() {
    return ab;
  }

  public List<MinimalCard> getMinimalCardList() {
    return minimalCardList;
  }

  public List<UserSharerTimeline> getSharers() {
    return sharers;
  }
}
