package cm.aptoide.pt.aptoidesdk.entities;

import cm.aptoide.pt.model.v7.ListSearchApps;
import lombok.Data;

/**
 * Created by neuro on 21-10-2016.
 */
@Data public class SearchResult {

  private long id;
  private String name;
  private String packageName;
  private long size;
  private String iconPath;
  private String storeName;
  private long downloads;

  public static SearchResult fromSearchAppsApp(ListSearchApps.SearchAppsApp searchAppsApp) {
    // TODO: 03-11-2016 neuro
    return new SearchResult();
  }
}