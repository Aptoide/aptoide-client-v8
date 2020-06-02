package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by franciscocalado on 06/09/2018.
 */

public class TermsAndConditionsResponse extends BaseV3Response {

  @JsonProperty("tos") private boolean tos;
  @JsonProperty("privacy") private boolean privacy;

  public TermsAndConditionsResponse() {
  }

  public boolean isTos() {
    return tos;
  }

  public void setTos(boolean tos) {
    this.tos = tos;
  }

  public boolean isPrivacy() {
    return privacy;
  }

  public void setPrivacy(boolean privacy) {
    this.privacy = privacy;
  }
}
