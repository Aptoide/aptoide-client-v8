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

public class PreApprovalResponse {

  private final PreApproval preApproval;

  @JsonCreator public PreApprovalResponse(@JsonProperty("pre-approval") PreApproval preApproval) {
    this.preApproval = preApproval;
  }

  public PreApproval getPreApproval() {
    return preApproval;
  }
}
