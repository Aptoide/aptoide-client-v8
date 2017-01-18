/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/11/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.BaseV3Response;
import rx.Observable;

/**
 * Created by marcelobenites on 16/11/16.
 */
public class CreatePaymentConfirmationRequest extends V3<BaseV3Response> {

  private CreatePaymentConfirmationRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static CreatePaymentConfirmationRequest ofInApp(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String developerPayload, String accessToken) {
    final BaseBody args = getBaseBody(productId, paymentId, operatorManager, accessToken);
    args.put("developerPayload", developerPayload);
    return new CreatePaymentConfirmationRequest(BASE_HOST, args);
  }

  public static CreatePaymentConfirmationRequest ofInApp(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String developerPayload, String accessToken,
      String paymentConfirmationId) {
    final BaseBody args = getBaseBody(productId, paymentId, operatorManager, accessToken);
    args.put("paykey", paymentConfirmationId);
    args.put("developerPayload", developerPayload);
    return new CreatePaymentConfirmationRequest(BASE_HOST, args);
  }

  public static CreatePaymentConfirmationRequest ofPaidApp(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String store, String accessToken) {
    final BaseBody args = getBaseBody(productId, paymentId, operatorManager, accessToken);
    args.put("repo", store);
    return new CreatePaymentConfirmationRequest(BASE_HOST, args);
  }

  public static CreatePaymentConfirmationRequest ofPaidApp(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String store, String accessToken, String paymentConfirmationId) {
    final BaseBody args = getBaseBody(productId, paymentId, operatorManager, accessToken);
    args.put("paykey", paymentConfirmationId);
    args.put("repo", store);
    return new CreatePaymentConfirmationRequest(BASE_HOST, args);
  }

  private static BaseBody getBaseBody(int productId, int paymentId,
      NetworkOperatorManager operatorManager, String accessToken) {
    BaseBody body = new BaseBody();
    body.put("productid", String.valueOf(productId));
    body.put("access_token", accessToken);
    body.put("payType", String.valueOf(paymentId));
    body.put("reqType", "rest");

    addNetworkInformation(operatorManager, body);

    return body;
  }

  @Override protected Observable<BaseV3Response> loadDataFromNetwork(
      Interfaces interfaces, boolean bypassCache) {
    return interfaces.createPaymentConfirmation(map);
  }
}
