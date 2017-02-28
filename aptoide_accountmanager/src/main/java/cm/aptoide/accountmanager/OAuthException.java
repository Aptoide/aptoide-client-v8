/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.accountmanager;

import cm.aptoide.accountmanager.ws.responses.OAuth;

/**
 * Created by marcelobenites on 10/02/17.
 */

public class OAuthException extends Exception {

  private final OAuth oAuth;

  public OAuthException(OAuth oAuth) {
    super(oAuth.getErrorDescription());
    this.oAuth = oAuth;
  }

  public OAuth getoAuth() {
    return oAuth;
  }
}
