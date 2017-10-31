package cm.aptoide.pt.billing.transaction;

import android.content.SharedPreferences;
import cm.aptoide.pt.billing.BillingIdResolver;
import cm.aptoide.pt.billing.Product;
import cm.aptoide.pt.billing.view.appcoin.TransactionSimulator;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.logger.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jose_messejana on 26-10-2017.
 */

public class AppCoinTransactionService implements TransactionService {

  private final TransactionFactory transactionFactory;
  private Transaction currentTransaction;
  private Map<String, Transaction> transactionList = new HashMap<>();
  //private Map<String, AppCoinTransaction> appCoinTransactionList = new HashMap<>();
  private Map<String, TransactionSimulator> transactionSimList = new HashMap<>();
  private int count = 0;
  private Map<String, Product> products = new HashMap<>();

  public AppCoinTransactionService(TransactionMapper transactionMapper,
      BodyInterceptor<BaseBody> bodyInterceptorV3, Converter.Factory converterFactory,
      OkHttpClient httpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, TransactionFactory transactionFactory,
      BillingIdResolver idResolver) {
    this.transactionFactory = transactionFactory;
  }

  @Override
  public Single<Transaction> getTransaction(String sellerId, String payerId, Product product) {
    Transaction transaction = transactionList.get(concat(product.getId(), payerId));
    Logger.d("TAG123", "get");
    if (transaction != null) {
      if (!transactionSimList.isEmpty()) {
        TransactionSimulator transactionSimulator =
            transactionSimList.get(concat(product.getId(), payerId));
        transactionSimulator.getStatus();
        if (transactionSimulator != null) {
          switch (transactionSimulator.getStatus()) {
            case COMPLETED:
              return createTransactionStatusUpdate(sellerId, product.getId(),
                  transaction.getPaymentMethodId(), payerId, Transaction.Status.COMPLETED);
            case FAILED:
              return createTransactionStatusUpdate(sellerId, product.getId(),
                  transaction.getPaymentMethodId(), payerId, Transaction.Status.FAILED);
            case CANCELED:
              return createTransactionStatusUpdate(sellerId, product.getId(),
                  transaction.getPaymentMethodId(), payerId, Transaction.Status.CANCELED);
            case PENDING:
              break;
            default:
              return createTransactionStatusUpdate(sellerId, product.getId(),
                  transaction.getPaymentMethodId(), payerId, Transaction.Status.UNKNOWN);
          }
        }
      }
    } else {
      return createTransactionwithstatus(product.getId(), null, Transaction.Status.NEW, payerId,
          -1);
    }
    return Single.just(transaction);
  }

  @Override
  public Single<Transaction> createTransaction(String sellerId, String payerId, int paymentMethodId,
      Product product, String payload) {
    Logger.d("TAG123", "create_tran");
    Transaction transaction = null;
    products.put(product.getId(), product);
    transaction = transactionFactory.create(null, payerId, paymentMethodId, product.getId(),
        Transaction.Status.PENDING_USER_AUTHORIZATION, null, null, null, null, null);
    saveTransaction(transaction);
    return Single.just(transaction);
  }

  @Override
  public Single<Transaction> createTransaction(String sellerId, String payerId, int paymentMethodId,
      Product product, String metadata, String payload) {
    Logger.d("TAG123", "create_tran2");
    products.put(product.getId(), product);
    Transaction transaction =
        transactionFactory.create(null, payerId, paymentMethodId, product.getId(),
            Transaction.Status.PENDING_USER_AUTHORIZATION, null, null, null, null, null);
    saveTransaction(transaction);
    return Single.just(transaction);
  }

  public Single<Transaction> createTransactionwithMeta(String sellerId, String payerId,
      int paymentMethodId, String productId, Transaction.Status status, String metadata) {
    Transaction transaction = null;
    try {
      Logger.d("TAG123", "create_tran_meta_try" + status);
      //AppCoin appcoin = new AppCoin("user","key", "key");
      //AppCoinTransaction appcointransaction = appcoin.makeTransaction;
      //appcointransactionList.add(appcoinTransaction)
      TransactionSimulator transactionSimulator = new TransactionSimulator();
      Single.just(true)
          .delay(TransactionSimulator.TIME_FOR_TEST_TRANSACTION, TimeUnit.MILLISECONDS)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .doOnSuccess(__ -> transactionSimulator.startThread())
          .subscribe(__ -> {
          }, throwable -> throwable.printStackTrace());
      addTStransaction(productId, payerId, transactionSimulator);
      transaction =
          transactionFactory.create(sellerId, payerId, paymentMethodId, productId, status, metadata,
              null, null, null, null);
      saveTransaction(transaction);
      Logger.d("TAG123", "create: " + transaction.getStatus() + transaction.getProductId());
      return Single.just(transaction);
    } catch (Exception e) {
      Logger.d("TAG123", "create_tran_meta_catch");
      transaction = transactionFactory.create(sellerId, payerId, paymentMethodId, productId,
          Transaction.Status.FAILED, metadata, null, null, null, null);
      saveTransaction(transaction);
    }
    return Single.just(transaction);
  }

  public Single<Transaction> createTransactionStatusUpdate(String sellerId, String productid,
      int paymentMethodId, String payerId, Transaction.Status status) { //made
    Transaction transaction = null;
    Logger.d("TAG123", "create_tran_update");
    transaction =
        transactionFactory.create(sellerId, payerId, paymentMethodId, productid, status, null, null,
            null, null, null);
    saveTransaction(transaction).andThen(Single.just(transaction));
    return Single.just(transaction);
  }

  public Single<Transaction> createTransactionwithstatus(String productId, String metadata,
      Transaction.Status status, String payerId, int paymentMethodId) {
    Logger.d("TAG123", "create_tran_status");
    Transaction transaction =
        transactionFactory.create(null, payerId, paymentMethodId, productId, status, metadata, null,
            null, null, null);
    currentTransaction = transaction;
    saveTransaction(transaction);
    return Single.just(transaction);
  }

  public Completable saveTransaction(Transaction transaction) {
    transactionList.put(concat(transaction.getProductId(), transaction.getPayerId()), transaction);
    Logger.d("TAG123", "save" + transaction.getStatus());
    return Completable.complete();
  }

  public Transaction getCurrentTransaction() {
    return currentTransaction;
  }

  private String concat(String productId, String payerId) {
    return productId + payerId;
  }

  public void addTStransaction(String productID, String payerID, TransactionSimulator transaction) {
    transactionSimList.put(concat(productID, payerID), transaction);
  }

 /* public void addCBtransaction(String productID, String payerID, String hash){
    coinbaseCBTransactionList.put(concat(productID,payerID),hash);
  } */

  public void remove(String key) {
    transactionList.remove(key);
  }
}
