/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.accountmanager.ws.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * Created by rmateus on 01-07-2014.
 */
public class OAuth {

  private String accessToken;
  @JsonProperty("refresh_token") private String refreshToken;
  @JsonProperty("error_description") private String errorDescription;
  private List<ErrorResponse> errors;
  private String status;
  private String error;

  public boolean hasErrors() {
    return errors != null && errors.size() > 0;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getErrorDescription() {
    return errorDescription;
  }

  public List<ErrorResponse> getErrors() {
    return errors;
  }

  public String getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }
}
