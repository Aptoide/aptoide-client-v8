/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by marcelobenites on 8/18/16.
 */
public class PaymentConfirmation extends RealmObject {

  public static final String PRODUCT_ID = "productId";

  @PrimaryKey private int productId;
  private String paymentConfirmationId;
  @Required private String status;

  public PaymentConfirmation() {
  }

  public PaymentConfirmation(String paymentConfirmationId, int productId, String status) {
    this.paymentConfirmationId = paymentConfirmationId;
    this.status = status;
    this.productId = productId;
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
