/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.ProductPaymentResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 7/28/16.
 *
 * @author SithEngineer marcelobenites
 */
public class CheckPaidAppProductPaymentRequest extends V3<ProductPaymentResponse> {

  private CheckPaidAppProductPaymentRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static CheckPaidAppProductPaymentRequest of(int productId, NetworkOperatorManager operatorManager,
      String accessToken) {
    final BaseBody args = new BaseBody();
    args.put("mode", "json");
    args.put("payreqtype", "rest");
    args.put("productid", String.valueOf(productId));
    args.put("access_token", accessToken);

    if (operatorManager.isSimStateReady()) {
      args.put("simcc", operatorManager.getSimCountryISO());
    }
    args.put("reqtype", "apkpurchasestatus");
    return new CheckPaidAppProductPaymentRequest(BASE_HOST, args);
  }

  @Override protected Observable<ProductPaymentResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.checkPaidAppProductPayment(map);
  }
}
