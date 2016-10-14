/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 14/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;

/**
 * Created by marcelobenites on 14/10/16.
 */

public class BoaCompraPayment implements Payment {

  private final BoaCompraApi api;

  public BoaCompraPayment(String apiUrl, BoaCompraAuthorization authorization) {
    this.api = BoaCompraApiFactory.create(apiUrl, authorization);
  }

  @Override public int getId() {
    return 0;
  }

  @Override public String getType() {
    return null;
  }

  @Override public Product getProduct() {
    return null;
  }

  @Override public Price getPrice() {
    return null;
  }

  @Override public String getDescription() {
    return null;
  }

  @Override public void removeListener() {

  }

  @Override public boolean isProcessing() {
    return false;
  }

  @Override public void process(PaymentConfirmationListener listener) {

  }
}
