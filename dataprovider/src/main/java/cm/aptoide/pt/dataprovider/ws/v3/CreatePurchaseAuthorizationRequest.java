/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 05/12/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.model.v3.PurchaseAuthorizationResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 05/12/16.
 */

public class CreatePurchaseAuthorizationRequest extends V3<PurchaseAuthorizationResponse> {

  private CreatePurchaseAuthorizationRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static CreatePurchaseAuthorizationRequest of(String accessToken, int paymentId) {
    BaseBody args = new BaseBody();
    args.put("access_token", accessToken);
    args.put("payType", String.valueOf(paymentId));
    return new CreatePurchaseAuthorizationRequest(BASE_HOST, args);
  }

  @Override protected Observable<PurchaseAuthorizationResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.createPurchaseAuthorization(map);
  }
}
