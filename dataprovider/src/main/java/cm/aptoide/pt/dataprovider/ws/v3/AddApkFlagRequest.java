/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v2.GenericResponseV2;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import rx.Observable;

/**
 * Created by sithengineer on 21/07/16.
 */
public class AddApkFlagRequest extends V3<GenericResponseV2> {

  private HashMapNotNull<String, String> args;

  protected AddApkFlagRequest(HashMapNotNull<String, String> args) {
    super(BASE_HOST);
    this.args = args;
  }

  public static AddApkFlagRequest of(String storeName, String appMd5sum, String flag) {
    HashMapNotNull<String, String> args = new HashMapNotNull<>();

    args.put("repo", storeName);
    args.put("md5sum", appMd5sum);
    args.put("flag", flag);
    args.put("mode", "json");
    args.put("access_token", AptoideAccountManager.getAccessToken());

    return new AddApkFlagRequest(args);
  }

  @Override protected Observable<GenericResponseV2> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.addApkFlag(args, bypassCache);
  }
}
