/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/11/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.BaseV3Response;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class CreateTransactionRequest extends V3<TransactionResponse> {

  private CreateTransactionRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static CreateTransactionRequest ofInApp(int productId, int paymentId,
      String developerPayload, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, int versionCode) {
    final BaseBody args = getBaseBody(productId, paymentId, versionCode);
    args.put("developerPayload", developerPayload);
    return new CreateTransactionRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static CreateTransactionRequest ofInApp(int productId, int paymentId,
      String developerPayload, String metadata, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, int versionCode) {
    final BaseBody args = getBaseBody(productId, paymentId, versionCode);
    args.put("paykey", metadata);
    args.put("developerPayload", developerPayload);
    return new CreateTransactionRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static CreateTransactionRequest ofPaidApp(int productId, int paymentId, String store,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, int versionCode) {
    final BaseBody args = getBaseBody(productId, paymentId, versionCode);
    args.put("repo", store);
    return new CreateTransactionRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  public static CreateTransactionRequest ofPaidApp(int productId, int paymentId, String store,
      String metadata, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, int versionCode) {
    final BaseBody args = getBaseBody(productId, paymentId, versionCode);
    args.put("paykey", metadata);
    args.put("repo", store);
    return new CreateTransactionRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  private static BaseBody getBaseBody(int productId, int paymentId, int versionCode) {
    final BaseBody body = new BaseBody();
    body.put("productid", String.valueOf(productId));
    body.put("payType", String.valueOf(paymentId));
    body.put("reqType", "rest");
    body.put("app_vercode", versionCode);
    return body;
  }

  @Override
  protected Observable<TransactionResponse> loadDataFromNetwork(Service service, boolean bypassCache) {
    return service.createTransaction(map, bypassCache);
  }
}
