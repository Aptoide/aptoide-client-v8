package cm.aptoide.pt.billing.networking;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.billing.transaction.TransactionService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetTransactionRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class TransactionServiceV3 implements TransactionService {

  private final TransactionMapperV3 transactionMapper;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final TransactionFactory transactionFactory;
  private final Resources resources;
  private final BillingIdManager billingIdManager;

  public TransactionServiceV3(TransactionMapperV3 transactionMapper,
      BodyInterceptor<BaseBody> bodyInterceptorV3, Converter.Factory converterFactory,
      OkHttpClient httpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, TransactionFactory transactionFactory,
      Resources resources, BillingIdManager billingIdManager) {
    this.transactionMapper = transactionMapper;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.transactionFactory = transactionFactory;
    this.resources = resources;
    this.billingIdManager = billingIdManager;
  }

  @Override public Single<Transaction> getTransaction(String customerId, String productId) {
    return GetApkInfoRequest.of(billingIdManager.resolveProductId(productId), bodyInterceptorV3,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences, resources)
        .observe(false)
        .toSingle()
        .flatMap(response -> {
          if (response.isOk()) {
            if (response.isPaid()) {
              return GetTransactionRequest.of(response.getPayment()
                      .getMetadata()
                      .getProductId(), bodyInterceptorV3, httpClient, converterFactory,
                  tokenInvalidator, sharedPreferences)
                  .observe(true)
                  .toSingle()
                  .map(transactionResponse -> transactionMapper.map(customerId,
                      billingIdManager.generateTransactionId(
                          billingIdManager.resolveProductId(productId)), transactionResponse,
                      productId));
            }
            return Single.just(transactionFactory.create(billingIdManager.generateTransactionId(
                billingIdManager.resolveProductId(productId)), customerId, productId,
                Transaction.Status.COMPLETED));
          }

          return Single.just(transactionFactory.create(
              billingIdManager.generateTransactionId(billingIdManager.resolveProductId(productId)),
              customerId, productId, Transaction.Status.FAILED));
        });
  }

  @Override public Single<Transaction> createTransaction(String customerId, String productId,
      String serviceId, String payload) {
    return GetApkInfoRequest.of(billingIdManager.resolveProductId(productId), bodyInterceptorV3,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences, resources)
        .observe(true)
        .toSingle()
        .map(response -> {

          if (response.isOk()) {
            if (response.isPaid()) {
              return transactionFactory.create(billingIdManager.generateTransactionId(
                  billingIdManager.resolveProductId(productId)), customerId, productId,
                  Transaction.Status.PENDING_SERVICE_AUTHORIZATION);
            }
            return transactionFactory.create(billingIdManager.generateTransactionId(
                billingIdManager.resolveProductId(productId)), customerId, productId,
                Transaction.Status.COMPLETED);
          }
          return transactionFactory.create(
              billingIdManager.generateTransactionId(billingIdManager.resolveProductId(productId)),
              customerId, productId, Transaction.Status.FAILED);
        });
  }

  @Override public Single<Transaction> createTransaction(String customerId, String productId,
      String serviceId, String payload, String token) {
    return Single.error(new IllegalStateException("Not implemented!"));
  }
}
