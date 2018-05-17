package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.view.app.DetailedApp;
import cm.aptoide.pt.view.app.DetailedAppRequestResult;

/**
 * Created by D01 on 07/05/18.
 */

public class DetailedAppViewModel {

  private final GetAppMeta.Media media;
  private final String modified;
  private final String appAdded;
  private final Obb obb;
  private final GetAppMeta.Pay pay;
  private final boolean isStoreFollowed;
  private final DetailedApp detailedApp;
  private final long appId;
  private final String appName;
  private final Store store;
  private final GetAppMeta.GetAppMetaFile file;
  private final String packageName;
  private final long size;
  private final int downloads;
  private final GetAppMeta.Stats.Rating globalRating;
  private final int pDownloads;
  private final GetAppMeta.Stats.Rating rating;
  private final GetAppMeta.Developer developer;
  private final String graphic;
  private final String added;
  private final boolean loading;
  private final DetailedAppRequestResult.Error error;

  public DetailedAppViewModel(DetailedApp detailedApp, long appId, String appName, Store store,
      GetAppMeta.GetAppMetaFile file, String packageName, long size, int downloads,
      GetAppMeta.Stats.Rating globalRating, int pDownloads, GetAppMeta.Stats.Rating rating,
      GetAppMeta.Developer developer, String graphic, String added, GetAppMeta.Media media,
      String modified, String appAdded, Obb obb, GetAppMeta.Pay pay, Boolean isStoreFollowed) {
    this.detailedApp = detailedApp;
    this.appId = appId;
    this.appName = appName;
    this.store = store;
    this.file = file;
    this.packageName = packageName;
    this.size = size;
    this.downloads = downloads;
    this.globalRating = globalRating;
    this.pDownloads = pDownloads;
    this.rating = rating;
    this.developer = developer;
    this.graphic = graphic;
    this.added = added;
    this.media = media;
    this.modified = modified;
    this.appAdded = appAdded;
    this.obb = obb;
    this.pay = pay;
    this.isStoreFollowed = isStoreFollowed;
    this.loading = false;
    this.error = null;
  }

  public DetailedAppViewModel(boolean loading) {
    this.loading = loading;
    this.detailedApp = null;
    this.appId = -1;
    this.appName = "";
    this.store = null;
    this.file = null;
    this.packageName = "";
    this.size = -1;
    this.downloads = -1;
    this.globalRating = null;
    this.pDownloads = -1;
    this.rating = null;
    this.developer = null;
    this.graphic = null;
    this.added = null;
    this.media = null;
    this.modified = null;
    this.appAdded = null;
    this.obb = null;
    this.pay = null;
    this.isStoreFollowed = false;
    this.error = null;
  }

  public DetailedAppViewModel(DetailedAppRequestResult.Error error) {
    this.error = error;
    this.detailedApp = null;
    this.appId = -1;
    this.appName = "";
    this.store = null;
    this.file = null;
    this.packageName = "";
    this.size = -1;
    this.downloads = -1;
    this.globalRating = null;
    this.pDownloads = -1;
    this.rating = null;
    this.developer = null;
    this.graphic = null;
    this.added = null;
    this.media = null;
    this.modified = null;
    this.appAdded = null;
    this.obb = null;
    this.pay = null;
    this.isStoreFollowed = false;
    this.loading = false;
  }

  public boolean isStoreFollowed() {
    return isStoreFollowed;
  }

  public long getAppId() {
    return appId;
  }

  public String getAppName() {
    return appName;
  }

  public Store getStore() {
    return store;
  }

  public String getPackageName() {
    return packageName;
  }

  public GetAppMeta.GetAppMetaFile getFile() {
    return file;
  }

  public long getSize() {
    return size;
  }

  public GetAppMeta.Developer getDeveloper() {
    return developer;
  }

  public String getGraphic() {
    return graphic;
  }

  public String getAdded() {
    return added;
  }

  public GetAppMeta.Media getMedia() {
    return media;
  }

  public String getModified() {
    return modified;
  }

  public String getAppAdded() {
    return appAdded;
  }

  public Obb getObb() {
    return obb;
  }

  public GetAppMeta.Pay getPay() {
    return pay;
  }

  public DetailedApp getDetailedApp() {
    return detailedApp;
  }

  public int getDownloads() {
    return downloads;
  }

  public GetAppMeta.Stats.Rating getGlobalRating() {
    return globalRating;
  }

  public int getpDownloads() {
    return pDownloads;
  }

  public GetAppMeta.Stats.Rating getRating() {
    return rating;
  }

  public boolean isLoading() {
    return loading;
  }

  public DetailedAppRequestResult.Error getError() {
    return error;
  }

  public boolean hasError() {
    return (error != null);
  }
}
