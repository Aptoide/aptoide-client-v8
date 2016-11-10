package cm.aptoide.pt.aptoidesdk.proxys;

import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.model.v7.ListSearchApps;
import rx.Observable;

/**
 * Created by neuro on 03-11-2016.
 */
public class ListSearchAppsProxy {
  public Observable<ListSearchApps> search(String query, String aptoideClientUUID) {
    return ListSearchAppsRequest.of(query, null, null, null, null, aptoideClientUUID).observe();
  }

  public Observable<ListSearchApps> search(String query, String storeName,
      String aptoideClientUUID) {
    return ListSearchAppsRequest.of(query, storeName, null, null, null, aptoideClientUUID)
        .observe();
  }
}
