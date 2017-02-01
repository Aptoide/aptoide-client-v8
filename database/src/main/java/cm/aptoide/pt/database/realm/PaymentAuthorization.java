/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 15/11/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by marcelobenites on 15/11/16.
 */

public class PaymentAuthorization extends RealmObject {

  public static final String PAYMENT_ID = "paymentId";
  public static final String PAYER_ID = "payerId";

  @PrimaryKey private int paymentId;
  @Required private String status;
  @Required private String payerId;

  private String url;
  private String redirectUrl;

  public PaymentAuthorization() {
  }

  public PaymentAuthorization(int paymentId, String url, String redirectUrl, String status,
      String payerId) {
    this.paymentId = paymentId;
    this.url = url;
    this.redirectUrl = redirectUrl;
    this.status = status;
    this.payerId = payerId;
  }

  public String getPayerId() {
    return payerId;
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

  public String getStatus() {
    return status;
  }
}
