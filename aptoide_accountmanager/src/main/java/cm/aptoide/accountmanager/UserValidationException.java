/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

/**
 * Created by marcelobenites on 10/02/17.
 */

public class UserValidationException extends Exception {

  public static final int EMPTY_NAME = 1;

  private final int code;

  public UserValidationException(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
