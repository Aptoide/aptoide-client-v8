/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.PaymentResponse;
import java.util.Locale;
import rx.Observable;

/**
 * Created by marcelobenites on 7/28/16.
 *
 * @author SithEngineer marcelobenites
 */
public class CheckProductPaymentRequest extends V3<PaymentResponse> {

  private CheckProductPaymentRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static CheckProductPaymentRequest ofInAppBilling(String paymentConfirmationId,
      int paymentId, int productId, double price, double taxRate, String currency,
      NetworkOperatorManager operatorManager, int apiVersion, String developerPayload,
      String accessToken) {
    final BaseBody args = new BaseBody();
    addDefaultValues(paymentConfirmationId, paymentId, productId, price, taxRate, currency,
        operatorManager, args, accessToken);
    args.put("reqtype", "iabpurchasestatus");
    args.put("apiversion", String.valueOf(apiVersion));
    args.put("developerPayload", developerPayload);
    return new CheckProductPaymentRequest(BASE_HOST, args);
  }

  public static CheckProductPaymentRequest ofPaidApp(String paymentConfirmationId, int paymentId,
      int productId, double price, double taxRate, String currency,
      NetworkOperatorManager operatorManager, String storeName, String accessToken) {
    final BaseBody args = new BaseBody();
    addDefaultValues(paymentConfirmationId, paymentId, productId, price, taxRate, currency,
        operatorManager, args, accessToken);
    args.put("reqtype", "apkpurchasestatus");
    args.put("repo", storeName);
    return new CheckProductPaymentRequest(BASE_HOST, args);
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

  @Override protected Observable<PaymentResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.checkProductPayment(map);
  }
}
