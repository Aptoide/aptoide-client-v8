package cm.aptoide.pt.model.v7.timeline;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
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

  private Date latestUpdate;

  @JsonCreator public SocialStoreLatestApps(@JsonProperty("uid") String cardId,
      @JsonProperty("stores") List<Store> stores, @JsonProperty("apps") List<App> apps,
      @JsonProperty("user") Comment.User user, @JsonProperty("stats") Review.Stats stats,
      @JsonProperty("ab") Ab ab) {
    this.user = user;
    this.cardId = cardId;
    this.ownerStore = stores.get(0);
    this.sharedStore = stores.get(1);
    this.apps = apps;
    this.ab = ab;
    this.likes = stats.getLikes();
    this.comments = stats.getComments();
  }

  public Date getLatestUpdate() {
    if (latestUpdate == null) {
      for (App app : apps) {
        if (latestUpdate == null || (app.getUpdated() != null
            && app.getUpdated().getTime() > latestUpdate.getTime())) {
          latestUpdate = app.getUpdated();
        }
      }
    }
    return latestUpdate;
  }
}
