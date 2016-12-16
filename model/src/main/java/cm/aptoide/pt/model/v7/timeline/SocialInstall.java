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
 * Created by jdandrade on 15/12/2016.
 */
@EqualsAndHashCode(exclude = { "app", "similarApps" }) public class SocialInstall
    implements TimelineCard {

  @Getter private final String cardId;
  @Getter private final App app;
  @Getter private final Ab ab;
  @Getter private final long likes;
  @Getter private final long comments;
  @Getter private final Review.Stats stats;
  @Getter private final Store store;
  @Getter private Comment.User user;

  @JsonCreator
  public SocialInstall(@JsonProperty("uid") String cardId, @JsonProperty("apps") List<App> apps,
      @JsonProperty("ab") Ab ab, @JsonProperty("user") Comment.User user,
      @JsonProperty("stats") Review.Stats stats, @JsonProperty("store") Store store) {
    this.ab = ab;
    this.cardId = cardId;
    this.user = user;
    this.stats = stats;
    this.store = store;
    this.likes = stats.getLikes();
    this.comments = stats.getComments();
    if (!apps.isEmpty()) {
      this.app = apps.get(0);
    } else {
      this.app = null;
    }
  }
}