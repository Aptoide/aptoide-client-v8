package cm.aptoide.pt.dataprovider.model.v7.timeline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jdandrade on 31/08/16.
 */
public class Ab {
  private final Conversion conversion;

  @JsonCreator public Ab(@JsonProperty("conversion") Conversion conversion) {
    this.conversion = conversion;
  }

  public Conversion getConversion() {
    return this.conversion;
  }
}
