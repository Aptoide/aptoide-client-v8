package cm.aptoide.pt.search.model;

import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.database.realm.MinimalAd;
import org.parceler.Parcel;

@Parcel public class SearchAdResult {
  boolean isAppc;
  long adId;
  String icon;
  long totalDownloads;
  float starRating;
  long modifiedDate;
  String packageName;
  String cpcUrl;
  String clickPerDownloadUrl;
  String clickPerInstallUrl;
  String clickUrl;
  String appName;
  long appId;
  long networkId;

  public SearchAdResult() {
  }

  public SearchAdResult(long adId, String icon, long totalDownloads, float starRating,
      long modifiedDate, String packageName, String cpcUrl, String clickPerDownloadUrl,
      String clickPerInstallUrl, String clickUrl, String appName, long appId, long networkId,
      boolean isAppc) {
    this.adId = adId;
    this.icon = icon;
    this.totalDownloads = totalDownloads;
    this.starRating = starRating;
    this.modifiedDate = modifiedDate;
    this.packageName = packageName;
    this.cpcUrl = cpcUrl;
    this.clickPerDownloadUrl = clickPerDownloadUrl;
    this.clickPerInstallUrl = clickPerInstallUrl;
    this.clickUrl = clickUrl;
    this.appName = appName;
    this.appId = appId;
    this.networkId = networkId;
    this.isAppc = isAppc;
  }

  public SearchAdResult(MinimalAd minimalAd) {
    this(minimalAd.getAdId(), minimalAd.getIconPath(), minimalAd.getDownloads(),
        minimalAd.getStars(), minimalAd.getModified(), minimalAd.getPackageName(),
        minimalAd.getCpcUrl(), minimalAd.getCpdUrl(), minimalAd.getCpiUrl(),
        minimalAd.getClickUrl(), minimalAd.getName(), minimalAd.getAppId(),
        minimalAd.getNetworkId(), false);
  }

  public SearchAdResult(AptoideNativeAd ad) {
    this(ad.getAdId(), ad.getIconUrl(), ad.getDownloads(), ad.getStars(), ad.getModified(),
        ad.getPackageName(), ad.getCpcUrl(), ad.getCpdUrl(), ad.getCpiUrl(), ad.getClickUrl(),
        ad.getAdTitle(), ad.getAppId(), ad.getNetworkId(), false);
  }

  public long getAdId() {
    return adId;
  }

  public String getIcon() {
    return icon;
  }

  public long getTotalDownloads() {
    return totalDownloads;
  }

  public float getStarRating() {
    return starRating;
  }

  public long getModifiedDate() {
    return modifiedDate;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getCpcUrl() {
    return cpcUrl;
  }

  public String getClickPerDownloadUrl() {
    return clickPerDownloadUrl;
  }

  public String getClickPerInstallUrl() {
    return clickPerInstallUrl;
  }

  public String getClickUrl() {
    return clickUrl;
  }

  public String getAppName() {
    return appName;
  }

  public long getAppId() {
    return appId;
  }

  public long getNetworkId() {
    return networkId;
  }

  public boolean isAppc() {
    return isAppc;
  }
}
