package cm.aptoide.pt.model.v7.timeline;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by jdandrade on 29/11/2016.
 */

@EqualsAndHashCode(exclude = { "ownerStore", "apps", "latestUpdate" })
public class SocialStoreLatestApps implements TimelineCard {

  @Getter private final String cardId;
  @Getter private final Store ownerStore;
  @Getter private final Store sharedStore;
  @Getter private final List<App> apps;
  @Getter private final Ab ab;
  @Getter private final long likes;
  @Getter private final long comments;
  @Getter private final Comment.User user;
  @Getter private final Comment.User userSharer;

  //private Date latestUpdate;

  @JsonCreator public SocialStoreLatestApps(@JsonProperty("uid") String cardId,
      @JsonProperty("stores") Stores stores, @JsonProperty("user") Comment.User user,
      @JsonProperty("stats") Review.Stats stats,
      @JsonProperty("user_sharer") Comment.User userSharer, @JsonProperty("apps") List<App> apps,
      @JsonProperty("ab") Ab ab) {
    this.user = user;
    this.ownerStore = stores.getUser();
    this.sharedStore = stores.getCard();
    this.cardId = cardId;
    this.userSharer = userSharer;
    this.apps = apps;
    this.ab = ab;
    this.likes = stats.getLikes();
    this.comments = stats.getComments();
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

  private static class Stores {
    @Getter private final Store user;
    @Getter private final Store card;

    @JsonCreator public Stores(@JsonProperty("user") Store user, @JsonProperty("card") Store card) {
      this.user = user;
      this.card = card;
    }
  }
}
