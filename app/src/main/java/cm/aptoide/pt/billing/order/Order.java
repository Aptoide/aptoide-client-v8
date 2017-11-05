package cm.aptoide.pt.billing.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL) @JsonPropertyOrder({
    "id", "signature", "product", "data"
}) public class Order {

  @JsonProperty("id") public Integer id;
  @JsonProperty("signature") public String signature;
  @JsonProperty("product") public Product product;
  @JsonProperty("data") public Data data;
}
