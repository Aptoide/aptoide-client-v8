/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 15/11/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.model.v3.GetProductPurchaseAuthorizationResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 15/11/16.
 */
public class GetProductPurchaseAuthorizationRequest extends V3<GetProductPurchaseAuthorizationResponse> {

  private GetProductPurchaseAuthorizationRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static GetProductPurchaseAuthorizationRequest of(String accessToken, int paymentId) {
    BaseBody args = new BaseBody();
    args.put("access_token", accessToken);
    args.put("payType", String.valueOf(paymentId));
    return new GetProductPurchaseAuthorizationRequest(BASE_HOST, args);
  }

  @Override protected Observable<GetProductPurchaseAuthorizationResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getProductPurchaseAuthorization(map);
  }
}
