package cm.aptoide.pt.v8engine.billing.transaction;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CreateTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetTransactionRequest;
import cm.aptoide.pt.v8engine.billing.BillingIdResolver;
import cm.aptoide.pt.v8engine.billing.PaymentMethodMapper;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.product.InAppProduct;
import cm.aptoide.pt.v8engine.billing.product.PaidAppProduct;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class V3TransactionService implements TransactionService {
  private final TransactionMapper transactionMapper;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final TransactionFactory transactionFactory;
  private final BillingIdResolver idResolver;

  public V3TransactionService(TransactionMapper transactionMapper,
      BodyInterceptor<BaseBody> bodyInterceptorV3, Converter.Factory converterFactory,
      OkHttpClient httpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, TransactionFactory transactionFactory,
      BillingIdResolver idResolver) {
    this.transactionMapper = transactionMapper;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.transactionFactory = transactionFactory;
    this.idResolver = idResolver;
  }

  @Override
  public Single<Transaction> getTransaction(String sellerId, String payerId, Product product) {
    return Single.just(product instanceof InAppProduct)
        .flatMap(isInAppBilling -> {
          if (isInAppBilling) {
            return GetTransactionRequest.of(((InAppProduct) product).getInternalId(),
                ((InAppProduct) product).getApiVersion(), bodyInterceptorV3, httpClient,
                converterFactory, tokenInvalidator, sharedPreferences)
                .observe()
                .cast(TransactionResponse.class)
                .toSingle();
          }
          return GetTransactionRequest.of(((PaidAppProduct) product).getInternalId(),
              bodyInterceptorV3, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
              .observe()
              .toSingle();
        })
        .map(response -> {
          if (response != null && response.isOk()) {
            return transactionMapper.map(product.getId(), response, payerId, null, sellerId);
          }
          return getErrorTransaction(sellerId, response.getErrors(), payerId, product, -1,
              null, null);
        });
  }

  @Override
  public Single<Transaction> createTransaction(String sellerId, String payerId, int paymentMethodId,
      Product product, String metadata, String payload) {
    return Single.just(product instanceof InAppProduct)
        .flatMap(isInAppBilling -> {
          if (isInAppBilling) {
            return CreateTransactionRequest.ofInApp(((InAppProduct) product).getInternalId(),
                paymentMethodId, payload, metadata, bodyInterceptorV3, httpClient, converterFactory,
                tokenInvalidator, sharedPreferences,
                ((InAppProduct) product).getPackageVersionCode(), product.getTitle())
                .observe(true)
                .toSingle();
          }
          return CreateTransactionRequest.ofPaidApp(((PaidAppProduct) product).getInternalId(),
              paymentMethodId, idResolver.resolveStoreName(sellerId), metadata,
              bodyInterceptorV3, httpClient, converterFactory, tokenInvalidator, sharedPreferences,
              ((PaidAppProduct) product).getPackageVersionCode(), product.getTitle())
              .observe(true)
              .toSingle();
        })
        .map(response -> {
          if (response.isOk()) {
            return transactionMapper.map(product.getId(), response, payerId, payload, sellerId);
          }
          return getErrorTransaction(sellerId, response.getErrors(), payerId, product,
              paymentMethodId, metadata, payload);
        });
  }

  @Override
  public Single<Transaction> createTransaction(String sellerId, String payerId, int paymentMethodId,
      Product product, String payload) {

    if (paymentMethodId == PaymentMethodMapper.PAYPAL) {
      return Single.just(
          transactionFactory.create(sellerId, payerId, paymentMethodId, product.getId(),
              Transaction.Status.PENDING_USER_AUTHORIZATION, null, null, null, null, payload));
    }

    return Single.just(product instanceof InAppProduct)
        .flatMap(isInAppBilling -> {
          if (isInAppBilling) {
            return CreateTransactionRequest.ofInApp(((InAppProduct) product).getInternalId(),
                paymentMethodId, payload, bodyInterceptorV3, httpClient, converterFactory,
                tokenInvalidator, sharedPreferences,
                ((InAppProduct) product).getPackageVersionCode(), product.getTitle())
                .observe(true)
                .toSingle();
          }
          return CreateTransactionRequest.ofPaidApp(((PaidAppProduct) product).getInternalId(),
              paymentMethodId, idResolver.resolveStoreName(sellerId), bodyInterceptorV3,
              httpClient, converterFactory, tokenInvalidator, sharedPreferences,
              ((PaidAppProduct) product).getPackageVersionCode(), product.getTitle())
              .observe(true)
              .toSingle();
        })
        .map(response -> {
          if (response.isOk()) {
            return transactionMapper.map(product.getId(), response, payerId, payload, sellerId);
          }
          return getErrorTransaction(sellerId, response.getErrors(), payerId, product,
              paymentMethodId, null, payload);
        });
  }

  private Transaction getErrorTransaction(String sellerId, List<ErrorResponse> errors,
      String payerId, Product product, int paymentMethodId, String metadata, String payload) {

    Transaction transaction =
        transactionFactory.create(sellerId, payerId, paymentMethodId, product.getId(),
            Transaction.Status.FAILED, metadata, null, null, null, payload);

    if (errors == null || errors.isEmpty()) {
      return transaction;
    }

    final ErrorResponse error = errors.get(0);

    if ("PRODUCT-204".equals(error.code) || "PRODUCT-209".equals(error.code)) {
      transaction =
          transactionFactory.create(sellerId, payerId, paymentMethodId, product.getId(),
              Transaction.Status.PENDING_USER_AUTHORIZATION, metadata, null, null, null, payload);
    }

    if ("PRODUCT-200".equals(error.code)) {
      transaction =
          transactionFactory.create(sellerId, payerId, paymentMethodId, product.getId(),
              Transaction.Status.COMPLETED, metadata, null, null, null, payload);
    }

    if ("PRODUCT-214".equals(error.code)) {
      transaction =
          transactionFactory.create(sellerId, payerId, paymentMethodId, product.getId(),
              Transaction.Status.NEW, metadata, null, null, null, payload);
    }

    if ("PRODUCT-216".equals(error.code)) {
      transaction =
          transactionFactory.create(sellerId, payerId, paymentMethodId, product.getId(),
              Transaction.Status.PENDING, metadata, null, null, null, payload);
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
      transaction =
          transactionFactory.create(sellerId, payerId, paymentMethodId, product.getId(),
              Transaction.Status.FAILED, metadata, null, null, null, payload);
    }

    return transaction;
  }
}
