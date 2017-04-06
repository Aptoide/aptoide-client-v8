/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 8/11/16.
 */
public class InAppBillingSkuDetailsRequest extends V3<InAppBillingSkuDetailsResponse> {

  public InAppBillingSkuDetailsRequest(BaseBody baseBody,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(baseBody,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static InAppBillingSkuDetailsRequest of(int apiVersion, String packageName,
      List<String> skuList, NetworkOperatorManager operatorManager, String type, String accessToken,
      BodyInterceptor<BaseBody> bodyInterceptor) {
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

    addNetworkInformation(operatorManager, args);

    return new InAppBillingSkuDetailsRequest(args, bodyInterceptor);
  }

  @Override
  protected Observable<InAppBillingSkuDetailsResponse> loadDataFromNetwork(V3.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getInAppBillingSkuDetails(map);
  }
}
