/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by marcelobenites on 7/1/16.
 */
public class AppUpdate extends App implements TimelineCard {

  private final String cardId;
  private final Ab ab;
  private final Urls urls;

  @JsonCreator public AppUpdate(@JsonProperty("uid") String cardId, @JsonProperty("ab") Ab ab,
      @JsonProperty("urls") Urls urls) {
    this.cardId = cardId;
    this.ab = ab;
    this.urls = urls;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $cardId = this.cardId;
    result = result * PRIME + ($cardId == null ? 43 : $cardId.hashCode());
    final Object $ab = this.ab;
    result = result * PRIME + ($ab == null ? 43 : $ab.hashCode());
    final Object $urls = this.getUrls();
    result = result * PRIME + ($urls == null ? 43 : $urls.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof AppUpdate;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof AppUpdate)) return false;
    final AppUpdate other = (AppUpdate) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$cardId = this.cardId;
    final Object other$cardId = other.cardId;
    if (this$cardId == null ? other$cardId != null : !this$cardId.equals(other$cardId)) {
      return false;
    }
    final Object this$ab = this.ab;
    final Object other$ab = other.ab;
    if (this$ab == null ? other$ab != null : !this$ab.equals(other$ab)) return false;
    final Object this$urls = this.getUrls();
    final Object other$urls = other.getUrls();
    if (this$urls == null ? other$urls != null : !this$urls.equals(other$urls)) return false;
    return true;
  }

  public String getCardId() {
    return this.cardId;
  }

  @Override public Urls getUrls() {
    return urls;
  }

  public Ab getAb() {
    return this.ab;
  }
}
