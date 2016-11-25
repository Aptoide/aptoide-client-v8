/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/11/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers;

import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import rx.Observable;

/**
 * Created by marcelobenites on 25/11/16.
 */

public abstract class AbstractPayment implements Payment {

  private final int id;
  private final String type;
  private final Product product;
  private final Price price;
  private final String description;

  public AbstractPayment(int id, String type, Product product, Price price, String description) {
    this.id = id;
    this.type = type;
    this.product = product;
    this.price = price;
    this.description = description;
  }

  @Override public int getId() {
    return id;
  }

  @Override public String getType() {
    return type;
  }

  @Override public Product getProduct() {
    return product;
  }

  @Override public Price getPrice() {
    return price;
  }

  @Override public String getDescription() {
    return description;
  }
}