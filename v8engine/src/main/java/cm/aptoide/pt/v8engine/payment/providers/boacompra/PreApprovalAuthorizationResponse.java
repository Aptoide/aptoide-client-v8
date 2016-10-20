/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 20/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by marcelobenites on 20/10/16.
 */

public class PreApprovalAuthorizationResponse {

  private final PreApprovalAuthorization preApprovalAuthorization;

  @JsonCreator public PreApprovalAuthorizationResponse(@JsonProperty("pre-approval")
      PreApprovalAuthorization preApprovalAuthorization) {
    this.preApprovalAuthorization = preApprovalAuthorization;
  }

  public PreApprovalAuthorization getPreApprovalAuthorization() {
    return preApprovalAuthorization;
  }
}
