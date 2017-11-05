package cm.aptoide.pt.billing.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL) @JsonPropertyOrder({
    "list"
}) public class OrdersList {

  @JsonProperty("list") public java.util.List<Order> orderList;
}
