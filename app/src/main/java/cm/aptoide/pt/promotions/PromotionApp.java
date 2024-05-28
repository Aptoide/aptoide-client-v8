package cm.aptoide.pt.promotions;

import cm.aptoide.pt.aab.Split;
import cm.aptoide.pt.dataprovider.model.v7.Obb;
import java.util.List;

public class PromotionApp {

  private final String name;
  private final String packageName;
  private final long appId;
  private final String downloadPath;
  private final String alternativePath;
  private final String appIcon;
  private final String description;
  private final long size;
  private final float rating;
  private final int numberOfDownloads;
  private final String md5;
  private final int versionCode;
  private final boolean isClaimed;
  private final String versionName;
  private final Obb obb;
  private final float appcValue;
  private final String signature;
  private final boolean hasAppc;
  private final List<Split> splits;
  private final List<String> requiredSplits;
  private final String rank;
  private final String storeName;
  private final double fiatValue;
  private final String fiatSymbol;

  private final List<String> bdsFlags;


  public PromotionApp(String name, String packageName, long appId, String downloadPath,
      String alternativePath, String appIcon, String description, long size, float rating,
      int numberOfDownloads, String md5, int versionCode, boolean isClaimed, String versionName,
      Obb obb, float appcValue, String signature, boolean hasAppc, List<Split> splits,
      List<String> requiredSplits, String rank, String storeName, double fiatValue,
      String fiatSymbol, List<String> bdsFlags) {
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
    this.signature = signature;
    this.hasAppc = hasAppc;
    this.splits = splits;
    this.requiredSplits = requiredSplits;
    this.rank = rank;
    this.storeName = storeName;
    this.fiatValue = fiatValue;
    this.fiatSymbol = fiatSymbol;
    this.bdsFlags = bdsFlags;
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

  public String getSignature() {
    return signature;
  }

  public boolean hasAppc() {
    return hasAppc;
  }

  public List<Split> getSplits() {
    return splits;
  }

  public List<String> getRequiredSplits() {
    return requiredSplits;
  }

  public String getRank() {
    return rank;
  }

  public String getStoreName() {
    return storeName;
  }

  public double getFiatValue() {
    return fiatValue;
  }

  public String getFiatSymbol() {
    return fiatSymbol;
  }

  public List<String> getBdsFlags() {
    return bdsFlags;
  }
}