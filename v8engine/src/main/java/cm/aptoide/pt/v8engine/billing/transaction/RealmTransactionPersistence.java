/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 01/09/2016.
 */

package cm.aptoide.pt.v8engine.billing.transaction;

import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import com.jakewharton.rxrelay.PublishRelay;
import io.realm.Realm;
import java.util.Map;
import lombok.Cleanup;
import rx.Completable;
import rx.Observable;
import rx.Single;

public class RealmTransactionPersistence implements TransactionPersistence {

  private final Map<String, Transaction> transactions;
  private final PublishRelay<Transaction> transactionRelay;
  private final Database database;
  private final TransactionMapper transactionMapper;
  private final TransactionFactory transactionFactory;

  public RealmTransactionPersistence(Map<String, Transaction> transactions,
      PublishRelay<Transaction> transactionRelay, Database database,
      TransactionMapper transactionMapper, TransactionFactory transactionFactory) {
    this.transactions = transactions;
    this.transactionRelay = transactionRelay;
    this.database = database;
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
    return restoreLocalTransaction(productId, payerId).andThen(
        transactionRelay.startWith(Observable.defer(() -> {
          if (transactions.containsKey(getTransactionsKey(payerId, productId))) {
            return Observable.just(transactions.get(getTransactionsKey(payerId, productId)));
          }
          return Observable.empty();
        })));
  }

  @Override public Completable removeTransaction(String payerId, int productId) {
    return Completable.fromAction(() -> {
      removeRealmTransaction(payerId, productId);
      transactions.remove(getTransactionsKey(payerId, productId));
    });
  }

  @Override public Completable removeAllTransactions() {
    return Completable.fromAction(() -> {
      database.deleteAll(PaymentConfirmation.class);
      transactions.clear();
    });
  }

  @Override public Completable saveTransaction(Transaction transaction) {
    return Completable.fromAction(() -> {

      saveTransactionInMemory(transaction);

      if (transaction instanceof LocalTransaction) {
        database.insert(transactionMapper.map(transaction));
      } else {
        removeRealmTransaction(transaction.getPayerId(), transaction.getProductId());
      }
    });
  }

  private void removeRealmTransaction(String payerId, int productId) {
    @Cleanup final Realm realm = database.get();

    final PaymentConfirmation localTransaction = getRealmTransaction(payerId, productId, realm);

    if (localTransaction != null) {
      localTransaction.deleteFromRealm();
    }
  }

  private Completable restoreLocalTransaction(int productId, String payerId) {
    return Completable.fromAction(() -> {
      @Cleanup Realm realm = database.get();

      final PaymentConfirmation realmTransaction = getRealmTransaction(payerId, productId, realm);

      if (realmTransaction != null) {
        saveTransactionInMemory(transactionMapper.map(realmTransaction));
      }
    });
  }

  private PaymentConfirmation getRealmTransaction(String payerId, int productId, Realm realm) {
    return realm.where(PaymentConfirmation.class)
        .equalTo(PaymentConfirmation.PRODUCT_ID, productId)
        .equalTo(PaymentConfirmation.PAYER_ID, payerId)
        .findFirst();
  }

  private String getTransactionsKey(String payerId, int productId) {
    return payerId + productId;
  }

  private void saveTransactionInMemory(Transaction transaction) {
    transactions.put(getTransactionsKey(transaction.getPayerId(), transaction.getProductId()),
        transaction);
    transactionRelay.call(transaction);
  }
}