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
 * Created by jdandrade on 28/11/2016.
 */
@EqualsAndHashCode(exclude = { "publisher" }, callSuper = false) public class SocialVideo
    extends SocialCard implements TimelineCard {

  @Getter private final Publisher publisher;
  @Getter private final String cardId;
  @Getter private final String title;
  @Getter private final String thumbnailUrl;
  @Getter private final String url;
  @Getter private final Comment.User user;
  @Getter private final Comment.User userSharer;
  @Getter private final Store store;
  @Getter private final SocialCardStats stats;
  @Getter private final Date date;
  @Getter private final List<App> apps;
  @Getter private final Ab ab;

  @JsonCreator
  public SocialVideo(@JsonProperty("uid") String cardId, @JsonProperty("title") String title,
      @JsonProperty("thumbnail") String thumbnailUrl,
      @JsonProperty("publisher") Publisher publisher, @JsonProperty("user") Comment.User user,
      @JsonProperty("user_sharer") Comment.User userSharer, @JsonProperty("my") My my,
      @JsonProperty("stats") SocialCardStats stats, @JsonProperty("url") String url,
      @JsonProperty("store") Store store, @JsonProperty("likes") List<UserTimeline> likes,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab) {
    super(likes, my);
    this.publisher = publisher;
    this.store = store;
    this.user = user;
    this.stats = stats;
    this.userSharer = userSharer;
    this.cardId = cardId;
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.url = url;
    this.date = date;
    this.apps = apps;
    this.ab = ab;
  }
}
