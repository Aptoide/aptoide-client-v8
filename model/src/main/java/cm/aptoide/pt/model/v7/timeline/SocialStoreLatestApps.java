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

@EqualsAndHashCode(exclude = { "store", "apps", "latestUpdate" }) public class SocialStoreLatestApps
    implements TimelineCard {

  @Getter private final String cardId;
  @Getter private final Store store;
  @Getter private final List<App> apps;
  @Getter private final Ab ab;
  @Getter private final long likes;
  @Getter private final long comments;

  private Date latestUpdate;

  @JsonCreator public SocialStoreLatestApps(@JsonProperty("uid") String cardId,
      @JsonProperty("store") Store store, @JsonProperty("apps") List<App> apps,
      @JsonProperty("user") Comment.User user, @JsonProperty("stats") Review.Stats stats,
      @JsonProperty("ab") Ab ab) {
    this.cardId = cardId;
    this.store = store;
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
