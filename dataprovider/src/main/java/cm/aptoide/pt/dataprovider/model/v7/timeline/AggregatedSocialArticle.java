package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 17/05/2017.
 */

public class AggregatedSocialArticle implements TimelineCard {

  private final List<MinimalCard> minimalCardList;
  private final List<UserSharerTimeline> sharers;
  private final Publisher publisher;
  private final String cardId;
  private final String title;
  private final String thumbnailUrl;
  private final String url;
  private final Date date;
  private final List<App> apps;
  private final Ab ab;
  private final Urls urls;

  public AggregatedSocialArticle(@JsonProperty("uid") String cardId,
      @JsonProperty("title") String title, @JsonProperty("thumbnail") String thumbnailUrl,
      @JsonProperty("publisher") Publisher publisher, @JsonProperty("store") Store store,
      @JsonProperty("url") String url,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab,
      @JsonProperty("cards_shared") List<MinimalCard> minimalCardList,
      @JsonProperty("sharers") List<UserSharerTimeline> sharers, @JsonProperty("urls") Urls urls) {
    this.minimalCardList = minimalCardList;
    this.sharers = sharers;
    this.publisher = publisher;
    this.cardId = cardId;
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.url = url;
    this.date = date;
    this.apps = apps;
    this.ab = ab;
    this.urls = urls;
  }

  public List<MinimalCard> getMinimalCardList() {
    return minimalCardList;
  }

  public List<UserSharerTimeline> getSharers() {
    return sharers;
  }

  public Publisher getPublisher() {
    return this.publisher;
  }

  public String getCardId() {
    return this.cardId;
  }

  public Urls getUrls() {
    return this.urls;
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
