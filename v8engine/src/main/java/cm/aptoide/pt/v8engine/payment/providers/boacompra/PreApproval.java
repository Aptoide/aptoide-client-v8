/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * Created by marcelobenites on 10/7/16.
 */

class PreApproval {

  @JsonProperty("pre-approval-code") private final String code;
  @JsonProperty("date") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") private final Date date;
  @JsonProperty("redirect-url") private final String redirectUrl;

  @JsonCreator public PreApproval(String code, Date date, String redirectUrl) {
    this.code = code;
    this.date = date;
    this.redirectUrl = redirectUrl;
  }

  public String getCode() {
    return code;
  }

  public Date getDate() {
    return date;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }
}
