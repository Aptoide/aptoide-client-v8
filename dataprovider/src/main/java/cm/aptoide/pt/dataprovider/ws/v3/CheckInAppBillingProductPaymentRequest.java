/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 17/10/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 17/10/16.
 */

public class CheckInAppBillingProductPaymentRequest extends V3<InAppBillingPurchasesResponse> {

  public CheckInAppBillingProductPaymentRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static CheckInAppBillingProductPaymentRequest of(int productId, NetworkOperatorManager operatorManager,
      int apiVersion, String accessToken) {
    final BaseBody args = new BaseBody();

    args.put("mode", "json");
    args.put("payreqtype", "rest");
    args.put("productid", String.valueOf(productId));
    args.put("access_token", accessToken);

    if (operatorManager.isSimStateReady()) {
      args.put("simcc", operatorManager.getSimCountryISO());
    }

    args.put("reqtype", "iabpurchasestatus");
    args.put("apiversion", String.valueOf(apiVersion));
    return new CheckInAppBillingProductPaymentRequest(BASE_HOST, args);
  }

  @Override
  protected Observable<InAppBillingPurchasesResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.checkInAppProductPayment(map);
  }
}
