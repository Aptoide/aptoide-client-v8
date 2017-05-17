/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 05/01/2017.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v3.PaymentAuthorizationsResponse;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class GetPaymentAuthorizationsRequest extends V3<PaymentAuthorizationsResponse> {

  private GetPaymentAuthorizationsRequest(BaseBody baseBody,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    super(baseBody, httpClient, converterFactory, bodyInterceptor);
  }

  public static GetPaymentAuthorizationsRequest of(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory) {
    return new GetPaymentAuthorizationsRequest(new BaseBody(), bodyInterceptor, httpClient,
        converterFactory);
  }

  @Override
  protected Observable<PaymentAuthorizationsResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getPaymentAuthorization(map);
  }
}
