/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by marcelobenites on 10/7/16.
 */
class PreApprovalAuthorizationRequest {

  @JsonProperty("pre-approval") private final PreApprovalRequest PreApprovalRequest;

  public PreApprovalAuthorizationRequest(PreApprovalRequest PreApprovalRequest) {
    this.PreApprovalRequest = PreApprovalRequest;
  }

  public PreApprovalRequest getPreApprovalRequest() {
    return PreApprovalRequest;
  }
}
