/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/11/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.InAppBillingProductPaymentResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 16/11/16.
 */
public class CreateInAppBillingProductPaymentRequest extends V3<InAppBillingProductPaymentResponse> {

  private CreateInAppBillingProductPaymentRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static CreateInAppBillingProductPaymentRequest of(int productId, int paymentId,
      NetworkOperatorManager operatorManager, int apiVersion, String developerPayload) {
    BaseBody body = new BaseBody();
    body.put("productid", String.valueOf(productId));
    body.put("payType", String.valueOf(paymentId));
    body.put("reqType", "rest");

    if (operatorManager.isSimStateReady()) {
      body.put("simcc", operatorManager.getSimCountryISO());
    }

    body.put("apiversion", String.valueOf(apiVersion));
    body.put("developerPayload", developerPayload);

    return new CreateInAppBillingProductPaymentRequest(BASE_HOST, body);
  }

  @Override protected Observable<InAppBillingProductPaymentResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return null;
  }
}
