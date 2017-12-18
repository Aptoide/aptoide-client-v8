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
 * Created by jdandrade on 28/11/2016.
 */
public class SocialVideo extends SocialCard implements TimelineCard {

  private final Publisher publisher;
  private final String cardId;
  private final String title;
  private final String thumbnailUrl;
  private final String url;
  private final String content;
  private final Comment.User user;
  private final Comment.User userSharer;
  private final Store store;
  private final SocialCardStats stats;
  private final Date date;
  private final List<App> apps;
  private final Ab ab;

  @JsonCreator
  public SocialVideo(@JsonProperty("uid") String cardId, @JsonProperty("title") String title,
      @JsonProperty("thumbnail") String thumbnailUrl,
      @JsonProperty("publisher") Publisher publisher, @JsonProperty("content") String content,
      @JsonProperty("user") Comment.User user, @JsonProperty("user_sharer") Comment.User userSharer,
      @JsonProperty("my") My my, @JsonProperty("stats") SocialCardStats stats,
      @JsonProperty("url") String url, @JsonProperty("store") Store store,
      @JsonProperty("likes") List<UserTimeline> likes,
      @JsonProperty("comments") List<CardComment> comments,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab,
      @JsonProperty("urls") Urls urls) {
    super(likes, comments, my, urls);
    this.publisher = publisher;
    this.content = content;
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

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $cardId = this.cardId;
    result = result * PRIME + ($cardId == null ? 43 : $cardId.hashCode());
    final Object $title = this.title;
    result = result * PRIME + ($title == null ? 43 : $title.hashCode());
    final Object $thumbnailUrl = this.thumbnailUrl;
    result = result * PRIME + ($thumbnailUrl == null ? 43 : $thumbnailUrl.hashCode());
    final Object $url = this.url;
    result = result * PRIME + ($url == null ? 43 : $url.hashCode());
    final Object $content = this.content;
    result = result * PRIME + ($content == null ? 43 : $content.hashCode());
    final Object $user = this.user;
    result = result * PRIME + ($user == null ? 43 : $user.hashCode());
    final Object $userSharer = this.userSharer;
    result = result * PRIME + ($userSharer == null ? 43 : $userSharer.hashCode());
    final Object $store = this.store;
    result = result * PRIME + ($store == null ? 43 : $store.hashCode());
    final Object $stats = this.stats;
    result = result * PRIME + ($stats == null ? 43 : $stats.hashCode());
    final Object $date = this.date;
    result = result * PRIME + ($date == null ? 43 : $date.hashCode());
    final Object $apps = this.apps;
    result = result * PRIME + ($apps == null ? 43 : $apps.hashCode());
    final Object $ab = this.ab;
    result = result * PRIME + ($ab == null ? 43 : $ab.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SocialVideo)) return false;
    final SocialVideo other = (SocialVideo) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$cardId = this.cardId;
    final Object other$cardId = other.cardId;
    if (this$cardId == null ? other$cardId != null : !this$cardId.equals(other$cardId)) {
      return false;
    }
    final Object this$title = this.title;
    final Object other$title = other.title;
    if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
    final Object this$thumbnailUrl = this.thumbnailUrl;
    final Object other$thumbnailUrl = other.thumbnailUrl;
    if (this$thumbnailUrl == null ? other$thumbnailUrl != null
        : !this$thumbnailUrl.equals(other$thumbnailUrl)) {
      return false;
    }
    final Object this$url = this.url;
    final Object other$url = other.url;
    if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
    final Object this$content = this.content;
    final Object other$content = other.content;
    if (this$content == null ? other$content != null : !this$content.equals(other$content)) {
      return false;
    }
    final Object this$user = this.user;
    final Object other$user = other.user;
    if (this$user == null ? other$user != null : !this$user.equals(other$user)) return false;
    final Object this$userSharer = this.userSharer;
    final Object other$userSharer = other.userSharer;
    if (this$userSharer == null ? other$userSharer != null
        : !this$userSharer.equals(other$userSharer)) {
      return false;
    }
    final Object this$store = this.store;
    final Object other$store = other.store;
    if (this$store == null ? other$store != null : !this$store.equals(other$store)) return false;
    final Object this$stats = this.stats;
    final Object other$stats = other.stats;
    if (this$stats == null ? other$stats != null : !this$stats.equals(other$stats)) return false;
    final Object this$date = this.date;
    final Object other$date = other.date;
    if (this$date == null ? other$date != null : !this$date.equals(other$date)) return false;
    final Object this$apps = this.apps;
    final Object other$apps = other.apps;
    if (this$apps == null ? other$apps != null : !this$apps.equals(other$apps)) return false;
    final Object this$ab = this.ab;
    final Object other$ab = other.ab;
    if (this$ab == null ? other$ab != null : !this$ab.equals(other$ab)) return false;
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof SocialVideo;
  }

  public Publisher getPublisher() {
    return this.publisher;
  }

  public String getCardId() {
    return this.cardId;
  }

  public String getTitle() {
    return this.title;
  }

  public String getThumbnailUrl() {
    return this.thumbnailUrl;
  }

  public String getUrl() {
    return this.url;
  }

  public String getContent() {
    return this.content;
  }

  public Comment.User getUser() {
    return this.user;
  }

  public Comment.User getUserSharer() {
    return this.userSharer;
  }

  public Store getStore() {
    return this.store;
  }

  public SocialCardStats getStats() {
    return this.stats;
  }

  public Date getDate() {
    return this.date;
  }

  public List<App> getApps() {
    return this.apps;
  }

  public Ab getAb() {
    return this.ab;
  }
}
