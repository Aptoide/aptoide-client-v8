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

@EqualsAndHashCode(exclude = { "publisher" }, callSuper = false) public class SocialArticle
    extends SocialCard implements TimelineCard {

  @Getter private final Publisher publisher;
  @Getter private final Comment.User user;
  @Getter private final Comment.User userSharer;
  @Getter private final SocialCardStats stats;
  @Getter private final Store store;
  @Getter private final String cardId;
  @Getter private final String title;
  @Getter private final String thumbnailUrl;
  @Getter private final String url;
  @Getter private final Date date;
  @Getter private final List<App> apps;
  @Getter private final Ab ab;

  @JsonCreator
  public SocialArticle(@JsonProperty("uid") String cardId, @JsonProperty("title") String title,
      @JsonProperty("thumbnail") String thumbnailUrl,
      @JsonProperty("publisher") Publisher publisher,
      @JsonProperty("user_sharer") Comment.User userSharer, @JsonProperty("user") Comment.User user,
      @JsonProperty("stats") SocialCardStats stats, @JsonProperty("store") Store store,
      @JsonProperty("my") My my, @JsonProperty("url") String url,
      @JsonProperty("likes") List<UserTimeline> likes,
      @JsonProperty("comments") List<CardComment> comments,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab) {
    super(likes, comments, my);
    this.stats = stats;
    this.publisher = publisher;
    this.cardId = cardId;
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.url = url;
    this.date = date;
    this.apps = apps;
    this.ab = ab;
    this.user = user;
    this.userSharer = userSharer;
    this.store = store;
  }
}
