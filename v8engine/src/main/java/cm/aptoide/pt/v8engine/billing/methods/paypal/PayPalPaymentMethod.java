/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.methods.paypal;

import cm.aptoide.pt.v8engine.billing.PaymentMethod;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.exception.PaymentLocalProcessingRequiredException;
import cm.aptoide.pt.v8engine.billing.repository.TransactionRepository;
import rx.Completable;

public class PayPalPaymentMethod implements PaymentMethod {

  private final int id;
  private final String name;
  private final String description;
  private final TransactionRepository transactionRepository;

  public PayPalPaymentMethod(int id, String name, String description,
      TransactionRepository transactionRepository) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.transactionRepository = transactionRepository;
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
    return transactionRepository.getTransaction(product)
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
    return transactionRepository.createTransaction(product, getId(), payPalConfirmationId)
        .toCompletable();
  }
}