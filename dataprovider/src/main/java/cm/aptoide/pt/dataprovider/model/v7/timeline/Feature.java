package cm.aptoide.pt.dataprovider.model.v7.timeline;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

public class Feature implements TimelineCard {

  private final String cardId;
  private final String title;
  private final String thumbnailUrl;
  private final String url;
  private final Date date;
  private final List<App> apps;
  private final Ab ab;
  private final Urls urls;

  @JsonCreator
  public Feature(@JsonProperty("uid") String cardId, @JsonProperty("title") String title,
      @JsonProperty("thumbnail") String thumbnailUrl, @JsonProperty("url") String url,
      @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC") @JsonProperty("date") Date date,
      @JsonProperty("apps") List<App> apps, @JsonProperty("ab") Ab ab,
      @JsonProperty("urls") Urls urls) {
    this.cardId = cardId;
    this.title = title;
    this.thumbnailUrl = thumbnailUrl;
    this.url = url;
    this.date = date;
    this.apps = apps;
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

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Feature)) return false;
    final Feature other = (Feature) o;
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

  protected boolean canEqual(Object other) {
    return other instanceof Feature;
  }

  public String getCardId() {
    return this.cardId;
  }

  @Override public Urls getUrls() {
    return urls;
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
