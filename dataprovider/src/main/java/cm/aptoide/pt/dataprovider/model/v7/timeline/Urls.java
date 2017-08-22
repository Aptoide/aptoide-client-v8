package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by trinkes on 22/08/2017.
 */

public class Urls {
  private final String read;

  public Urls(@JsonProperty("read") String read) {
    this.read = read;
  }

  public String getRead() {
    return read;
  }
}
