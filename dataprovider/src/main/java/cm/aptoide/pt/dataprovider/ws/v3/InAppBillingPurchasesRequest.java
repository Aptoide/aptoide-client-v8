/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 12/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import rx.Observable;

/**
 * Created by marcelobenites on 8/12/16.
 */
public class InAppBillingPurchasesRequest extends V3<InAppBillingPurchasesResponse> {

  private InAppBillingPurchasesRequest(BaseBody baseBody,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(baseBody,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static InAppBillingPurchasesRequest of(int apiVersion, String packageName, String type,
      String accessToken, BodyInterceptor<BaseBody> bodyInterceptor) {
    BaseBody args = getBaseArgs(apiVersion, packageName, type, accessToken);
    return new InAppBillingPurchasesRequest(args, bodyInterceptor);
  }

  @NonNull private static BaseBody getBaseArgs(int apiVersion, String packageName, String type,
      String accessToken) {
    BaseBody args = new BaseBody();
    args.put("mode", "json");
    args.put("aptvercode", String.valueOf(AptoideUtils.Core.getVerCode()));
    args.put("package", packageName);
    args.put("apiversion", String.valueOf(apiVersion));
    args.put("reqtype", "iabpurchases");
    args.put("access_token", accessToken);
    args.put("purchasetype", type);
    return args;
  }

  @Override
  protected Observable<InAppBillingPurchasesResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getInAppBillingPurchases(map);
  }
}
