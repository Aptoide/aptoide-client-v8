/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/11/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.ProductPaymentResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 16/11/16.
 */

public class CreatePaidAppProductPaymentRequest extends V3<ProductPaymentResponse> {

  private CreatePaidAppProductPaymentRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static CreatePaidAppProductPaymentRequest of(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String store, String accessToken) {
    BaseBody body = new BaseBody();
    body.put("productid", String.valueOf(productId));
    body.put("access_token", accessToken);
    body.put("payType", String.valueOf(paymentId));
    body.put("reqType", "rest");
    body.put("repo", store);

    if (operatorManager.isSimStateReady()) {
      body.put("simcc", operatorManager.getSimCountryISO());
    }

    return new CreatePaidAppProductPaymentRequest(BASE_HOST, body);
  }

  @Override protected Observable<ProductPaymentResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.createPaidAppProductPayment(map);
  }
}
