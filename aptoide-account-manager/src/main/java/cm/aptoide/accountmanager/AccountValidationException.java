/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

/**
 * Created by marcelobenites on 10/02/17.
 */

public class AccountValidationException extends Exception {

  public static final int EMPTY_EMAIL = 1;
  public static final int EMPTY_PASSWORD = 2;
  public static final int EMPTY_EMAIL_AND_PASSWORD = 3;
  public static final int INVALID_PASSWORD = 4;
  public static final int EMPTY_NAME = 5;
  public static final int EMPTY_NAME_AND_AVATAR = 6;

  private final int code;

  public AccountValidationException(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
