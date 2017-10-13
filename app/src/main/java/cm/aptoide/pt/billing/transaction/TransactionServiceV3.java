package cm.aptoide.pt.billing.transaction;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.billing.Customer;
import cm.aptoide.pt.billing.IdResolver;
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
  private final Customer customer;
  private final Resources resources;
  private final IdResolver idResolver;

  public TransactionServiceV3(TransactionMapperV3 transactionMapper,
      BodyInterceptor<BaseBody> bodyInterceptorV3, Converter.Factory converterFactory,
      OkHttpClient httpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, TransactionFactory transactionFactory, Customer customer,
      Resources resources, IdResolver idResolver) {
    this.transactionMapper = transactionMapper;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.transactionFactory = transactionFactory;
    this.customer = customer;
    this.resources = resources;
    this.idResolver = idResolver;
  }

  @Override public Single<Transaction> getTransaction(String productId) {
    return Single.zip(
        GetApkInfoRequest.of(idResolver.resolveProductId(productId), bodyInterceptorV3, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences, resources)
            .observe(false)
            .toSingle(), customer.getId(), (response, customerId) -> {

          if (response.isOk()) {
            if (response.isPaid()) {
              return GetTransactionRequest.of(response.getPayment()
                      .getMetadata()
                      .getProductId(), bodyInterceptorV3, httpClient, converterFactory,
                  tokenInvalidator, sharedPreferences)
                  .observe(true)
                  .toSingle()
                  .map(transactionResponse -> transactionMapper.map(customerId,
                      idResolver.generateTransactionId(idResolver.resolveProductId(productId)),
                      transactionResponse, productId));
            }
            return Single.just(transactionFactory.create(
                idResolver.generateTransactionId(idResolver.resolveProductId(productId)),
                customerId, idResolver.generateServiceId(1), productId,
                Transaction.Status.COMPLETED));
          }

          return Single.just(transactionFactory.create(
              idResolver.generateTransactionId(idResolver.resolveProductId(productId)), customerId,
              idResolver.generateServiceId(1), productId, Transaction.Status.FAILED));
        })
        .flatMap(single -> single);
  }

  @Override
  public Single<Transaction> createTransaction(String productId, String serviceId, String payload) {
    return Single.zip(
        GetApkInfoRequest.of(idResolver.resolveProductId(productId), bodyInterceptorV3, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences, resources)
            .observe(true)
            .toSingle(), customer.getId(), (response, customerId) -> {

          if (response.isOk()) {
            if (response.isPaid()) {
              return transactionFactory.create(
                  idResolver.generateTransactionId(idResolver.resolveProductId(productId)),
                  customerId, serviceId, productId,
                  Transaction.Status.PENDING_SERVICE_AUTHORIZATION);
            }
            return transactionFactory.create(
                idResolver.generateTransactionId(idResolver.resolveProductId(productId)),
                customerId, idResolver.generateServiceId(1), productId,
                Transaction.Status.COMPLETED);
          }
          return transactionFactory.create(
              idResolver.generateTransactionId(idResolver.resolveProductId(productId)), customerId,
              idResolver.generateServiceId(1), productId, Transaction.Status.FAILED);
        });
  }
}