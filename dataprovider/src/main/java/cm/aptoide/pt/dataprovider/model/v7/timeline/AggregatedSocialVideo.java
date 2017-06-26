package cm.aptoide.pt.model.v7.timeline;

import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 19/05/2017.
 */

public class AggregatedSocialVideo implements TimelineCard {
  private final List<MinimalCard> minimalCards;
  private final List<UserSharerTimeline> sharers;
  private final String title;
  private final String thumbnailUrl;
  private final Publisher publisher;
  private final String url;
  private final Store store;
  private final Date date;
  private final List<App> apps;
  private final Ab ab;
  private final String cardId;

  @JsonCreator public AggregatedSocialVideo(@JsonProperty("uid") String cardId,
      @JsonProperty("title") String title, @JsonProperty("thumbnail") String thumbnailUrl,
      @JsonProperty("publisher") Publisher publisher, @JsonProperty("url") String url,
      @JsonProperty("store") Store store,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab,
      @JsonProperty("cards_shared") List<MinimalCard> minimalCardList,
      @JsonProperty("sharers") List<UserSharerTimeline> sharers) {
    this.cardId = cardId;
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.publisher = publisher;
    this.url = url;
    this.store = store;
    this.date = date;
    this.apps = apps;
    this.ab = ab;
    this.minimalCards = minimalCardList;
    this.sharers = sharers;
  }

  public List<MinimalCard> getMinimalCards() {
    return minimalCards;
  }

  public List<UserSharerTimeline> getSharers() {
    return sharers;
  }

  public String getTitle() {
    return title;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public Publisher getPublisher() {
    return publisher;
  }

  public String getUrl() {
    return url;
  }

  public Store getStore() {
    return store;
  }

  public Date getDate() {
    return date;
  }

  public List<App> getApps() {
    return apps;
  }

  public Ab getAb() {
    return ab;
  }

  @Override public String getCardId() {
    return this.cardId;
  }
}
