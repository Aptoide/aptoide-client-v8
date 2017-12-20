package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by marcelobenites on 6/23/16.
 */
public class Publisher {

  private final String name;
  private final String logoUrl;
  private final String baseUrl;

  @JsonCreator
  public Publisher(@JsonProperty("name") String name, @JsonProperty("logo") String logoUrl,
      @JsonProperty("url") String baseUrl) {
    this.name = name;
    this.logoUrl = logoUrl;
    this.baseUrl = baseUrl;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $name = this.name;
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    final Object $logoUrl = this.logoUrl;
    result = result * PRIME + ($logoUrl == null ? 43 : $logoUrl.hashCode());
    final Object $baseUrl = this.baseUrl;
    result = result * PRIME + ($baseUrl == null ? 43 : $baseUrl.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Publisher)) return false;
    final Publisher other = (Publisher) o;
    if (!other.canEqual(this)) return false;
    final Object this$name = this.name;
    final Object other$name = other.name;
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    final Object this$logoUrl = this.logoUrl;
    final Object other$logoUrl = other.logoUrl;
    if (this$logoUrl == null ? other$logoUrl != null : !this$logoUrl.equals(other$logoUrl)) {
      return false;
    }
    final Object this$baseUrl = this.baseUrl;
    final Object other$baseUrl = other.baseUrl;
    return this$baseUrl == null ? other$baseUrl == null : this$baseUrl.equals(other$baseUrl);
  }

  protected boolean canEqual(Object other) {
    return other instanceof Publisher;
  }

  public String getName() {
    return this.name;
  }

  public String getLogoUrl() {
    return this.logoUrl;
  }

  public String getBaseUrl() {
    return this.baseUrl;
  }
}
