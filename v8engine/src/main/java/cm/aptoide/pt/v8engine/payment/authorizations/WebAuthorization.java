/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 23/12/2016.
 */

package cm.aptoide.pt.v8engine.payment.authorizations;

import cm.aptoide.pt.v8engine.payment.Authorization;

/**
 * Created by marcelobenites on 15/11/16.
 */
public class WebAuthorization extends Authorization {

  private final String url;
  private final String redirectUrl;

  public static Authorization syncingError(int paymentId) {
    return new WebAuthorization(paymentId, "", "", WebAuthorization.Status.SYNCING_ERROR);
  }

  public static Authorization syncing(int paymentId) {
    return new WebAuthorization(paymentId, "", "", WebAuthorization.Status.SYNCING);
  }

  public WebAuthorization(int paymentId, String url, String redirectUrl, Status status) {
    super(paymentId, status);
    this.url = url;
    this.redirectUrl = redirectUrl;
  }

  public String getUrl() {
    return url;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }
}
