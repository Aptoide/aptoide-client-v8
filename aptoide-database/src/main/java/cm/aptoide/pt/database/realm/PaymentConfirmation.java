/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class PaymentConfirmation extends RealmObject {

  public static final String PRODUCT_ID = "productId";
  public static final String PAYER_ID = "payerId";
  public static final String SELLER_ID = "sellerId";

  @PrimaryKey private String id;
  @Required private String productId;
  @Required private String sellerId;
  @Required private String payerId;
  @Required private String status;

  private int paymentMethodId;

  private String paymentConfirmationId;
  private String confirmationUrl;
  private String successUrl;
  private String clientToken;
  private String payload;

  public PaymentConfirmation() {
  }

  public PaymentConfirmation(String id, String localMetadata, String productId, String sellerId,
      String status, String payerId, int paymentMethodId, String confirmationUrl, String successUrl,
      String clientToken, String payload) {
    this.id = id;
    this.paymentConfirmationId = localMetadata;
    this.sellerId = sellerId;
    this.status = status;
    this.productId = productId;
    this.payerId = payerId;
    this.paymentMethodId = paymentMethodId;
    this.confirmationUrl = confirmationUrl;
    this.successUrl = successUrl;
    this.clientToken = clientToken;
    this.payload = payload;
  }

  public String getId() {
    return id;
  }

  public String getSellerId() {
    return sellerId;
  }

  public String getPayerId() {
    return payerId;
  }

  public String getLocalMetadata() {
    return paymentConfirmationId;
  }

  public String getProductId() {
    return productId;
  }

  public String getStatus() {
    return status;
  }

  public int getPaymentMethodId() {
    return paymentMethodId;
  }

  public String getConfirmationUrl() {
    return confirmationUrl;
  }

  public String getSuccessUrl() {
    return successUrl;
  }

  public String getClientToken() {
    return clientToken;
  }

  public String getPayload() {
    return payload;
  }
}