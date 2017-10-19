package cm.aptoide.pt.search.model;

import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import org.parceler.Parcel;

@Parcel public class SearchAppResult {
  int rank;
  String icon;
  String storeName;
  String storeTheme;
  long modifiedDate;
  float averageRating;
  long totalDownloads;
  String appName;
  String packageName;
  long appId;
  boolean hasOtherVersions;

  public SearchAppResult() {
  }

  public SearchAppResult(int rank, String icon, String storeName, String storeTheme,
      long modifiedDate, float averageRating, long totalDownloads, String appName,
      String packageName, long appId, boolean hasOtherVersions) {
    this.rank = rank;
    this.icon = icon;
    this.storeName = storeName;
    this.storeTheme = storeTheme;
    this.modifiedDate = modifiedDate;
    this.averageRating = averageRating;
    this.totalDownloads = totalDownloads;
    this.appName = appName;
    this.packageName = packageName;
    this.appId = appId;
    this.hasOtherVersions = hasOtherVersions;
  }

  public SearchAppResult(SearchApp searchApp) {
    this(searchApp.getFile()
            .getMalware()
            .getRank()
            .ordinal(), searchApp.getIcon(), searchApp.getStore()
            .getName(), searchApp.getStore()
            .getAppearance()
            .getTheme(), searchApp.getModified()
            .getTime(), searchApp.getStats()
            .getRating()
            .getAvg(), searchApp.getStats()
            .getPdownloads(), searchApp.getName(), searchApp.getPackageName(), searchApp.getId(),
        searchApp.hasVersions());
  }

  public int getRank() {
    return rank;
  }

  public String getIcon() {
    return icon;
  }

  public String getStoreName() {
    return storeName;
  }

  public String getStoreTheme() {
    return storeTheme;
  }

  public long getModifiedDate() {
    return modifiedDate;
  }

  public float getAverageRating() {
    return averageRating;
  }

  public long getTotalDownloads() {
    return totalDownloads;
  }

  public String getAppName() {
    return appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getAppId() {
    return appId;
  }

  public boolean hasOtherVersions() {
    return hasOtherVersions;
  }
}
