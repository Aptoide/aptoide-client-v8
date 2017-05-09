/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 05/12/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.BaseV3Response;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by marcelobenites on 05/12/16.
 */

public class CreatePaymentAuthorizationRequest extends V3<BaseV3Response> {

  private CreatePaymentAuthorizationRequest(BaseBody baseBody,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor);
  }

  public static CreatePaymentAuthorizationRequest of(String accessToken, int paymentId,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    BaseBody args = new BaseBody();
    args.put("access_token", accessToken);
    args.put("payType", String.valueOf(paymentId));
    return new CreatePaymentAuthorizationRequest(args, bodyInterceptor, httpClient,
        converterFactory);
  }

  @Override protected Observable<BaseV3Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.createPaymentAuthorization(map);
  }
}
