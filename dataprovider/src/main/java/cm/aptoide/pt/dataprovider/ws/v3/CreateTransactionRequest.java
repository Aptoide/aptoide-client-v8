/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/11/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
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

  public static CreateTransactionRequest of(int productId, int serviceId, String store,
      String metadata, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, int versionCode, String productTitle) {
    final BaseBody args = new BaseBody();
    args.put("productid", String.valueOf(productId));
    args.put("payType", String.valueOf(serviceId));
    args.put("reqType", "rest");
    args.put("product_name", productTitle);
    args.put("app_vercode", versionCode);
    args.put("paykey", metadata);
    args.put("repo", store);
    return new CreateTransactionRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<TransactionResponse> loadDataFromNetwork(Service service,
      boolean bypassCache) {
    return service.createTransaction(map, bypassCache);
  }
}
