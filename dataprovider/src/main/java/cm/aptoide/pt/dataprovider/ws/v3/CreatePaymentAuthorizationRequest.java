/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 05/12/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v3.BaseV3Response;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class CreatePaymentAuthorizationRequest extends V3<BaseV3Response> {

  private final boolean hasAuthorizationCode;

  private CreatePaymentAuthorizationRequest(BaseBody baseBody,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, boolean hasAuthorizationCode,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor, tokenInvalidator,
        sharedPreferences);
    this.hasAuthorizationCode = hasAuthorizationCode;
  }

  public static CreatePaymentAuthorizationRequest of(int paymentId,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    BaseBody args = new BaseBody();
    args.put("payType", String.valueOf(paymentId));
    return new CreatePaymentAuthorizationRequest(args, bodyInterceptor, httpClient,
        converterFactory, false, tokenInvalidator, sharedPreferences);
  }

  public static CreatePaymentAuthorizationRequest of(int paymentId, String authorizationCode,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    BaseBody args = new BaseBody();
    args.put("payType", String.valueOf(paymentId));
    args.put("authToken", authorizationCode);
    args.put("reqType", "rest");
    return new CreatePaymentAuthorizationRequest(args, bodyInterceptor, httpClient,
        converterFactory, true, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<BaseV3Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    if (hasAuthorizationCode) {
      return interfaces.createPaymentAuthorizationWithCode(map);
    }
    return interfaces.createPaymentAuthorization(map);
  }
}
