package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
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
  private final Date date;
  private final List<App> apps;
  private final Ab ab;
  private final List<MinimalCard> minimalCardList;
  private final List<UserSharerTimeline> sharers;

  public AggregatedSocialStoreLatestApps(@JsonProperty("uid") String cardId,
      @JsonProperty("stores") SocialStoreLatestApps.Stores stores,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab,
      @JsonProperty("cards_shared") List<MinimalCard> minimalCardList,
      @JsonProperty("sharers") List<UserSharerTimeline> sharers) {
    this.cardId = cardId;
    this.ownerStore = stores.getUser();
    this.sharedStore = stores.getCard();
    this.date = date;
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

  public Date getDate() {
    return date;
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
