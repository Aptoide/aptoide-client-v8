/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 20/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Created by marcelobenites on 20/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreApprovalListResponse {

  private final List<PreApprovalResponse> preApprovalResponses;

  @JsonCreator public PreApprovalListResponse(
      @JsonProperty("pre-approvals") List<PreApprovalResponse> preApprovalResponses) {
    this.preApprovalResponses = preApprovalResponses;
  }

  public List<PreApprovalResponse> getPreApprovalResponses() {
    return preApprovalResponses;
  }
}
