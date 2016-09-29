/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.BaseBody;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 8/12/16.
 */
public class InAppBillingPurchasesRequest extends V3<InAppBillingPurchasesResponse> {

  private InAppBillingPurchasesRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static InAppBillingPurchasesRequest of(int apiVersion, String packageName, String type) {
    BaseBody args = getBaseArgs(apiVersion, packageName, type);
    return new InAppBillingPurchasesRequest(BASE_HOST, args);
  }

  @NonNull private static BaseBody getBaseArgs(int apiVersion, String packageName,
      String type) {
    BaseBody args = new BaseBody();
    args.put("mode", "json");
    args.put("package", packageName);
    args.put("apiversion", String.valueOf(apiVersion));
    args.put("reqtype", "iabpurchases");
    args.put("access_token", AptoideAccountManager.getAccessToken());
    args.put("purchasetype", type);
    return args;
  }

  @Override
  protected Observable<InAppBillingPurchasesResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getInAppBillingPurchases(map);
  }
}
