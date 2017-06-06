/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 02/01/2017.
 */

package cm.aptoide.pt.v8engine.billing.services;

import cm.aptoide.pt.v8engine.billing.Payment;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.repository.PaymentRepositoryFactory;
import rx.Completable;

public class AptoidePayment implements Payment {

  private final PaymentRepositoryFactory paymentRepositoryFactory;
  private final int id;
  private final String name;
  private final String description;

  public AptoidePayment(int id, String name, String description,
      PaymentRepositoryFactory paymentRepositoryFactory) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.paymentRepositoryFactory = paymentRepositoryFactory;
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

  @Override public Completable process(Product product) {
    return paymentRepositoryFactory.getPaymentConfirmationRepository(product)
        .createPaymentConfirmation(id, product);
  }
}