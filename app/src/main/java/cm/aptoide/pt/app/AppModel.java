package cm.aptoide.pt.app;

import cm.aptoide.pt.aab.Split;
import cm.aptoide.pt.app.view.AppViewFragment.OpenType;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.view.app.AppDeveloper;
import cm.aptoide.pt.view.app.AppFlags;
import cm.aptoide.pt.view.app.AppMedia;
import cm.aptoide.pt.view.app.AppRating;
import cm.aptoide.pt.view.app.DetailedAppRequestResult;
import java.util.List;

/**
 * Created by D01 on 07/05/18.
 */

public class AppModel {
  private static final String BDS_STORE_FLAG = "STORE_BDS";

  private final AppMedia media;
  private final String modified;
  private final String appAdded;
  private final Obb obb;
  private final String webUrls;
  private final boolean isLatestTrustedVersion;
  private final String uniqueName;
  private final OpenType openType;
  private final double appc;
  private final SearchAdResult minimalAd;
  private final String editorsChoice;
  private final String originTag;
  private final boolean isStoreFollowed;
  private final long appId;
  private final String appName;
  private final Store store;
  private final String storeTheme;
  private final boolean isGoodApp;
  private final Malware malware;
  private final AppFlags appFlags;
  private final List<String> tags;
  private final List<String> usedFeatures;
  private final List<String> usedPermissions;
  private final long fileSize;
  private final String md5;
  private final String pathAlt;
  private final int versionCode;
  private final String versionName;
  private final String packageName;
  private final long size;
  private final int downloads;
  private final AppRating globalRating;
  private final int packageDownloads;
  private final AppRating rating;
  private final AppDeveloper appDeveloper;
  private final String graphic;
  private final String icon;
  private final boolean loading;
  private final DetailedAppRequestResult.Error error;
  private final String marketName;
  private final boolean isMature;
  private final List<Split> splits;
  private final List<String> requiredSplits;
  private final boolean isBeta;
  private String path;
  private final boolean hasBilling;
  private final boolean hasAdvertising;
  private final List<String> bdsFlags;
  private final String campaignUrl;
  private final String signature;
  private final String oemId;
  private boolean isEskills;
  private String appCategory;

  public AppModel(long appId, String appName, Store store, String storeTheme, boolean isGoodApp,
      Malware malware, AppFlags appFlags, List<String> tags, List<String> usedFeatures,
      List<String> usedPermissions, long fileSize, String md5, String path, String pathAlt,
      int versionCode, String versionName, String packageName, long size, int downloads,
      AppRating globalRating, int packageDownloads, AppRating rating, AppDeveloper appDeveloper,
      String graphic, String icon, AppMedia media, String modified, String appAdded, Obb obb,
      String webUrls, boolean isLatestTrustedVersion, String uniqueName, OpenType openType,
      double appc, SearchAdResult minimalAd, String editorsChoice, String originTag,
      boolean isStoreFollowed, String marketName, boolean hasBilling, boolean hasAdvertising,
      List<String> bdsFlags, String campaignUrl, String signature, boolean isMature,
      List<Split> splits, List<String> requiredSplits, String oemId, boolean isBeta,
      boolean isEskills, String appCategory) {
    this.appId = appId;
    this.appName = appName;
    this.store = store;
    this.storeTheme = storeTheme;
    this.isGoodApp = isGoodApp;
    this.malware = malware;
    this.appFlags = appFlags;
    this.tags = tags;
    this.usedFeatures = usedFeatures;
    this.usedPermissions = usedPermissions;
    this.fileSize = fileSize;
    this.md5 = md5;
    this.path = path;
    this.pathAlt = pathAlt;
    this.versionCode = versionCode;
    this.versionName = versionName;
    this.packageName = packageName;
    this.size = size;
    this.downloads = downloads;
    this.globalRating = globalRating;
    this.packageDownloads = packageDownloads;
    this.rating = rating;
    this.appDeveloper = appDeveloper;
    this.graphic = graphic;
    this.icon = icon;
    this.media = media;
    this.modified = modified;
    this.appAdded = appAdded;
    this.obb = obb;
    this.webUrls = webUrls;
    this.isLatestTrustedVersion = isLatestTrustedVersion;
    this.uniqueName = uniqueName;
    this.openType = openType;
    this.appc = appc;
    this.minimalAd = minimalAd;
    this.editorsChoice = editorsChoice;
    this.originTag = originTag;
    this.isStoreFollowed = isStoreFollowed;
    this.marketName = marketName;
    this.hasBilling = hasBilling;
    this.hasAdvertising = hasAdvertising;
    this.bdsFlags = bdsFlags;
    this.campaignUrl = campaignUrl;
    this.signature = signature;
    this.isMature = isMature;
    this.splits = splits;
    this.requiredSplits = requiredSplits;
    this.isEskills = isEskills;
    this.loading = false;
    this.error = null;
    this.oemId = oemId;
    this.isBeta = isBeta;
    this.appCategory = appCategory;
  }

