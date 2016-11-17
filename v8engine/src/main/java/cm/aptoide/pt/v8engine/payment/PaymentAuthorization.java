/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 15/11/2016.
 */

package cm.aptoide.pt.v8engine.payment;

/**
 * Created by marcelobenites on 15/11/16.
 */
public class PaymentAuthorization {

  private final int paymentId;
  private final String url;
  private final String redirectUrl;

  private boolean authorized;

  public PaymentAuthorization(int paymentId, String url, String redirectUrl, boolean authorized) {
    this.paymentId = paymentId;
    this.url = url;
    this.redirectUrl = redirectUrl;
    this.authorized = authorized;
  }

  public int getPaymentId() {
    return paymentId;
  }

  public String getUrl() {
    return url;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  public boolean isAuthorized() {
    return authorized;
  }

  public void setAuthorized(boolean authorized) {
    this.authorized = authorized;
  }
}
