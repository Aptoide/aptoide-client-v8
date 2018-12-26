package cm.aptoide.pt.promotions;

import cm.aptoide.pt.dataprovider.model.v7.Obb;

public class PromotionApp {

  private String name;
  private String packageName;
  private long appId;
  private String downloadPath;
  private String alternativePath;
  private String appIcon;
  private String description;
  private long size;
  private float rating;
  private int numberOfDownloads;
  private String md5;
  private int versionCode;
  private boolean isClaimed;
  private String versionName;
  private Obb obb;
  private float appcValue;

  public PromotionApp(String name, String packageName, long appId, String downloadPath,
      String alternativePath, String appIcon, String description, long size, float rating,
      int numberOfDownloads, String md5, int versionCode, boolean isClaimed, String versionName,
      Obb obb, float appcValue) {
    this.name = name;
    this.packageName = packageName;
    this.appId = appId;
    this.downloadPath = downloadPath;
    this.alternativePath = alternativePath;
    this.appIcon = appIcon;
    this.description = description;
    this.size = size;
    this.rating = rating;
    this.numberOfDownloads = numberOfDownloads;
    this.md5 = md5;
    this.versionCode = versionCode;
    this.isClaimed = isClaimed;
    this.versionName = versionName;
    this.obb = obb;
    this.appcValue = appcValue;
  }

  public String getName() {
    return name;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getAppId() {
    return appId;
  }

  public String getDownloadPath() {
    return downloadPath;
  }

  public String getAlternativePath() {
    return alternativePath;
  }

  public String getAppIcon() {
    return appIcon;
  }

  public String getDescription() {
    return description;
  }

  public long getSize() {
    return size;
  }

  public float getRating() {
    return rating;
  }

  public int getNumberOfDownloads() {
    return numberOfDownloads;
  }

  public String getMd5() {
    return this.md5;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public boolean isClaimed() {
    return isClaimed;
  }

  public Obb getObb() {
    return obb;
  }

  public String getVersionName() {
    return versionName;
  }

  public float getAppcValue() {
    return appcValue;
  }
}