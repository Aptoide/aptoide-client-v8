/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import java.util.List;

/**
 * Created by j-pac on 30-05-2014.
 */
public class GenericResponseV3 {

  private String status;
  private List<ErrorResponse> errors;

  // Oauth api error
  private String error;
  private String errorDescription;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<ErrorResponse> getErrors() {
    return errors;
  }

  public void setErrors(List<ErrorResponse> errors) {
    this.errors = errors;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getErrorDescription() {
    return errorDescription;
  }

  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }
}
