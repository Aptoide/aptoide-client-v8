/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 17/10/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetTransactionRequest extends V3<TransactionResponse> {

  public GetTransactionRequest(BaseBody baseBody, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
  }

  public static GetTransactionRequest of(int productId, int apiVersion,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    final BaseBody args = getBaseBody(productId);
    args.put("reqtype", "iabpurchasestatus");
    args.put("apiversion", String.valueOf(apiVersion));
    return new GetTransactionRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  private static BaseBody getBaseBody(int productId) {
    final BaseBody args = new BaseBody();
    args.put("mode", "json");
    args.put("payreqtype", "rest");
    args.put("productid", String.valueOf(productId));

    return args;
  }

  public static GetTransactionRequest of(int productId, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    final BaseBody args = getBaseBody(productId);
    args.put("reqtype", "apkpurchasestatus");
    return new GetTransactionRequest(args, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<TransactionResponse> loadDataFromNetwork(Service service,
      boolean bypassCache) {
    return service.getTransaction(map);
  }
}
