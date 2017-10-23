package cm.aptoide.pt.billing.networking;

import android.content.SharedPreferences;
import cm.aptoide.pt.billing.BillingIdManager;
import cm.aptoide.pt.billing.transaction.Transaction;
import cm.aptoide.pt.billing.transaction.TransactionFactory;
import cm.aptoide.pt.billing.transaction.TransactionService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.billing.CreateTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetTransactionRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class TransactionServiceV7 implements TransactionService {
  private final TransactionMapperV7 transactionMapper;
  private final BodyInterceptor<BaseBody> bodyInterceptorV7;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final BillingIdManager billingIdManager;
  private final TransactionFactory transactionFactory;

  public TransactionServiceV7(TransactionMapperV7 transactionMapper,
      BodyInterceptor<BaseBody> bodyInterceptorV7, Converter.Factory converterFactory,
      OkHttpClient httpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, BillingIdManager billingIdManager,
      TransactionFactory transactionFactory) {
    this.transactionMapper = transactionMapper;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.billingIdManager = billingIdManager;
    this.transactionFactory = transactionFactory;
  }

  @Override public Single<Transaction> getTransaction(String customerId, String productId) {
    return GetTransactionRequest.of(bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, billingIdManager.resolveProductId(productId))
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response.isSuccessful()) {
            final GetTransactionRequest.ResponseBody responseBody = response.body();
            if (responseBody != null && responseBody.isOk()) {
              return Single.just(transactionMapper.map(responseBody.getData()));
            }
            return Single.error(new IllegalArgumentException(V7.getErrorMessage(responseBody)));
          }

          if (response.code() == 404) {
            return Single.just(
                transactionFactory.create(billingIdManager.generateTransactionId(), customerId,
                    billingIdManager.generateServiceId(), productId, Transaction.Status.NEW));
          }

          return Single.just(
              transactionFactory.create(billingIdManager.generateTransactionId(), customerId,
                  billingIdManager.generateServiceId(), productId, Transaction.Status.FAILED));
        });
  }

  @Override public Single<Transaction> createTransaction(String customerId, String productId,
      String serviceId, String payload) {
    return CreateTransactionRequest.of(billingIdManager.resolveProductId(productId),
        billingIdManager.resolveServiceId(serviceId), payload, bodyInterceptorV7, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(transactionMapper.map(response.getData()));
          }
          return Single.just(
              transactionFactory.create(billingIdManager.generateTransactionId(), customerId,
                  billingIdManager.generateServiceId(), productId, Transaction.Status.FAILED));
        });
  }

  @Override public Single<Transaction> createTransaction(String customerId, String productId,
      String serviceId, String payload, String token) {
    return CreateTransactionRequest.of(billingIdManager.resolveProductId(productId),
        billingIdManager.resolveServiceId(serviceId), payload, token, bodyInterceptorV7, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(transactionMapper.map(response.getData()));
          }
          return Single.just(
              transactionFactory.create(billingIdManager.generateTransactionId(), customerId,
                  billingIdManager.generateServiceId(), productId, Transaction.Status.FAILED));
        });
  }
}
