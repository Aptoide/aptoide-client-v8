package cm.aptoide.pt.promotions;

import cm.aptoide.pt.app.DownloadModel;
import cm.aptoide.pt.dataprovider.model.v7.Obb;

public class PromotionViewApp {

  private DownloadModel downloadModel;
  private String name;
  private String packageName;
  private long appId;
  private String downloadPath;
  private String alternativePath;
  private String appIcon;
  private boolean isClaimed;
  private String description;
  private long size;
  private float rating;
  private int numberOfDownloads;
  private String md5;
  private int versionCode;
  private String versionName;
  private Obb obb;

  public PromotionViewApp(DownloadModel downloadModel, String name, String packageName, long appId,
      String downloadPath, String alternativePath, String appIcon, boolean isClaimed,
      String description, long size, float rating, int numberOfDownloads, String md5,
      int versionCode, String versionName, Obb obb) {
    this.downloadModel = downloadModel;
    this.name = name;
    this.packageName = packageName;
    this.appId = appId;
    this.downloadPath = downloadPath;
    this.alternativePath = alternativePath;
    this.appIcon = appIcon;
    this.isClaimed = isClaimed;
    this.description = description;
    this.size = size;
    this.rating = rating;
    this.numberOfDownloads = numberOfDownloads;
    this.md5 = md5;
    this.versionCode = versionCode;
    this.versionName = versionName;
    this.obb = obb;
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

  public DownloadModel getDownloadModel() {
    return downloadModel;
  }

  public void setDownloadModel(DownloadModel downloadModel) {
    this.downloadModel = downloadModel;
  }

  public boolean isClaimed() {
    return isClaimed;
  }

  public String getVersionName() {
    return versionName;
  }

  public Obb getObb() {
    return obb;
  }
}
