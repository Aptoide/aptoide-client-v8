/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

import cm.aptoide.pt.model.v3.ErrorResponse;
import java.util.List;

/**
 * Created by marcelobenites on 10/02/17.
 */

public class AccountException extends Exception {

  private final String error;

  private String code;

  public AccountException(List<ErrorResponse> errors) {
    code = errors != null && !errors.isEmpty() ? errors.get(0).code : null;
    error = errors != null && !errors.isEmpty() ? errors.get(0).msg : null;
  }

  public AccountException(String error) {
    this.error = error;
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
}
