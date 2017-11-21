package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 29/11/2016.
 */

public class SocialStoreLatestApps extends SocialCard implements TimelineCard {

  private final String cardId;
  private final Store ownerStore;
  private final Store sharedStore;
  private final List<App> apps;
  private final Ab ab;
  private final SocialCardStats stats;
  private final Comment.User user;
  private final Comment.User userSharer;
  private final Date date;

  //private Date latestUpdate;

  @JsonCreator public SocialStoreLatestApps(@JsonProperty("uid") String cardId,
      @JsonProperty("stores") Stores stores, @JsonProperty("user") Comment.User user,
      @JsonProperty("stats") SocialCardStats stats, @JsonProperty("likes") List<UserTimeline> likes,
      @JsonProperty("comments") List<CardComment> comments, @JsonProperty("my") My my,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("user_sharer") Comment.User userSharer, @JsonProperty("apps") List<App> apps,
      @JsonProperty("ab") Ab ab, @JsonProperty("urls") Urls urls) {
    super(likes, comments, my, urls);
    this.user = user;
    this.date = date;
    this.ownerStore = stores.getUser();
    this.sharedStore = stores.getCard();
    this.cardId = cardId;
    this.userSharer = userSharer;
    this.apps = apps;
    this.ab = ab;
    this.stats = stats;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $cardId = this.cardId;
    result = result * PRIME + ($cardId == null ? 43 : $cardId.hashCode());
    final Object $sharedStore = this.sharedStore;
    result = result * PRIME + ($sharedStore == null ? 43 : $sharedStore.hashCode());
    final Object $ab = this.ab;
    result = result * PRIME + ($ab == null ? 43 : $ab.hashCode());
    final Object $stats = this.stats;
    result = result * PRIME + ($stats == null ? 43 : $stats.hashCode());
    final Object $user = this.user;
    result = result * PRIME + ($user == null ? 43 : $user.hashCode());
    final Object $userSharer = this.userSharer;
    result = result * PRIME + ($userSharer == null ? 43 : $userSharer.hashCode());
    final Object $date = this.date;
    result = result * PRIME + ($date == null ? 43 : $date.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SocialStoreLatestApps)) return false;
    final SocialStoreLatestApps other = (SocialStoreLatestApps) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$cardId = this.cardId;
    final Object other$cardId = other.cardId;
    if (this$cardId == null ? other$cardId != null : !this$cardId.equals(other$cardId)) {
      return false;
    }
    final Object this$sharedStore = this.sharedStore;
    final Object other$sharedStore = other.sharedStore;
    if (this$sharedStore == null ? other$sharedStore != null
        : !this$sharedStore.equals(other$sharedStore)) {
      return false;
    }
    final Object this$ab = this.ab;
    final Object other$ab = other.ab;
    if (this$ab == null ? other$ab != null : !this$ab.equals(other$ab)) return false;
    final Object this$stats = this.stats;
    final Object other$stats = other.stats;
    if (this$stats == null ? other$stats != null : !this$stats.equals(other$stats)) return false;
    final Object this$user = this.user;
    final Object other$user = other.user;
    if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
    final Object this$userSharer = this.userSharer;
    final Object other$userSharer = other.userSharer;
    if (this$userSharer == null ? other$userSharer != null
        : !this$userSharer.equals(other$userSharer)) {
      return false;
    }
    final Object this$date = this.date;
    final Object other$date = other.date;
    if (this$date == null ? other$date != null : !this$date.equals(other$date)) return false;
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof SocialStoreLatestApps;
  }

  public String getCardId() {
    return this.cardId;
  }

  public Store getOwnerStore() {
    return this.ownerStore;
  }

  public Store getSharedStore() {
    return this.sharedStore;
  }

  public List<App> getApps() {
    return this.apps;
  }

  public Ab getAb() {
    return this.ab;
  }

  public SocialCardStats getStats() {
    return this.stats;
  }

  public Comment.User getUser() {
    return this.user;
  }

  public Comment.User getUserSharer() {
    return this.userSharer;
  }

  public Date getDate() {
    return this.date;
  }

  //public Date getLatestUpdate() {
  //  if (latestUpdate == null) {
  //  for (App app : apps) {
  //    if (latestUpdate == null || (app.getUpdated() != null
  //        && app.getUpdated().getTime() > latestUpdate.getTime())) {
  //      latestUpdate = app.getUpdated();
  //    }
  //  }
  //  }
  //  return latestUpdate;
  //}

  protected static class Stores {
    private final Store user;
    private final Store card;

    @JsonCreator public Stores(@JsonProperty("user") Store user, @JsonProperty("card") Store card) {
      this.user = user;
      this.card = card;
    }

    public Store getUser() {
      return this.user;
    }

    public Store getCard() {
      return this.card;
    }
  }
}
