/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v3.PaidApp;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import rx.Observable;

/**
 * Created by sithengineer on 21/07/16.
 */
public class GetApkInfoRequest extends V3<PaidApp> {

  protected GetApkInfoRequest(BaseBody baseBody) {
    super(BASE_HOST, baseBody);
  }

  public static GetApkInfoRequest of(long appId, NetworkOperatorManager operatorManager,
      boolean fromSponsored, String storeName, String accessToken) {
    BaseBody args = new BaseBody();
    args.put("identif", "id:" + appId);
    args.put("repo", storeName);
    args.put("mode", "json");
    args.put("access_token", accessToken);

    if (fromSponsored) {
      args.put("adview", "1");
    }
    addOptions(args, operatorManager);
    return new GetApkInfoRequest(args);
  }

  private static void addOptions(BaseBody args, NetworkOperatorManager operatorManager) {
    BaseBody options = new BaseBody();
    options.put("cmtlimit", "5");
    options.put("payinfo", "true");
    options.put("q", Api.Q);
    options.put("lang", ManagerPreferences.getLanguage());

    addNetworkInformation(operatorManager, options);

    StringBuilder optionsBuilder = new StringBuilder();
    optionsBuilder.append("(");
    for (String optionKey : options.keySet()) {
      optionsBuilder.append(optionKey);
      optionsBuilder.append("=");
      optionsBuilder.append(options.get(optionKey));
      optionsBuilder.append(";");
    }
    optionsBuilder.append(")");
    args.put("options", optionsBuilder.toString());
  }

  @Override
  protected Observable<PaidApp> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
    return interfaces.getApkInfo(map, bypassCache);
  }
}
