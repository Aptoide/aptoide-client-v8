/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

/**
 * Created by marcelobenites on 7/8/16.
 */
public class Recommendation implements TimelineCard {

  private final String cardId;
  private final App recommendedApp;
  private final List<App> similarApps;
  private final Urls urls;
  private final Ab ab;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") private Date timestamp;

  @JsonCreator public Recommendation(@JsonProperty("uid") String cardId,
      @JsonProperty("timestamp") Date timestamp, @JsonProperty("app") App recommendedApp,
      @JsonProperty("apps") List<App> similarApps, @JsonProperty("ab") Ab ab,
      @JsonProperty("urls") Urls urls) {
    this.ab = ab;
    this.cardId = cardId;
    this.timestamp = timestamp;
    this.recommendedApp = recommendedApp;
    this.similarApps = similarApps;
    this.urls = urls;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $cardId = this.cardId;
    result = result * PRIME + ($cardId == null ? 43 : $cardId.hashCode());
    final Object $urls = this.getUrls();
    result = result * PRIME + ($urls == null ? 43 : $urls.hashCode());
    final Object $ab = this.ab;
    result = result * PRIME + ($ab == null ? 43 : $ab.hashCode());
    final Object $timestamp = this.timestamp;
    result = result * PRIME + ($timestamp == null ? 43 : $timestamp.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Recommendation)) return false;
    final Recommendation other = (Recommendation) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$cardId = this.cardId;
    final Object other$cardId = other.cardId;
    if (this$cardId == null ? other$cardId != null : !this$cardId.equals(other$cardId)) {
      return false;
    }
    final Object this$urls = this.getUrls();
    final Object other$urls = other.getUrls();
    if (this$urls == null ? other$urls != null : !this$urls.equals(other$urls)) return false;
    final Object this$ab = this.ab;
    final Object other$ab = other.ab;
    if (this$ab == null ? other$ab != null : !this$ab.equals(other$ab)) return false;
    final Object this$timestamp = this.timestamp;
    final Object other$timestamp = other.timestamp;
    if (this$timestamp == null ? other$timestamp != null
        : !this$timestamp.equals(other$timestamp)) {
      return false;
    }
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof Recommendation;
  }

  public String getCardId() {
    return this.cardId;
  }

  @Override public Urls getUrls() {
    return urls;
  }

  public App getRecommendedApp() {
    return this.recommendedApp;
  }

  public List<App> getSimilarApps() {
    return this.similarApps;
  }

  public Ab getAb() {
    return this.ab;
  }

  public Date getTimestamp() {
    return this.timestamp;
  }
}
