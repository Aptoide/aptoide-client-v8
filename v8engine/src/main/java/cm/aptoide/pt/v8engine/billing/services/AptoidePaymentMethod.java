/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 02/01/2017.
 */

package cm.aptoide.pt.v8engine.billing.services;

import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepositoryFactory;
import rx.Completable;

public abstract class AptoidePaymentMethod implements PaymentMethod {

  private final int id;
  private final String name;
  private final String description;

  public AptoidePaymentMethod(int id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  @Override public int getId() {
    return id;
  }

  @Override public String getName() {
    return name;
  }

  @Override public String getDescription() {
    return description;
  }

}