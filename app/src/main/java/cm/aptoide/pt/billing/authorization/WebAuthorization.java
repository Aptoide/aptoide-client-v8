/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 23/12/2016.
 */

package cm.aptoide.pt.billing.authorization;

public class WebAuthorization extends Authorization {

  private final String url;
  private final String redirectUrl;

  public WebAuthorization(String id, String customerId, Status status, String url, String redirectUrl,
      String transactionId) {
    super(id, customerId, status, transactionId);
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
