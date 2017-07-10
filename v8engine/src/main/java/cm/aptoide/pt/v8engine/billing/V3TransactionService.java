package cm.aptoide.pt.v8engine.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreateTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetTransactionRequest;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import java.util.List;
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
          return getErrorTransaction(response.getErrors(), payerId, product, -1, null);
        });
  }

  @Override
  public Single<Transaction> createTransaction(Product product, int paymentMethodId, String payerId,
      String metadata) {
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
          if (response.isOk()) {
            return Single.just(
                transactionFactory.create(product.getId(), Transaction.Status.COMPLETED, payerId,
                    paymentMethodId, metadata));
          }
          return getErrorTransaction(response.getErrors(), payerId, product, paymentMethodId,
              metadata);
        });
  }

  @Override public Single<Transaction> createTransaction(Product product, int paymentMethodId,
      String payerId) {
    return Single.just(product instanceof InAppProduct)
        .flatMap(isInAppBilling -> {
          if (isInAppBilling) {
            return CreateTransactionRequest.ofInApp(product.getId(), paymentMethodId,
                ((InAppProduct) product).getDeveloperPayload(), bodyInterceptorV3, httpClient,
                converterFactory, tokenInvalidator, sharedPreferences,
                ((InAppProduct) product).getPackageVersionCode())
                .observe(true)
                .toSingle();
          }
          return CreateTransactionRequest.ofPaidApp(product.getId(), paymentMethodId,
              ((PaidAppProduct) product).getStoreName(), bodyInterceptorV3, httpClient,
              converterFactory, tokenInvalidator, sharedPreferences,
              ((PaidAppProduct) product).getPackageVersionCode())
              .observe(true)
              .toSingle();
        })
        .flatMap(response -> {
          if (response.isOk()) {
            return getTransaction(product, payerId);
          }
          return getErrorTransaction(response.getErrors(), payerId, product, paymentMethodId, null);
        });
  }

  private Single<Transaction> getErrorTransaction(List<ErrorResponse> errors, String payerId,
      Product product, int paymentMethodId, String metadata) {

    Transaction transaction =
        transactionFactory.create(product.getId(), Transaction.Status.FAILED, payerId,
            paymentMethodId, metadata);

    if (errors == null || errors.isEmpty()) {
      return Single.just(transaction);
    }

    final ErrorResponse error = errors.get(0);

    if ("PRODUCT-204".equals(error.code) || "PRODUCT-209".equals(error.code)) {
      transaction =
          transactionFactory.create(product.getId(), Transaction.Status.PENDING_USER_AUTHORIZATION,
              payerId, paymentMethodId, metadata);
    }

    if ("PRODUCT-200".equals(error.code)) {
      transaction =
          transactionFactory.create(product.getId(), Transaction.Status.COMPLETED, payerId,
              paymentMethodId, metadata);
    }

    if ("PRODUCT-214".equals(error.code)) {
      transaction = transactionFactory.create(product.getId(), Transaction.Status.NEW, payerId,
          paymentMethodId, metadata);
    }

    if ("PRODUCT-216".equals(error.code)) {
      transaction = transactionFactory.create(product.getId(), Transaction.Status.PENDING, payerId,
          paymentMethodId, metadata);
    }

    if ("PRODUCT-7".equals(error.code)
        || "PRODUCT-8".equals(error.code)
        || "PRODUCT-9".equals(error.code)
        || "PRODUCT-102".equals(error.code)
        || "PRODUCT-104".equals(error.code)
        || "PRODUCT-206".equals(error.code)
        || "PRODUCT-207".equals(error.code)
        || "PRODUCT-208".equals(error.code)
        || "PRODUCT-215".equals(error.code)
        || "PRODUCT-217".equals(error.code)) {
      transaction = transactionFactory.create(product.getId(), Transaction.Status.FAILED, payerId,
          paymentMethodId, metadata);
    }

    return Single.just(transaction);
  }
}
