/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by marcelobenites on 8/18/16.
 */
public class PaymentConfirmation extends RealmObject {

  public static final String PRODUCT_ID = "productId";
  public static final String PAYER_ID = "payerId";

  @PrimaryKey private int productId;
  @Required private String payerId;
  @Required private String status;

  private String paymentConfirmationId;

  public PaymentConfirmation() {
  }

  public PaymentConfirmation(String paymentConfirmationId, int productId, String status,
      String payerId) {
    this.paymentConfirmationId = paymentConfirmationId;
    this.status = status;
    this.productId = productId;
    this.payerId = payerId;
  }

  public String getPayerId() {
    return payerId;
  }

  public String getPaymentConfirmationId() {
    return paymentConfirmationId;
  }

  public int getProductId() {
    return productId;
  }

  public String getStatus() {
    return status;
  }
}
