/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV3Exception;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.OAuth;
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
    errors = getErrorsList(exception.getBaseResponse()
        .getErrors());
    errors.put(String.valueOf(exception.getBaseResponse()
        .getError()), String.valueOf(exception.getBaseResponse()
        .getErrorDescription()));
  }

  public AccountException(OAuth oAuth) {
    error = oAuth.getError();
    errors = getErrorsList(oAuth.getErrors());
    errors.put(oAuth.getError(), oAuth.getErrorDescription());
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
