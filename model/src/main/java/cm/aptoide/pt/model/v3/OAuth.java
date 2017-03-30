/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by rmateus on 01-07-2014.
 */
public class OAuth {

  private String accessToken;
  @JsonProperty("refresh_token") private String refreshToken;
  @JsonProperty("error_description") private String errorDescription;
  private String error;

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getErrorDescription() {
    return errorDescription;
  }

  public String getError() {
    return error;
  }

  public boolean hasErrors() {
    return error != null;
  }
}
