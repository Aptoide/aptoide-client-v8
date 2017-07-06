/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/11/2016.
 */

package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true) public class TransactionResponse
    extends BaseV3Response {

  @JsonProperty("payStatus") private String transactionStatus;
  @JsonProperty("paykey") private String localMetadata;
  @JsonProperty("paymentId") private int paymentMethodId;
  @JsonProperty("confirmationUrl") private String confirmationUrl;
  @JsonProperty("successUrl") private String successUrl;
}