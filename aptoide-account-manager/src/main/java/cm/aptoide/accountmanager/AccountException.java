/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.OAuth;
import cm.aptoide.pt.dataprovider.ws.v3.GenericResponseV3;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by marcelobenites on 10/02/17.
 */

public class AccountException extends Exception {

  private final String error;
  private final Map<String, String> errors;

  private String code;

  public AccountException(List<ErrorResponse> errors) {
    this.errors = new HashMap<>();
    if (errors != null && !errors.isEmpty()) {
      code = errors.get(0).code;
    } else {
      code = null;
    }
    if (errors != null && !errors.isEmpty()) {
      error = errors.get(0).msg;
    } else {
      error = null;
    }
    this.errors.put(String.valueOf(error), String.valueOf(code));
  }

  public AccountException(AptoideWsV3Exception exception) {
    error = exception.getBaseResponse()
        .getError();
    errors = createErrorsList(exception.getBaseResponse());
  }

  public AccountException(OAuth oAuth) {
    error = oAuth.getError();
    errors = createErrorsList(oAuth);
  }

  private Map<String, String> createErrorsList(OAuth oauth) {
    if (oauth.getErrors() != null && !oauth.getErrors()
        .isEmpty()) {
      return getErrorsList(oauth.getErrors());
    } else {
      return createErrorList(oauth.getError(), oauth.getErrorDescription());
    }
  }

  private Map<String, String> createErrorsList(GenericResponseV3 response) {
    if (response.getErrors() != null && !response.getErrors()
        .isEmpty()) {
      return getErrorsList(response.getErrors());
    } else {
      return createErrorList(response.getError(), response.getErrorDescription());
    }
  }

  private Map<String, String> createErrorList(String code, String description) {
    Map<String, String> error = new HashMap<>();
    error.put(code, description);
    return error;
  }

  public String getCode() {
    return code;
  }

  public String getError() {
    return error;
  }

  public boolean hasCode() {
    return code != null;
  }

  public Map<String, String> getErrors() {
    return errors;
  }

  @NonNull private Map<String, String> getErrorsList(List<ErrorResponse> errorResponses) {
    Map<String, String> errors = new HashMap<>();
    for (ErrorResponse error : errorResponses) {
      errors.put(String.valueOf(error.code), String.valueOf(error.msg));
    }
    return errors;
  }
}
