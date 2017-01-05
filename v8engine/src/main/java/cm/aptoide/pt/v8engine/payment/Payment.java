/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.pt.v8engine.payment.exception.PaymentException;
import rx.Completable;
import rx.Observable;

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

  Authorization getAuthorization();

  void setAuthorization(Authorization authorization);

  boolean isAuthorizationRequired();

  Completable process();

  Observable<PaymentConfirmation> getConfirmation();

}