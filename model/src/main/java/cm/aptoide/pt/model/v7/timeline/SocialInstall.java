package cm.aptoide.pt.model.v7.timeline;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by jdandrade on 15/12/2016.
 */
@EqualsAndHashCode(exclude = { "app" }, callSuper = false) public class SocialInstall
    extends SocialCard implements TimelineCard {

  @Getter private final String cardId;
  @Getter private final App app;
  @Getter private final Ab ab;
  @Getter private final SocialCardStats stats;
  @Getter private final Store store;
  @Getter private final Comment.User user;
  @Getter private final Comment.User userSharer;
  @Getter private final Date date;

  @JsonCreator
  public SocialInstall(@JsonProperty("uid") String cardId, @JsonProperty("apps") List<App> apps,
      @JsonProperty("ab") Ab ab, @JsonProperty("user_sharer") Comment.User userSharer,
      @JsonProperty("user") Comment.User user, @JsonProperty("likes") List<UserTimeline> likes,
      @JsonProperty("comments") List<CardComment> comments,
      @JsonProperty("stats") SocialCardStats stats, @JsonProperty("my") My my,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("store") Store store) {
    super(likes, comments, my);
    this.ab = ab;
    this.date = date;
    this.cardId = cardId;
    this.user = user;
    this.userSharer = userSharer;
    this.stats = stats;
    this.store = store;
    if (!apps.isEmpty()) {
      this.app = apps.get(0);
    } else {
      this.app = null;
    }
  }
}
