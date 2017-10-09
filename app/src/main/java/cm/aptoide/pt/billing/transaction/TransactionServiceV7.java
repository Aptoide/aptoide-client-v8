package cm.aptoide.pt.billing.transaction;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.billing.CreateTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetTransactionsRequest;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Single;

public class TransactionServiceV7 implements TransactionService {
  private final TransactionMapper transactionMapper;
  private final BodyInterceptor<BaseBody> bodyInterceptorV7;
  private final Converter.Factory converterFactory;
  private final OkHttpClient httpClient;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;

  public TransactionServiceV7(TransactionMapper transactionMapper,
      BodyInterceptor<BaseBody> bodyInterceptorV7, Converter.Factory converterFactory,
      OkHttpClient httpClient, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    this.transactionMapper = transactionMapper;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public Single<List<Transaction>> getTransactions() {
    return GetTransactionsRequest.of(bodyInterceptorV7, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(transactionMapper.map(response.getData().getList()));
          }
          return Single.error(new IllegalArgumentException(V7.getErrorMessage(response)));
        });
  }

  @Override
  public Single<Transaction> createTransaction(long productId, int serviceId, String payload) {
    return CreateTransactionRequest.of(productId, serviceId, payload, bodyInterceptorV7, httpClient,
        converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true)
        .toSingle()
        .flatMap(response -> {
          if (response != null && response.isOk()) {
            return Single.just(transactionMapper.map(response.getData()));
          }
          return Single.error(new IllegalArgumentException(V7.getErrorMessage(response)));
        });
  }
}
