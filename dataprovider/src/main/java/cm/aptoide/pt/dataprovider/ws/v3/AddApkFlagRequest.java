/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v2.GenericResponseV2;
import rx.Observable;

/**
 * Created by sithengineer on 21/07/16.
 */
public class AddApkFlagRequest extends V3<GenericResponseV2> {

  protected AddApkFlagRequest(BaseBody baseBody) {
    super(BASE_HOST, baseBody);
  }

  public static AddApkFlagRequest of(String storeName, String appMd5sum, String flag) {
    BaseBody args = new BaseBody();

    args.put("repo", storeName);
    args.put("md5sum", appMd5sum);
    args.put("flag", flag);
    args.put("mode", "json");
    args.put("access_token", AptoideAccountManager.getAccessToken());

    return new AddApkFlagRequest(args);
  }

  @Override protected Observable<GenericResponseV2> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.addApkFlag(map, bypassCache);
  }
}
