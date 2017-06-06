/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 23/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.services;

import cm.aptoide.pt.v8engine.billing.Authorization;

public class WebAuthorization extends Authorization {

  private final String url;
  private final String redirectUrl;

  public WebAuthorization(int paymentId, String url, String redirectUrl, Status status,
      String payerId) {
    super(paymentId, payerId, status);
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
