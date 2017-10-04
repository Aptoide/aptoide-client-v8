/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 23/12/2016.
 */

package cm.aptoide.pt.billing.authorization.boacompra;

import cm.aptoide.pt.billing.authorization.Authorization;

public class BoaCompraAuthorization extends Authorization {

  private final String url;
  private final String redirectUrl;

  public BoaCompraAuthorization(int paymentId, String url, String redirectUrl, Status status,
      String customerId) {
    super(paymentId, customerId, status);
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
