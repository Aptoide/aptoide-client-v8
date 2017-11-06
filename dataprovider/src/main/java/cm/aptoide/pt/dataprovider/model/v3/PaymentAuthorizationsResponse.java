/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 15/11/2016.
 */

package cm.aptoide.pt.dataprovider.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true) public class PaymentAuthorizationsResponse
    extends BaseV3Response {

  @JsonProperty("authorizations") private List<PaymentAuthorizationResponse> authorizations;
}