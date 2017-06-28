/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.services;

import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentLocalProcessingRequiredException;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepositoryFactory;
import rx.Completable;

public class PayPalPaymentMethod extends AptoidePaymentMethod {

  private final TransactionRepositoryFactory transactionRepositoryFactory;

  public PayPalPaymentMethod(int id, String name, String description,
      TransactionRepositoryFactory transactionRepositoryFactory) {
    super(id, name, description);
    this.transactionRepositoryFactory = transactionRepositoryFactory;
  }

  @Override public Completable process(Product product) {
    return transactionRepositoryFactory.getTransactionRepository(product)
        .getTransaction(product)
        .takeUntil(transaction -> transaction.isCompleted())
        .flatMapCompletable(transaction -> {

          if (transaction.isCompleted() || transaction.isPending()) {
            return Completable.complete();
          }

          return Completable.error(new PaymentLocalProcessingRequiredException(
              "PayPal SDK local processing of the payment required."));
        })
        .toCompletable();
  }

  public Completable process(Product product, String payPalConfirmationId) {
    return transactionRepositoryFactory.getTransactionRepository(product)
        .createTransaction(product, getId(), payPalConfirmationId);
  }
}