/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/02/2017.
 */

package cm.aptoide.pt.v8engine.gms;

/**
 * Created by marcelobenites on 06/02/17.
 */
public class GooglePlayServicesException extends Throwable {

  private final int errorCode;
  private final boolean resolvable;

  public GooglePlayServicesException(int errorCode, boolean resolvable) {
    this.errorCode = errorCode;
    this.resolvable = resolvable;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public boolean isResolvable() {
    return resolvable;
  }
}
