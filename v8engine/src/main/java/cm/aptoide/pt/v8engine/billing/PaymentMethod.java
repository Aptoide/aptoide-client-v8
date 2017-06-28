/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 10/08/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import rx.Completable;

public interface PaymentMethod {

  int getId();

  String getName();

  String getDescription();

  Completable process(Product product);
}