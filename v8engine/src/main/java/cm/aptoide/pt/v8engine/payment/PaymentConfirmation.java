/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.model.v3.ProductPaymentResponse;

/**
 * Created by marcelobenites on 8/10/16.
 */
public class PaymentConfirmation {

  private final String paymentConfirmationId;
  private final int paymentId;
  private final Product product;
  private final Price price;

  private ProductPaymentResponse.Status status;

  public PaymentConfirmation(String paymentConfirmationId, int paymentId, Product product,
      Price price, ProductPaymentResponse.Status status) {
    this.paymentConfirmationId = paymentConfirmationId;
    this.paymentId = paymentId;
    this.product = product;
    this.price = price;
    this.status = status;
  }

  public Product getProduct() {
    return product;
  }

  public String getPaymentConfirmationId() {
    return paymentConfirmationId;
  }

  public int getPaymentId() {
    return paymentId;
  }

  public Price getPrice() {
    return price;
  }

  public ProductPaymentResponse.Status getStatus() {
    return status;
  }

  public void setStatus(ProductPaymentResponse.Status status) {
    this.status = status;
  }

  public boolean isCompleted() {
    return ProductPaymentResponse.Status.COMPLETED.equals(status);
  }

  public boolean isFailed() {
    return ProductPaymentResponse.Status.FAILED.equals(status)
        || ProductPaymentResponse.Status.CANCELED.equals(status);
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final PaymentConfirmation that = (PaymentConfirmation) o;

    if (!paymentConfirmationId.equals(that.paymentConfirmationId)) {
      return false;
    }

    return true;
  }

  @Override public int hashCode() {
    return paymentConfirmationId.hashCode();
  }
}