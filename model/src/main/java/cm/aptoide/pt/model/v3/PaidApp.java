/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/08/2016.
 */

package cm.aptoide.pt.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by marcelobenites on 7/27/16.
 */
@Data @EqualsAndHashCode(callSuper = true) public class PaidApp extends BaseV3Response {

  @JsonProperty("apk") public Path path;
  @JsonProperty("payment") private Payment payment;

  public boolean isPaid() {
    return (payment != null
        && payment.getAmount() != null
        && payment.getAmount()
        .floatValue() > 0.0f);
  }

  @Data public static class Payment {

    @JsonProperty("amount") private Double amount;

    @JsonProperty("currency_symbol") private String symbol;

    @JsonProperty("metadata") private Metadata metadata;

    @JsonProperty("payment_services") private List<PaymentServiceResponse> paymentServices;

    @JsonProperty("status") private String status;

    public boolean isPaid() {
      return status.equalsIgnoreCase("OK");
    }
  }

  @Data public static class Metadata {

    @JsonProperty("id") private int id;
  }

  @Data public static class Path {

    @JsonProperty("path") private String stringPath;
  }
}
