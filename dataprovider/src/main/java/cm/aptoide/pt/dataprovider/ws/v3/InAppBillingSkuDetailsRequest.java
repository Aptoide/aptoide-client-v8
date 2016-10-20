/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 8/11/16.
 */
public class InAppBillingSkuDetailsRequest extends V3<InAppBillingSkuDetailsResponse> {

  public InAppBillingSkuDetailsRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static InAppBillingSkuDetailsRequest of(int apiVersion, String packageName,
      List<String> skuList, NetworkOperatorManager operatorManager, String type, String accessToken,
      String email) {
    BaseBody args = new BaseBody();
    args.put("mode", "json");
    args.put("package", packageName);
    args.put("apiversion", String.valueOf(apiVersion));
    args.put("reqtype", "iabskudetails");
    args.put("purchasetype", type);
    args.put("access_token", accessToken);

    if (!skuList.isEmpty()) {
      final StringBuilder stringBuilder = new StringBuilder();
      for (String sku : skuList) {
        stringBuilder.append(sku);
        stringBuilder.append(",");
      }
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
      args.put("skulist", stringBuilder.toString());
    }

    if (operatorManager.isSimStateReady()) {
      args.put("mcc", operatorManager.getMobileCountryCode());
      args.put("mnc", operatorManager.getMobileNetworkCode());
      args.put("simcc", operatorManager.getSimCountryISO());
    }

    return new InAppBillingSkuDetailsRequest(BASE_HOST, args);
  }

  @Override
  protected Observable<InAppBillingSkuDetailsResponse> loadDataFromNetwork(V3.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getInAppBillingSkuDetails(map);
  }
}
