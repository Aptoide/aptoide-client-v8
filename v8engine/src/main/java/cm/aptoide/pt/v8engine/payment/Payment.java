/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import rx.Completable;
import rx.Observable;
import rx.Single;

public interface Payment {

  int getId();

  String getName();

  String getDescription();

  Completable process(Product product);
}