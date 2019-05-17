package cm.aptoide.pt.app;

import cm.aptoide.pt.dataprovider.model.v7.Obb;

public class WalletPromotionViewModel {

  private final DownloadModel downloadModel;
  private final String appName;
  private final String icon;
  private final long id;
  private final String packageName;
  private final String md5sum;
  private final int versionCode;
  private final String versionName;
  private final String path;
  private final String pathAlt;
  private final Obb obb;
  private final int appcValue;
  private final boolean isWalletInstalled;
  private final boolean shouldShowOffer;
  private final boolean isAppViewAppInstalled;
  private final long size;

  public WalletPromotionViewModel(DownloadModel downloadModel, String appName, String icon, long id,
      String packageName, String md5sum, int versionCode, String versionName, String path,
      String pathAlt, Obb obb, int appcValue, boolean isWalletInstalled, boolean shouldShowOffer,
      boolean isAppViewAppInstalled, long size) {
    this.downloadModel = downloadModel;
    this.appName = appName;
    this.icon = icon;
    this.id = id;
    this.packageName = packageName;
    this.md5sum = md5sum;
    this.versionCode = versionCode;
    this.versionName = versionName;
    this.path = path;
    this.pathAlt = pathAlt;
    this.obb = obb;
    this.appcValue = appcValue;
    this.isWalletInstalled = isWalletInstalled;
    this.shouldShowOffer = shouldShowOffer;
    this.isAppViewAppInstalled = isAppViewAppInstalled;
    this.size = size;
  }

  public WalletPromotionViewModel(boolean shouldShowOffer) {
    this.shouldShowOffer = shouldShowOffer;
    this.isAppViewAppInstalled = false;
    this.downloadModel = null;
    this.appName = "";
    this.icon = "";
    this.id = -1;
    this.packageName = null;
    this.md5sum = null;
    this.versionCode = -1;
    this.versionName = null;
    this.path = null;
    this.pathAlt = null;
    this.obb = null;
    this.appcValue = -1;
    this.isWalletInstalled = false;
    this.size = 0;
  }

  public String getAppName() {
    return appName;
  }

  public String getIcon() {
    return icon;
  }

  public long getId() {
    return id;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getMd5sum() {
    return md5sum;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public String getVersionName() {
    return versionName;
  }

  public String getPath() {
    return path;
  }

  public String getPathAlt() {
    return pathAlt;
  }

  public Obb getObb() {
    return obb;
  }

  public int getAppcValue() {
    return appcValue;
  }

  public DownloadModel getDownloadModel() {
    return this.downloadModel;
  }

  public boolean shouldShowOffer() {
    return this.shouldShowOffer;
  }

  public boolean isWalletInstalled() {
    return isWalletInstalled;
  }

  public boolean isAppViewAppInstalled() {
    return isAppViewAppInstalled;
  }

  public long getSize() {
    return size;
  }
}
