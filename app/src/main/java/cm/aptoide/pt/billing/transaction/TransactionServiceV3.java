package cm.aptoide.pt.billing.transaction;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cm.aptoide.pt.billing.Customer;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.AuthorizationMapper;
import cm.aptoide.pt.billing.authorization.AuthorizationPersistence;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v3.GetTransactionRequest;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class TransactionServiceV3 implements TransactionService {

  private final TransactionMapper transactionMapper;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final TransactionFactory transactionFactory;
  private final AuthorizationPersistence authorizationPersistence;
  private final AuthorizationMapper authorizationMapper;
  private final Customer customer;
  private final Resources resources;

  public TransactionServiceV3(TransactionMapper transactionMapper,
      BodyInterceptor<BaseBody> bodyInterceptorV3, Converter.Factory converterFactory,
      OkHttpClient httpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, TransactionFactory transactionFactory,
      AuthorizationPersistence authorizationPersistence, AuthorizationMapper authorizationMapper,
      Customer customer, Resources resources) {
    this.transactionMapper = transactionMapper;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.transactionFactory = transactionFactory;
    this.authorizationPersistence = authorizationPersistence;
    this.authorizationMapper = authorizationMapper;
    this.customer = customer;
    this.resources = resources;
  }

  @Override public Single<Transaction> getTransaction(long productId) {
    return customer.getId()
        .flatMap(customerId -> GetTransactionRequest.of(productId, bodyInterceptorV3, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences)
            .observe(true)
            .toSingle()
            .map(response -> transactionMapper.map(customerId, productId, response)));
  }

  @Override
  public Single<Transaction> createTransaction(long productId, long serviceId, String payload) {
    return Single.zip(
        GetApkInfoRequest.of(productId, bodyInterceptorV3, httpClient, converterFactory,
            tokenInvalidator, sharedPreferences, resources)
            .observe(true)
            .toSingle(), customer.getId(),
        (response, customerId) -> authorizationMapper.map(customerId, serviceId, productId,
            AuthorizationFactory.PAYPAL_SDK, Authorization.Status.PENDING, response))
        .flatMap(authorization -> authorizationPersistence.saveAuthorization(authorization)
            .andThen(Single.just(
                transactionFactory.create(productId, authorization.getCustomerId(), serviceId,
                    productId, Transaction.Status.PENDING_SERVICE_AUTHORIZATION))));
  }
}