  public AppModel(boolean loading) {
    this.loading = loading;
    this.appId = -1;
    this.appName = "";
    this.store = null;
    this.storeTheme = "";
    this.isGoodApp = false;
    this.malware = null;
    this.appFlags = null;
    this.tags = null;
    this.usedFeatures = null;
    this.usedPermissions = null;
    this.fileSize = -1;
    this.md5 = "";
    this.path = "";
    this.pathAlt = "";
    this.versionCode = -1;
    this.versionName = "";
    this.packageName = "";
    this.size = -1;
    this.downloads = -1;
    this.globalRating = null;
    this.packageDownloads = -1;
    this.rating = null;
    this.appDeveloper = null;
    this.graphic = null;
    this.icon = null;
    this.media = null;
    this.modified = null;
    this.appAdded = null;
    this.obb = null;
    this.webUrls = null;
    this.isLatestTrustedVersion = false;
    this.uniqueName = "";
    this.openType = null;
    this.appc = -1;
    this.minimalAd = null;
    this.editorsChoice = "";
    this.originTag = "";
    this.marketName = "";
    this.isStoreFollowed = false;
    this.signature = "";
    this.error = null;
    this.hasBilling = false;
    this.hasAdvertising = false;
    this.isMature = false;
    this.bdsFlags = null;
    this.campaignUrl = "";
    this.splits = null;
    this.requiredSplits = null;
    this.oemId = null;
    this.isBeta = false;
    this.appCategory = "";
  }

  public AppModel(DetailedAppRequestResult.Error error) {
    this.error = error;
    this.appId = -1;
    this.appName = "";
    this.store = new Store();
    store.setId(-1);
    this.storeTheme = "";
    this.isGoodApp = false;
    this.malware = null;
    this.appFlags = null;
    this.tags = null;
    this.usedFeatures = null;
    this.usedPermissions = null;
    this.fileSize = -1;
    this.md5 = "";
    this.path = "";
    this.pathAlt = "";
    this.versionCode = -1;
    this.versionName = "";
    this.packageName = "";
    this.size = -1;
    this.downloads = -1;
    this.globalRating = null;
    this.packageDownloads = -1;
    this.rating = null;
    this.appDeveloper = null;
    this.graphic = null;
    this.icon = null;
    this.media = null;
    this.modified = null;
    this.appAdded = null;
    this.obb = null;
    this.webUrls = null;
    this.isLatestTrustedVersion = false;
    this.uniqueName = "";
    this.openType = null;
    this.appc = -1;
    this.minimalAd = null;
    this.editorsChoice = "";
    this.originTag = "";
    this.marketName = "";
    this.isStoreFollowed = false;
    this.signature = "";
    this.loading = false;
    this.hasBilling = false;
    this.hasAdvertising = false;
    this.isMature = false;
    this.bdsFlags = null;
    this.campaignUrl = "";
    this.splits = null;
    this.requiredSplits = null;
    this.oemId = null;
    this.isBeta = false;
    this.appCategory = "";
  }

  public boolean isMature() {
    return isMature;
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

  public String getIcon() {
    return icon;
  }

  public AppMedia getMedia() {
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

  public int getDownloads() {
    return downloads;
  }

  public AppRating getGlobalRating() {
    return globalRating;
  }

  public int getPackageDownloads() {
    return packageDownloads;
  }

  public AppRating getRating() {
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

  public String getWebUrls() {
    return webUrls;
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

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPathAlt() {
    return pathAlt;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public String getVersionName() {
    return versionName;
  }

  public boolean isGoodApp() {
    return isGoodApp;
  }

  public Malware getMalware() {
    return malware;
  }

  public String getUniqueName() {
    return uniqueName;
  }

  public OpenType getOpenType() {
    return openType;
  }

  public double getAppc() {
    return appc;
  }

  public SearchAdResult getMinimalAd() {
    return minimalAd;
  }

  public String getStoreTheme() {
    return storeTheme;
  }

  public String getEditorsChoice() {
    return editorsChoice;
  }

  public boolean isFromEditorsChoice() {
    return !editorsChoice.isEmpty();
  }

  public String getOriginTag() {
    return originTag;
  }

  public boolean isLatestTrustedVersion() {
    return isLatestTrustedVersion;
  }

  public String getMarketName() {
    return marketName;
  }

  public boolean hasBilling() {
    return hasBilling;
  }

  public boolean isAppCoinApp() {
    return hasBilling || hasAdvertising;
  }

  public boolean hasAdvertising() {
    return this.hasAdvertising;
  }

  public String getCampaignUrl() {
    return campaignUrl;
  }

  public String getSignature() {
    return signature;
  }

  public List<Split> getSplits() {
    return splits;
  }

  public boolean hasSplits() {
    return splits != null && !splits.isEmpty();
  }

  public List<String> getRequiredSplits() {
    return requiredSplits;
  }

  public String getOemId() {
    return oemId;
  }

  public boolean isBeta() {
    return isBeta;
  }

  public boolean isEskills() {
    return isEskills;
  }

  public List<String> getBdsFlags() {
    return bdsFlags;
  }

  public String getAppCategory() {
    return appCategory;
  }
}
