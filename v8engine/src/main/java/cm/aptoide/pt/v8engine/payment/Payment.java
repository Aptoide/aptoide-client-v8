/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import rx.Completable;

/**
 * Created by marcelobenites on 8/10/16.
 */
public interface Payment {

  int getId();

  String getName();

  String getType();

  Product getProduct();

  Price getPrice();

  String getDescription();

  Status getStatus();

  Authorization getAuthorization();

  PaymentConfirmation getConfirmation();

  void setAuthorization(Authorization authorization);

  void setConfirmation(PaymentConfirmation confirmation);

  boolean isAuthorizationRequired();

  Completable process();

  enum Status {
    NEW,
    COMPLETED,
    PENDING
  }
}