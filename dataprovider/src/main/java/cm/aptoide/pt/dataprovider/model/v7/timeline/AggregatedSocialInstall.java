package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialInstall implements TimelineCard {

  private final List<MinimalCard> minimalCardList;
  private final List<UserSharerTimeline> sharers;
  private final String cardId;
  private final App app;
  private final Ab ab;
  private final Date date;
  private final Urls urls;

  public AggregatedSocialInstall(@JsonProperty("uid") String cardId,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("cards_shared") List<MinimalCard> minimalCardList,
      @JsonProperty("sharers") List<UserSharerTimeline> sharers, @JsonProperty("urls") Urls urls) {

    this.minimalCardList = minimalCardList;
    this.sharers = sharers;
    this.cardId = cardId;
    this.ab = ab;
    this.date = date;
    this.urls = urls;
    if (!apps.isEmpty()) {
      this.app = apps.get(0);
    } else {
      this.app = null;
    }
  }

  public List<MinimalCard> getMinimalCardList() {
    return minimalCardList;
  }

  public List<UserSharerTimeline> getSharers() {
    return sharers;
  }

  public String getCardId() {
    return this.cardId;
  }

  @Override public Urls getUrls() {
    return urls;
  }

  public App getApp() {
    return this.app;
  }

  public Ab getAb() {
    return this.ab;
  }

  public Date getDate() {
    return this.date;
  }
}
