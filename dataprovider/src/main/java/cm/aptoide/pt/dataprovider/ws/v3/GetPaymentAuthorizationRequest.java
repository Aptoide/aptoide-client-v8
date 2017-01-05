/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 05/01/2017.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.model.v3.PaymentAuthorizationResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 15/11/16.
 */
public class GetPaymentAuthorizationRequest extends V3<PaymentAuthorizationResponse> {

  private GetPaymentAuthorizationRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static GetPaymentAuthorizationRequest of(int paymentId, String accessToken) {
    BaseBody args = new BaseBody();
    args.put("access_token", accessToken);
    args.put("payType", String.valueOf(paymentId));
    return new GetPaymentAuthorizationRequest(BASE_HOST, args);
  }

  @Override protected Observable<PaymentAuthorizationResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getPaymentAuthorization(map);
  }
}
