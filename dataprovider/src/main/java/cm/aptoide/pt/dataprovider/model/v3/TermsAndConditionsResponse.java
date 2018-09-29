package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * Created by franciscocalado on 06/09/2018.
 */

public class TermsAndConditionsResponse extends BaseV3Response {

  @JsonFormat(pattern = "yyyy-MM-dd") private Date birthdate;
  @JsonProperty("tos") private boolean tos;
  @JsonProperty("privacy") private boolean privacy;

  public TermsAndConditionsResponse() {
  }

  public Date getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(Date birthDate) {
    this.birthdate = birthDate;
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
