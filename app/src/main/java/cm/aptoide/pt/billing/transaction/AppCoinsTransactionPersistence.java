package cm.aptoide.pt.billing.transaction;

import cm.aptoide.pt.logger.Logger;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.HashMap;
import java.util.Map;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by jose_messejana on 26-10-2017.
 */

public class AppCoinsTransactionPersistence implements TransactionPersistence {

  private Map<String, Transaction> transactionList = new HashMap<>();
  private final PublishRelay<Transaction> transactionRelay;
  private AppCoinTransactionService appCoinTransactionService;
  private final TransactionFactory transactionFactory;

  public AppCoinsTransactionPersistence(Map<String, Transaction> transactionList,
      PublishRelay<Transaction> transactionRelay, AppCoinTransactionService appCoinTransactionService, TransactionFactory transactionFactory) {
    this.transactionList = transactionList;
    this.transactionRelay = transactionRelay;
    this.appCoinTransactionService = appCoinTransactionService;
    this.transactionFactory = transactionFactory;
  }

  @Override
  public Single<Transaction> createTransaction(String sellerId, String payerId, int paymentMethodId,
      String productId, Transaction.Status status, String payload, String metadata) {
    Logger.d("TAG123","create_repo");
    return appCoinTransactionService.createTransactionwithMeta(sellerId,payerId, paymentMethodId, productId, status
        ,metadata).observeOn(Schedulers.io());

  }

  @Override
  public Observable<Transaction> getTransaction(String sellerId, String payerId, String productId) {
    Logger.d("TAG123","getTransRepo");
    return transactionRelay;
  }

  @Override
  public Completable removeTransaction(String sellerId, String payerId, String productId) {
    Logger.d("TAG123", "NOT HERE");
    appCoinTransactionService.remove(productId+payerId);
    return Completable.fromAction(() -> {
      transactionList.remove(productId+payerId);
    });
  }

  @Override
  public Completable saveTransaction(Transaction transaction) {
    Logger.d("TAG123","save_repo");
    return Completable.fromAction(() -> {
      transactionList.put((transaction.getProductId() + transaction.getPayerId()), transaction);
      transactionRelay.call(transaction);
    });
  }
}
