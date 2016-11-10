package cm.aptoide.pt.aptoidesdk.proxys;

import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.model.v7.GetApp;
import rx.Observable;

/**
 * Created by neuro on 10-11-2016.
 */

public class GetAppProxy {

  public Observable<GetApp> getApp(String packageName, String storeName, String aptoideClientUUID) {
    return GetAppRequest.of(packageName, storeName, null, aptoideClientUUID).observe();
  }

  public Observable<GetApp> getApp(long appId, String aptoideClientUUID) {
    return GetAppRequest.of(appId, null, aptoideClientUUID).observe();
  }
}
