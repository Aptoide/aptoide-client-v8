/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.v8engine.billing.transaction;

import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class RealmTransactionPersistence implements TransactionPersistence {

  private final Database realm;
  private final TransactionMapper transactionMapper;
  private final TransactionFactory transactionFactory;

  public RealmTransactionPersistence(Database realm, TransactionMapper transactionMapper,
      TransactionFactory transactionFactory) {
    this.realm = realm;
    this.transactionMapper = transactionMapper;
    this.transactionFactory = transactionFactory;
  }

  @Override public Single<Transaction> createTransaction(int productId, String metadata,
      Transaction.Status status, String payerId, int paymentMethodId) {
    final Transaction transaction =
        transactionFactory.create(productId, payerId, status, paymentMethodId, metadata, null, null,
            null);
    return saveTransaction(transaction).andThen(Single.just(transaction));
  }

  @Override public Observable<Transaction> getTransaction(int productId, String payerId) {
    return realm.getRealm()
        .map(realm -> realm.where(PaymentConfirmation.class)
            .equalTo(PaymentConfirmation.PRODUCT_ID, productId)
            .equalTo(PaymentConfirmation.PAYER_ID, payerId))
        .flatMap(query -> realm.findAsList(query))
        .flatMap(paymentConfirmations -> Observable.from(paymentConfirmations)
            .map(paymentConfirmation -> transactionMapper.map(paymentConfirmation))
            .defaultIfEmpty(
                transactionFactory.create(productId, payerId, Transaction.Status.NEW, -1, null,
                    null, null, null)));
  }

  @Override public Completable removeTransaction(int productId) {
    return Completable.fromAction(
        () -> realm.delete(PaymentConfirmation.class, PaymentConfirmation.PRODUCT_ID, productId));
  }

  @Override public Completable removeAllTransactions() {
    return Completable.fromAction(() -> realm.deleteAll(PaymentConfirmation.class));
  }

  @Override public Completable saveTransaction(Transaction transaction) {
    return Completable.fromAction(() -> realm.insert(transactionMapper.map(transaction)));
  }
}