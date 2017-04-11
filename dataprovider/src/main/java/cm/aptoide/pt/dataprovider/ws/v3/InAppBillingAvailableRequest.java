/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.model.v3.InAppBillingAvailableResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import rx.Observable;

/**
 * Created by marcelobenites on 8/11/16.
 */
public class InAppBillingAvailableRequest extends V3<InAppBillingAvailableResponse> {

  public InAppBillingAvailableRequest(BaseBody baseBody,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    super(baseBody,
        OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter(), bodyInterceptor);
  }

  public static InAppBillingAvailableRequest of(int apiVersion, String packageName, String type,
      BodyInterceptor<BaseBody> bodyInterceptor) {
    final BaseBody args = new BaseBody();
    args.put("mode", "json");
    args.put("apiversion", String.valueOf(apiVersion));
    args.put("reqtype", "iabavailable");
    args.put("package", packageName);
    args.put("purchasetype", type);
    return new InAppBillingAvailableRequest(args, bodyInterceptor);
  }

  @Override
  protected Observable<InAppBillingAvailableResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getInAppBillingAvailable(map);
  }
}
