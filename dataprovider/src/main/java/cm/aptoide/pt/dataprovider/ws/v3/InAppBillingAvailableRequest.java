/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.accountmanager.ws.BaseBody;
import cm.aptoide.pt.model.v3.InAppBillingAvailableResponse;
import rx.Observable;

/**
 * Created by marcelobenites on 8/11/16.
 */
public class InAppBillingAvailableRequest extends V3<InAppBillingAvailableResponse> {

  public InAppBillingAvailableRequest(String baseHost, BaseBody baseBody) {
    super(baseHost, baseBody);
  }

  public static InAppBillingAvailableRequest of(int apiVersion, String packageName, String type) {
    final BaseBody args = new BaseBody();
    args.put("mode", "json");
    args.put("apiversion", String.valueOf(apiVersion));
    args.put("reqtype", "iabavailable");
    args.put("package", packageName);
    args.put("purchasetype", type);
    return new InAppBillingAvailableRequest(BASE_HOST, args);
  }

  @Override
  protected Observable<InAppBillingAvailableResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getInAppBillingAvailable(map);
  }
}
