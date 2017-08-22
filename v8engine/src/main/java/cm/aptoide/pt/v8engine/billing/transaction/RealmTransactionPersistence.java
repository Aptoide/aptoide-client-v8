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

  @Override public Single<Transaction> createTransaction(String sellerId, String payerId,
      int paymentMethodId, String productId, Transaction.Status status, String payload,
      String metadata) {
    final Transaction transaction =
        transactionFactory.create(sellerId, payerId, paymentMethodId, productId, status,
            metadata, null, null, null, payload);
    return saveTransaction(transaction).andThen(Single.just(transaction));
  }

  @Override public Observable<Transaction> getTransaction(String sellerId, String payerId,
      String productId) {
    return transactionRelay.flatMap(
        transaction -> resolveTransaction(payerId, productId, transaction, sellerId))
        .startWith(resolveTransaction(payerId, productId,
            nonLocalTransactions.get(getTransactionsKey(payerId, productId, sellerId)), sellerId))
        .subscribeOn(Schedulers.io());
  }

  @Override
  public Completable removeTransaction(String sellerId, String payerId, String productId) {
    return Completable.fromAction(() -> {
      removeLocalTransaction(payerId, productId, sellerId);
      nonLocalTransactions.remove(getTransactionsKey(payerId, productId, sellerId));
    });
  }

  @Override public Completable saveTransaction(Transaction transaction) {
    return Completable.fromAction(() -> {

      final String transactionsKey =
          getTransactionsKey(transaction.getPayerId(), transaction.getProductId(),
              transaction.getSellerId());
      if (transaction instanceof LocalTransaction) {
        localTransactions.insert(transactionMapper.map(transaction, transactionsKey));
        nonLocalTransactions.remove(transactionsKey);
      } else {
        nonLocalTransactions.put(transactionsKey, transaction);
      }

      transactionRelay.call(transaction);
    });
  }

  private Observable<Transaction> resolveTransaction(String payerId, String productId,
      Transaction nonLocalTransaction, String sellerId) {

    final Transaction localTransaction = getLocalTransaction(payerId, productId, sellerId);

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

  private Transaction getLocalTransaction(String payerId, String productId, String sellerId) {
    @Cleanup Realm realm = localTransactions.get();

    final PaymentConfirmation realmTransaction =
        getRealmTransaction(payerId, productId, realm, sellerId);

    Transaction localTransaction = null;
    if (realmTransaction != null) {
      localTransaction = transactionMapper.map(realmTransaction);
    }
    return localTransaction;
  }

  private void removeLocalTransaction(String payerId, String productId, String sellerId) {
    @Cleanup final Realm realm = localTransactions.get();

    final PaymentConfirmation realmTransaction =
        getRealmTransaction(payerId, productId, realm, sellerId);

    if (realmTransaction != null) {
      realm.beginTransaction();
      realmTransaction.deleteFromRealm();
      realm.commitTransaction();
    }
  }

  private PaymentConfirmation getRealmTransaction(String payerId, String productId, Realm realm,
      String sellerId) {
    return realm.where(PaymentConfirmation.class)
        .equalTo(PaymentConfirmation.PRODUCT_ID, productId)
        .equalTo(PaymentConfirmation.PAYER_ID, payerId)
        .equalTo(PaymentConfirmation.SELLER_ID, sellerId)
        .findFirst();
  }

  private String getTransactionsKey(String payerId, String productId, String sellerId) {
    return payerId + productId + sellerId;
  }
}