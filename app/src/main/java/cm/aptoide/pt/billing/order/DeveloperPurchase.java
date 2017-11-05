package cm.aptoide.pt.billing.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL) @JsonPropertyOrder({
    "orderId", "packageName", "productId", "purchaseTime", "purchaseToken", "purchaseState"
}) public class DeveloperPurchase {

  @JsonProperty("orderId") public String orderId;
  @JsonProperty("packageName") public String packageName;
  @JsonProperty("productId") public String productId;
  @JsonProperty("purchaseTime") public Integer purchaseTime;
  @JsonProperty("purchaseToken") public String purchaseToken;
  @JsonProperty("purchaseState") public Integer purchaseState;
}
