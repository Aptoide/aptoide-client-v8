package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true) public class PaymentAuthorizationResponse
    extends BaseV3Response {

  @JsonProperty("paymentTypeId") private int paymentId;
  @JsonProperty("url") private String url;
  @JsonProperty("successUrl") private String successUrl;
  @JsonProperty("authorizationStatus") private String authorizationStatus;
}
