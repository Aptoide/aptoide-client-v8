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
  private float appcValue;

  public PromotionViewApp(DownloadModel downloadModel, String name, String packageName, long appId,
      String downloadPath, String alternativePath, String appIcon, boolean isClaimed,
      String description, long size, float rating, int numberOfDownloads, String md5,
      int versionCode, String versionName, Obb obb, float appcValue) {
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

  public DownloadModel getDownloadModel() {
    return downloadModel;
  }

  public void setDownloadModel(DownloadModel downloadModel) {
    this.downloadModel = downloadModel;
  }

  public boolean isClaimed() {
    return isClaimed;
  }

  public void setClaimed() {
    isClaimed = true;
  }

  public String getVersionName() {
    return versionName;
  }

  public Obb getObb() {
    return obb;
  }

  @Override public int hashCode() {
    int result = downloadModel != null ? downloadModel.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (packageName != null ? packageName.hashCode() : 0);
    result = 31 * result + (int) (appId ^ (appId >>> 32));
    result = 31 * result + (downloadPath != null ? downloadPath.hashCode() : 0);
    result = 31 * result + (alternativePath != null ? alternativePath.hashCode() : 0);
    result = 31 * result + (appIcon != null ? appIcon.hashCode() : 0);
    result = 31 * result + (isClaimed ? 1 : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (int) (size ^ (size >>> 32));
    result = 31 * result + (rating != +0.0f ? Float.floatToIntBits(rating) : 0);
    result = 31 * result + numberOfDownloads;
    result = 31 * result + (md5 != null ? md5.hashCode() : 0);
    result = 31 * result + versionCode;
    result = 31 * result + (versionName != null ? versionName.hashCode() : 0);
    result = 31 * result + (obb != null ? obb.hashCode() : 0);
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PromotionViewApp that = (PromotionViewApp) o;
    if (md5 != null ? !md5.equals(that.md5) : that.md5 != null) return false;
    return true;
  }

  public float getAppcValue() {
    return appcValue;
  }
}
