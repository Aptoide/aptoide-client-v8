package cm.aptoide.pt.aptoidesdk.proxys;

import cm.aptoide.pt.aptoidesdk.entities.SearchResult;
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import java.util.List;

/**
 * Created by neuro on 03-11-2016.
 */
public class ListSearchAppsProxy {
  public List<SearchResult> search(String query, String aptoideClientUUID) {
    ListSearchAppsRequest.of(query, null, null, null, null, aptoideClientUUID);
    // TODO: 04-11-2016 neuro 
    return null;
  }

  public List<SearchResult> search(String query, String storeName, String aptoideClientUUID) {
    // TODO: 04-11-2016 neuro 
    return null;
  }
}
