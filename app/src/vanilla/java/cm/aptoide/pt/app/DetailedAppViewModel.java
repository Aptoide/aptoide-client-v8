package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.view.app.AppDeveloper;
import cm.aptoide.pt.view.app.AppFlags;
import cm.aptoide.pt.view.app.DetailedApp;
import cm.aptoide.pt.view.app.DetailedAppRequestResult;
import java.util.List;

/**
 * Created by D01 on 07/05/18.
 */

public class DetailedAppViewModel {

  private final GetAppMeta.Media media;
  private final String modified;
  private final String appAdded;
  private final Obb obb;
  private final GetAppMeta.Pay pay;
  private final String wUrls;
  private final boolean isPaid;
  private final String uName;
  private final boolean isStoreFollowed;
  private final DetailedApp detailedApp;
  private final long appId;
  private final String appName;
  private final Store store;
  private final boolean isGoodApp;
  private final Malware malware;
  private final AppFlags appFlags;
  private final List<String> tags;
  private final List<String> usedFeatures;
  private final List<String> usedPermissions;
  private final long fileSize;
  private final String md5;
  private final String md5Sum;
  private final String path;
  private final String pathAlt;
  private final int verCode;
  private final String verName;
  private final String packageName;
  private final long size;
  private final int downloads;
  private final GetAppMeta.Stats.Rating globalRating;
  private final int pDownloads;
  private final GetAppMeta.Stats.Rating rating;
  private final AppDeveloper appDeveloper;
  private final String graphic;
  private final String added;
  private final boolean loading;
  private final DetailedAppRequestResult.Error error;

  public DetailedAppViewModel(DetailedApp detailedApp, long appId, String appName, Store store,
      boolean isGoodApp, Malware malware, AppFlags appFlags, List<String> tags,
      List<String> usedFeatures, List<String> usedPermissions, long fileSize, String md5,
      String md5Sum, String path, String pathAlt, int verCode, String verName, String packageName,
      long size, int downloads, GetAppMeta.Stats.Rating globalRating, int pDownloads,
      GetAppMeta.Stats.Rating rating, AppDeveloper appDeveloper, String graphic, String added,
      GetAppMeta.Media media, String modified, String appAdded, Obb obb, GetAppMeta.Pay pay,
      String wUrls, boolean isPaid, String uName, boolean isStoreFollowed) {
    this.detailedApp = detailedApp;
    this.appId = appId;
    this.appName = appName;
    this.store = store;
    this.isGoodApp = isGoodApp;
    this.malware = malware;
    this.appFlags = appFlags;
    this.tags = tags;
    this.usedFeatures = usedFeatures;
    this.usedPermissions = usedPermissions;
    this.fileSize = fileSize;
    this.md5 = md5;
    this.md5Sum = md5Sum;
    this.path = path;
    this.pathAlt = pathAlt;
    this.verCode = verCode;
    this.verName = verName;
    this.packageName = packageName;
    this.size = size;
    this.downloads = downloads;
    this.globalRating = globalRating;
    this.pDownloads = pDownloads;
    this.rating = rating;
    this.appDeveloper = appDeveloper;
    this.graphic = graphic;
    this.added = added;
    this.media = media;
    this.modified = modified;
    this.appAdded = appAdded;
    this.obb = obb;
    this.pay = pay;
    this.wUrls = wUrls;
    this.isPaid = isPaid;
    this.uName = uName;
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
    this.isGoodApp = false;
    this.malware = null;
    this.appFlags = null;
    this.tags = null;
    this.usedFeatures = null;
    this.usedPermissions = null;
    this.fileSize = -1;
    this.md5 = "";
    this.md5Sum = "";
    this.path = "";
    this.pathAlt = "";
    this.verCode = -1;
    this.verName = "";
    this.packageName = "";
    this.size = -1;
    this.downloads = -1;
    this.globalRating = null;
    this.pDownloads = -1;
    this.rating = null;
    this.appDeveloper = null;
    this.graphic = null;
    this.added = null;
    this.media = null;
    this.modified = null;
    this.appAdded = null;
    this.obb = null;
    this.pay = null;
    this.wUrls = null;
    this.isPaid = false;
    this.uName = "";
    this.isStoreFollowed = false;
    this.error = null;
  }

  public DetailedAppViewModel(DetailedAppRequestResult.Error error) {
    this.error = error;
    this.detailedApp = null;
    this.appId = -1;
    this.appName = "";
    this.store = null;
    this.isGoodApp = false;
    this.malware = null;
    this.appFlags = null;
    this.tags = null;
    this.usedFeatures = null;
    this.usedPermissions = null;
    this.fileSize = -1;
    this.md5 = "";
    this.md5Sum = "";
    this.path = "";
    this.pathAlt = "";
    this.verCode = -1;
    this.verName = "";
    this.packageName = "";
    this.size = -1;
    this.downloads = -1;
    this.globalRating = null;
    this.pDownloads = -1;
    this.rating = null;
    this.appDeveloper = null;
    this.graphic = null;
    this.added = null;
    this.media = null;
    this.modified = null;
    this.appAdded = null;
    this.obb = null;
    this.pay = null;
    this.wUrls = null;
    this.isPaid = false;
    this.uName = "";
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

  public long getSize() {
    return size;
  }

  public AppDeveloper getDeveloper() {
    return appDeveloper;
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

  public String getwUrls() {
    return wUrls;
  }

  public AppFlags getAppFlags() {
    return appFlags;
  }

  public List<String> getTags() {
    return tags;
  }

  public List<String> getUsedFeatures() {
    return usedFeatures;
  }

  public List<String> getUsedPermissions() {
    return usedPermissions;
  }

  public long getFileSize() {
    return fileSize;
  }

  public String getMd5() {
    return md5;
  }

  public String getMd5Sum() {
    return md5Sum;
  }

  public String getPath() {
    return path;
  }

  public String getPathAlt() {
    return pathAlt;
  }

  public int getVerCode() {
    return verCode;
  }

  public String getVerName() {
    return verName;
  }

  public boolean isGoodApp() {
    return isGoodApp;
  }

  public Malware getMalware() {
    return malware;
  }

  public boolean isPaid() {
    return isPaid;
  }

  public String getuName() {
    return uName;
  }
}
