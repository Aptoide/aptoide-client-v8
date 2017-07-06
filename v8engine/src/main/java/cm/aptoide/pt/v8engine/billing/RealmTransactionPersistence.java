/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.v8engine.billing;

import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class RealmTransactionPersistence implements TransactionPersistence {

  private final Database realm;
  private final TransactionFactory transactionMapper;

  public RealmTransactionPersistence(Database realm, TransactionFactory transactionMapper) {
    this.realm = realm;
    this.transactionMapper = transactionMapper;
  }

  @Override public Single<Transaction> createTransaction(int productId, String metadata,
      Transaction.Status status, String payerId, int paymentMethodId) {
    final Transaction transaction =
        transactionMapper.create(productId, metadata, status, payerId, paymentMethodId);
    return saveTransaction(transaction).andThen(Single.just(transaction));
  }

  @Override public Single<Transaction> createTransaction(int productId, String payerId) {
    final Transaction transaction =
        transactionMapper.create(productId, payerId);
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
                transactionMapper.create(productId, payerId)));
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