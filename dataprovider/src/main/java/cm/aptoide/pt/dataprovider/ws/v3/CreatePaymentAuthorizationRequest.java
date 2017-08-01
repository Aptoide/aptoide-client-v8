/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 05/12/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.PaymentAuthorizationResponse;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class CreatePaymentAuthorizationRequest extends V3<PaymentAuthorizationResponse> {

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

  @Override protected Observable<PaymentAuthorizationResponse> loadDataFromNetwork(Service service,
      boolean bypassCache) {
    if (hasAuthorizationCode) {
      return service.createPaymentAuthorizationWithCode(map, bypassCache);
    }
    return service.createPaymentAuthorization(map, bypassCache);
  }
}
