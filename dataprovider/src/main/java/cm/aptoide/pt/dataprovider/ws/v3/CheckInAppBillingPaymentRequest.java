/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 17/10/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import java.util.Locale;
import rx.Observable;

/**
 * Created by marcelobenites on 17/10/16.
 */

public class CheckInAppBillingPaymentRequest extends V3<InAppBillingPurchasesResponse> {

  public CheckInAppBillingPaymentRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static CheckInAppBillingPaymentRequest of(String paymentConfirmationId, int paymentId, int productId, double price, double taxRate, String currency,
      NetworkOperatorManager operatorManager, int apiVersion, String developerPayload,
      String accessToken) {
    final BaseBody args = new BaseBody();
    addDefaultValues(paymentConfirmationId, paymentId, productId, price, taxRate, currency,
        operatorManager, args, accessToken);
    args.put("reqtype", "iabpurchasestatus");
    args.put("apiversion", String.valueOf(apiVersion));
    args.put("developerPayload", developerPayload);
    return new CheckInAppBillingPaymentRequest(BASE_HOST, args);
  }

  private static void addDefaultValues(String paymentConfirmationId, int paymentId, int productId,
      double price, double taxRate, String currency, NetworkOperatorManager operatorManager,
      BaseBody args, String accessToken) {

    args.put("mode", "json");
    args.put("payreqtype", "rest");
    args.put("paytype", String.valueOf(paymentId));
    args.put("paykey", paymentConfirmationId);
    args.put("taxrate", String.format(Locale.ROOT, "%.2f", taxRate));
    args.put("productid", String.valueOf(productId));
    args.put("price", String.format(Locale.ROOT, "%.2f", price));
    args.put("currency", currency);
    args.put("access_token", accessToken);

    if (operatorManager.isSimStateReady()) {
      args.put("simcc", operatorManager.getSimCountryISO());
    }
  }

  @Override
  protected Observable<InAppBillingPurchasesResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.checkInAppProductPayment(map);
  }
}
