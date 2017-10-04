/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.billing.sync;

import cm.aptoide.pt.billing.Customer;
import cm.aptoide.pt.billing.Product;
import cm.aptoide.pt.billing.transaction.LocalTransaction;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionPersistence;
import cm.aptoide.pt.billing.transaction.TransactionService;
import cm.aptoide.pt.sync.Sync;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class TransactionSync extends Sync {

  private final Product product;
  private final TransactionPersistence transactionPersistence;
  private final Customer customer;
  private final TransactionService transactionService;
  private final String merchantName;

  public TransactionSync(Product product, TransactionPersistence transactionPersistence,
      Customer customer, TransactionService transactionService, boolean periodic, boolean exact,
      long interval, long trigger, String merchantName) {
    super(String.valueOf(product.getId()), periodic, exact, trigger, interval);
    this.product = product;
    this.transactionPersistence = transactionPersistence;
    this.customer = customer;
    this.transactionService = transactionService;
    this.merchantName = merchantName;
  }

  @Override public Completable execute() {
    return customer.getId()
        .flatMap(customerId -> syncLocalTransaction(customerId, merchantName).onErrorResumeNext(throwable -> {
          if (throwable instanceof NoSuchElementException) {
            return syncTransaction(customerId, merchantName);
          }
          return Single.error(throwable);
        }))
        .toCompletable();
  }

  private Single<Transaction> syncLocalTransaction(String customerId, String merchantName) {
    return transactionPersistence.getTransaction(merchantName, customerId, product.getId())
        .timeout(1, TimeUnit.SECONDS, Observable.empty())
        .first()
        .filter(transaction -> transaction instanceof LocalTransaction)
        .filter(transaction -> transaction.isPending())
        .cast(LocalTransaction.class)
        .toSingle()
        .flatMap(
            localTransaction -> transactionService.createTransaction(localTransaction.getSellerId(),
                localTransaction.getCustomerId(), localTransaction.getPaymentMethodId(), product,
                localTransaction.getLocalMetadata(), localTransaction.getPayload()))
        .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
            .andThen(Single.just(transaction)));
  }

  private Single<Transaction> syncTransaction(String customerId, String merchantName) {
    return transactionService.getTransaction(merchantName, customerId, product)
        .flatMap(transaction -> transactionPersistence.saveTransaction(transaction)
            .andThen(Single.just(transaction)));
  }
}
