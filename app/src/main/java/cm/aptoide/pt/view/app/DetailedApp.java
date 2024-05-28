package cm.aptoide.pt.view.app;

import cm.aptoide.pt.aab.Split;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import java.util.List;

/**
 * Created by D01 on 04/05/18.
 */

public class DetailedApp {

  private final String name;
  private final String packageName;
  private final long size;
  private final String icon;
  private final String graphic;
  private final String added;
  private final String modified;
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
  private final AppDeveloper appDeveloper;
  private final Store store;
  private final AppMedia media;
  private final AppStats stats;
  private final Obb obb;
  private final String webUrls;
  private final boolean isLatestTrustedVersion;
  private final String uniqueName;
  private final List<Split> splits;
  private final List<String> requiredSplits;
  private final boolean isBeta;
  private String path;
  private long id;
  private boolean hasBilling;
  private boolean hasAdvertising;
  private List<String> bdsFlags;
  private boolean isMature;
  private String signature;
  private String appCategory;

  public DetailedApp(long id, String name, String packageName, long size, String icon,
      String graphic, String added, String modified, boolean isGoodApp, Malware malware,
      AppFlags appFlags, List<String> tags, List<String> usedFeatures, List<String> usedPermissions,
      long fileSize, String md5, String path, String pathAlt, int versionCode, String versionName,
      AppDeveloper appDeveloper, Store store, AppMedia media, AppStats stats, Obb obb,
      String webUrls, boolean isLatestTrustedVersion, String uniqueName, boolean hasBilling,
      boolean hasAdvertising, List<String> bdsFlags, boolean isMature, String signature,
      List<Split> splits, List<String> requiredSplits, boolean isBeta, String appCategory) {

    this.id = id;
    this.name = name;
    this.packageName = packageName;
    this.size = size;
    this.icon = icon;
    this.graphic = graphic;
    this.added = added;
    this.modified = modified;
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
    this.appDeveloper = appDeveloper;
    this.store = store;
    this.media = media;
    this.stats = stats;
    this.obb = obb;
    this.webUrls = webUrls;
    this.hasBilling = hasBilling;
    this.hasAdvertising = hasAdvertising;
    this.bdsFlags = bdsFlags;
    this.isMature = isMature;
    this.splits = splits;
    this.requiredSplits = requiredSplits;
    this.signature = signature;
    this.isLatestTrustedVersion = isLatestTrustedVersion;
    this.uniqueName = uniqueName;
    this.isBeta = isBeta;
    this.appCategory = appCategory;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getSize() {
    return size;
  }

  public String getIcon() {
    return icon;
  }

  public String getGraphic() {
    return graphic;
  }

  public String getAdded() {
    return added;
  }

  public String getModified() {
    return modified;
  }

  public Store getStore() {
    return store;
  }

  public AppDeveloper getDeveloper() {
    return appDeveloper;
  }

  public AppMedia getMedia() {
    return media;
  }

  public AppStats getStats() {
    return stats;
  }

  public Obb getObb() {
    return obb;
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

  public boolean isLatestTrustedVersion() {
    return isLatestTrustedVersion;
  }

  public boolean hasBilling() {
    return hasBilling;
  }

  public boolean hasAdvertising() {
    return this.hasAdvertising;
  }

  public List<String> getBdsFlags() {
    return bdsFlags;
  }

  public void setBdsFlags(List<String> bdsFlags) {
    this.bdsFlags = bdsFlags;
  }

  public boolean isMature() {
    return isMature;
  }

  public String getSignature() {
    return signature;
  }

  public List<Split> getSplits() {
    return this.splits;
  }

  public List<String> getRequiredSplits() {
    return this.requiredSplits;
  }

  public boolean isBeta() {
    return this.isBeta;
  }

  public String getAppCategory() {
    return appCategory;
  }
}
