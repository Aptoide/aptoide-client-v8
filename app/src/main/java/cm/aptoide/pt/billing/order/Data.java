package cm.aptoide.pt.billing.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL) @JsonPropertyOrder({
    "developer_purchase"
}) public class Data {

  @JsonProperty("developer_purchase") public DeveloperPurchase developerPurchase;
}
