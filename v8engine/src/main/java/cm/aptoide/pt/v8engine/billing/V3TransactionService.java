package cm.aptoide.pt.v8engine.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreateTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.billing.repository.TransactionFactory;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class V3TransactionService implements TransactionService {
  private final TransactionFactory transactionFactory;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public V3TransactionService(TransactionFactory transactionFactory,
      BodyInterceptor<BaseBody> bodyInterceptorV3, Converter.Factory converterFactory,
      OkHttpClient httpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.transactionFactory = transactionFactory;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public Single<Transaction> getTransaction(Product product, String payerId) {
    return Single.just(product instanceof InAppProduct)
        .flatMap(isInAppBilling -> {
          if (isInAppBilling) {
            return GetTransactionRequest.of(product.getId(),
                ((InAppProduct) product).getApiVersion(), bodyInterceptorV3, httpClient,
                converterFactory, tokenInvalidator, sharedPreferences)
                .observe()
                .cast(TransactionResponse.class)
                .toSingle();
          }
          return GetTransactionRequest.of(product.getId(), bodyInterceptorV3, httpClient,
              converterFactory, tokenInvalidator, sharedPreferences)
              .observe()
              .toSingle();
        })
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(transactionFactory.map(product.getId(), response, payerId));
          }
          return Single.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
        });
  }

  @Override public Single<Transaction> createTransaction(Product product, int paymentMethodId,
      String metadata, String payerId) {
    return Single.just(product instanceof InAppProduct)
        .flatMap(isInAppBilling -> {
          if (isInAppBilling) {
            return CreateTransactionRequest.ofInApp(product.getId(), paymentMethodId,
                ((InAppProduct) product).getDeveloperPayload(), metadata, bodyInterceptorV3,
                httpClient, converterFactory, tokenInvalidator, sharedPreferences,
                ((InAppProduct) product).getPackageVersionCode())
                .observe(true)
                .toSingle();
          }
          return CreateTransactionRequest.ofPaidApp(product.getId(), paymentMethodId,
              ((PaidAppProduct) product).getStoreName(), metadata, bodyInterceptorV3, httpClient,
              converterFactory, tokenInvalidator, sharedPreferences,
              ((PaidAppProduct) product).getPackageVersionCode())
              .observe(true)
              .toSingle();
        })
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(
                transactionFactory.create(product.getId(), metadata, Transaction.Status.COMPLETED,
                    payerId, paymentMethodId));
          }
          return Single.error(new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
        });
  }
}
