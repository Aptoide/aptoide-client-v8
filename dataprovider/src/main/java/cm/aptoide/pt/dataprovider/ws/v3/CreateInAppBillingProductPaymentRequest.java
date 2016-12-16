/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/11/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.InAppBillingProductPaymentResponse;
import java.util.Locale;
import rx.Observable;

/**
 * Created by marcelobenites on 16/11/16.
 */
public class CreateInAppBillingProductPaymentRequest
    extends V3<InAppBillingProductPaymentResponse> {

  private CreateInAppBillingProductPaymentRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static CreateInAppBillingProductPaymentRequest of(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String developerPayload, String accessToken) {
    return new CreateInAppBillingProductPaymentRequest(BASE_HOST,
        getBaseBody(productId, paymentId, operatorManager, developerPayload, accessToken));
  }

  public static CreateInAppBillingProductPaymentRequest of(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String developerPayload, String accessToken,
      String paymentConfirmationId) {
    final BaseBody args =
        getBaseBody(productId, paymentId, operatorManager, developerPayload, accessToken);
    args.put("paykey", paymentConfirmationId);
    return new CreateInAppBillingProductPaymentRequest(BASE_HOST, args);
  }

  private static BaseBody getBaseBody(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String developerPayload, String accessToken) {
    BaseBody body = new BaseBody();
    body.put("productid", String.valueOf(productId));
    body.put("access_token", accessToken);
    body.put("payType", String.valueOf(paymentId));
    body.put("reqType", "rest");

    if (operatorManager.isSimStateReady()) {
      body.put("simcc", operatorManager.getSimCountryISO());
    }

    body.put("developerPayload", developerPayload);
    return body;
  }

  @Override protected Observable<InAppBillingProductPaymentResponse> loadDataFromNetwork(
      Interfaces interfaces, boolean bypassCache) {
    return interfaces.createInAppBillingProductPayment(map);
  }
}
