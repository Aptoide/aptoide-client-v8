package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 31/08/16.
 */
public class Conversion {
  private final String url;

  @JsonCreator public Conversion(@JsonProperty("url") String url) {
    this.url = url;
  }

  public String getUrl() {
    return this.url;
  }
}
