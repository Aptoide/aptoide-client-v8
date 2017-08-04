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
import rx.schedulers.Schedulers;

public class RealmTransactionPersistence implements TransactionPersistence {

  private final Map<String, Transaction> nonLocalTransactions;
  private final PublishRelay<Transaction> transactionRelay;
  private final Database localTransactions;
  private final TransactionMapper transactionMapper;
  private final TransactionFactory transactionFactory;

  public RealmTransactionPersistence(Map<String, Transaction> nonLocalTransactions,
      PublishRelay<Transaction> transactionRelay, Database database,
      TransactionMapper transactionMapper, TransactionFactory transactionFactory) {
    this.nonLocalTransactions = nonLocalTransactions;
    this.transactionRelay = transactionRelay;
    this.localTransactions = database;
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
    return transactionRelay.flatMap(
        transaction -> resolveTransaction(payerId, productId, transaction))
        .startWith(resolveTransaction(payerId, productId,
            nonLocalTransactions.get(getServerTransactionsKey(payerId, productId))))
        .subscribeOn(Schedulers.io());
  }

  @Override public Completable removeTransaction(String payerId, int productId) {
    return Completable.fromAction(() -> {
      removeLocalTransaction(payerId, productId);
      nonLocalTransactions.remove(getServerTransactionsKey(payerId, productId));
    });
  }

  @Override public Completable saveTransaction(Transaction transaction) {
    return Completable.fromAction(() -> {

      if (transaction instanceof LocalTransaction) {
        localTransactions.insert(transactionMapper.map(transaction));
        nonLocalTransactions.remove(
            getServerTransactionsKey(transaction.getPayerId(), transaction.getProductId()));
      } else {
        nonLocalTransactions.put(
            getServerTransactionsKey(transaction.getPayerId(), transaction.getProductId()),
            transaction);
      }

      transactionRelay.call(transaction);
    });
  }

  private Observable<Transaction> resolveTransaction(String payerId, int productId,
      Transaction nonLocalTransaction) {

    final Transaction localTransaction = getLocalTransaction(payerId, productId);

    if (localTransaction == null && nonLocalTransaction == null) {
      return Observable.empty();
    }

    if (nonLocalTransaction == null) {
      return Observable.just(localTransaction);
    }

    if (localTransaction == null) {
      return Observable.just(nonLocalTransaction);
    }

    if (nonLocalTransaction.getPaymentMethodId() == -1) {
      return Observable.just(localTransaction);
    }

    if (localTransaction.isPending()) {
      return Observable.just(localTransaction);
    }

    return Observable.just(nonLocalTransaction);
  }

  private Transaction getLocalTransaction(String payerId, int productId) {
    @Cleanup Realm realm = localTransactions.get();

    final PaymentConfirmation realmTransaction = getRealmTransaction(payerId, productId, realm);

    Transaction localTransaction = null;
    if (realmTransaction != null) {
      localTransaction = transactionMapper.map(realmTransaction);
    }
    return localTransaction;
  }

  private void removeLocalTransaction(String payerId, int productId) {
    @Cleanup final Realm realm = localTransactions.get();

    final PaymentConfirmation realmTransaction = getRealmTransaction(payerId, productId, realm);

    if (realmTransaction != null) {
      realm.beginTransaction();
      realmTransaction.deleteFromRealm();
      realm.commitTransaction();
    }
  }

  private PaymentConfirmation getRealmTransaction(String payerId, int productId, Realm realm) {
    return realm.where(PaymentConfirmation.class)
        .equalTo(PaymentConfirmation.PRODUCT_ID, productId)
        .equalTo(PaymentConfirmation.PAYER_ID, payerId)
        .findFirst();
  }

  private String getServerTransactionsKey(String payerId, int productId) {
    return payerId + productId;
  }
}