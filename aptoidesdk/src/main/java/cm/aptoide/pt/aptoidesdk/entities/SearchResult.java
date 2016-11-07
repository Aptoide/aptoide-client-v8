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

  public SearchResult(long id, String name, String packageName, long size, String iconPath,
      String storeName, long downloads) {
    this.id = id;
    this.name = name;
    this.packageName = packageName;
    this.size = size;
    this.iconPath = iconPath;
    this.storeName = storeName;
    this.downloads = downloads;
  }

  public static SearchResult fromSearchAppsApp(ListSearchApps.SearchAppsApp searchAppsApp) {

    long id = searchAppsApp.getId();
    String name = searchAppsApp.getName();
    String packageName = searchAppsApp.getPackageName();
    long size = searchAppsApp.getSize();
    String iconPath = searchAppsApp.getIcon();
    String storeName = searchAppsApp.getStore().getName();
    long downloads = searchAppsApp.getStats().getDownloads();

    return new SearchResult(id, name, packageName, size, iconPath, storeName, downloads);
  }
}