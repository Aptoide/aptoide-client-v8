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
 * Created by jdandrade on 20/12/2016.
 */
@EqualsAndHashCode public class SocialRecommendation implements TimelineCard {

  @Getter private final String cardId;
  @Getter private final App app;
  @Getter private final Ab ab;
  @Getter private final long likes;
  @Getter private final long comments;
  @Getter private final Review.Stats stats;
  @Getter private final Store store;
  @Getter private final Comment.User userSharer;
  @Getter private Comment.User user;

  @JsonCreator public SocialRecommendation(@JsonProperty("uid") String cardId,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab,
      @JsonProperty("user_sharer") Comment.User userSharer, @JsonProperty("user") Comment.User user,
      @JsonProperty("stats") Review.Stats stats, @JsonProperty("store") Store store) {
    this.ab = ab;
    this.cardId = cardId;
    this.user = user;
    this.userSharer = userSharer;
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
