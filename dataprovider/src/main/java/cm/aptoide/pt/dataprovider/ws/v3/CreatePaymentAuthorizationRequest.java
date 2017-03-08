/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 05/12/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.model.v3.BaseV3Response;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import rx.Observable;

/**
 * Created by marcelobenites on 05/12/16.
 */

public class CreatePaymentAuthorizationRequest extends V3<BaseV3Response> {

  private CreatePaymentAuthorizationRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), isDebug()));
  }

  public static CreatePaymentAuthorizationRequest of(String accessToken, int paymentId) {
    BaseBody args = new BaseBody();
    args.put("access_token", accessToken);
    args.put("payType", String.valueOf(paymentId));
    return new CreatePaymentAuthorizationRequest(BASE_HOST, args);
  }

  @Override protected Observable<BaseV3Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.createPaymentAuthorization(map);
  }
}
