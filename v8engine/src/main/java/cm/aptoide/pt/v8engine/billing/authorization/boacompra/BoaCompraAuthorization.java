/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 23/12/2016.
 */

package cm.aptoide.pt.v8engine.billing.authorization.boacompra;

import cm.aptoide.pt.v8engine.billing.authorization.Authorization;

public class BoaCompraAuthorization extends Authorization {

  private final String url;
  private final String redirectUrl;

  public BoaCompraAuthorization(int paymentId, String url, String redirectUrl, Status status,
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
