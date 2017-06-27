/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/11/2016.
 */

package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by marcelobenites on 16/11/16.
 */
@Data @EqualsAndHashCode(callSuper = true) public class TransactionResponse
    extends BaseV3Response {

  @JsonProperty("payStatus") private String transactionStatus;
  @JsonProperty("paykey") private String localMetadata;
  @JsonProperty("paymentId") private int paymentMethodId;
}