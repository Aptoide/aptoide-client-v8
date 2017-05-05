/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by marcelobenites on 8/10/16.
 */
public interface Payment {

  int getId();

  String getName();

  String getDescription();

  Observable<Authorization> getAuthorization();

  Observable<PaymentConfirmation> getConfirmation(Product product);

  Completable process(Product product);
}