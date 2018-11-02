package cm.aptoide.pt.ads.data;

import android.view.View;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;

/**
 * Created by franciscoaleixo on 04/10/2018.
 */

public class AptoideNativeAd implements ApplicationAd {
  private String cpdUrl;
  private String description;
  private String packageName;
  private Long networkId;
  private String clickUrl;
  private String cpcUrl;
  private Long appId;
  private Long adId;
  private String cpiUrl;
  private String name;
  private String iconPath;
  private Integer downloads;
  private Integer stars;
  private Long modified;

  public AptoideNativeAd(MinimalAd ad) {
    this.packageName = ad.getPackageName();
    this.networkId = ad.getNetworkId();
    this.clickUrl = ad.getClickUrl();
    this.cpcUrl = ad.getCpcUrl();
    this.cpdUrl = ad.getCpdUrl();
    this.appId = ad.getAppId();
    this.adId = ad.getAdId();
    this.cpiUrl = ad.getCpiUrl();
    this.name = ad.getName();
    this.iconPath = ad.getIconPath();
    this.description = ad.getDescription();
    this.downloads = ad.getDownloads();
    this.stars = ad.getStars();
    this.modified = ad.getModified();
  }

  public AptoideNativeAd(GetAdsResponse.Ad ad) {
    GetAdsResponse.Partner partner = ad.getPartner();
    int id = 0;
    String clickUrl = null;
    if (partner != null) {
      id = partner.getInfo()
          .getId();
      clickUrl = partner.getData()
          .getClickUrl();
    }
    this.packageName = ad.getData()
        .getPackageName();
    this.networkId = (long) id;
    this.clickUrl = clickUrl;
    this.cpcUrl = ad.getInfo()
        .getCpcUrl();
    this.cpdUrl = ad.getInfo()
        .getCpdUrl();
    this.appId = ad.getData()
        .getId();
    this.adId = ad.getInfo()
        .getAdId();
    this.cpiUrl = ad.getInfo()
        .getCpiUrl();
    this.name = ad.getData()
        .getName();
    this.iconPath = ad.getData()
        .getIcon();
    this.description = ad.getData()
        .getDescription();
    this.downloads = ad.getData()
        .getDownloads();
    this.stars = ad.getData()
        .getStars();
    this.modified = ad.getData()
        .getModified()
        .getTime();
  }

  @Override public String getAdTitle() {
    return name;
  }

  @Override public String getIconUrl() {
    return iconPath;
  }

  @Override public Integer getStars() {
    return stars;
  }

  @Override public void registerClickableView(View view) {
  }

  @Override public String getPackageName() {
    return packageName;
  }

  @Override public Network getNetwork() {
    return Network.SERVER;
  }

  public String getCpdUrl() {
    return cpdUrl;
  }

  public String getDescription() {
    return description;
  }

  public Long getNetworkId() {
    return networkId;
  }

  public String getClickUrl() {
    return clickUrl;
  }

  public String getCpcUrl() {
    return cpcUrl;
  }

  public Long getAppId() {
    return appId;
  }

  public Long getAdId() {
    return adId;
  }

  public String getCpiUrl() {
    return cpiUrl;
  }

  public Integer getDownloads() {
    return downloads;
  }

  public Long getModified() {
    return modified;
  }

  @Override public int hashCode() {
    int result = cpdUrl.hashCode();
    result = 31 * result + description.hashCode();
    result = 31 * result + packageName.hashCode();
    result = 31 * result + clickUrl.hashCode();
    result = 31 * result + cpcUrl.hashCode();
    result = 31 * result + cpiUrl.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + iconPath.hashCode();
    result = 31 * result + downloads.hashCode();
    result = 31 * result + stars.hashCode();
    result = 31 * result + (int) (networkId ^ (networkId >>> 32));
    result = 31 * result + (int) (appId ^ (appId >>> 32));
    result = 31 * result + (int) (adId ^ (adId >>> 32));
    result = 31 * result + (int) (modified ^ (modified >>> 32));
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AptoideNativeAd)) return false;

    AptoideNativeAd ad = (AptoideNativeAd) o;

    return ad.modified.equals(this.modified)
        && ad.stars.equals(this.stars)
        && ad.downloads.equals(this.downloads)
        && ad.description.equals(this.description)
        && ad.iconPath.equals(this.iconPath)
        && ad.name.equals(this.name)
        && ad.cpiUrl.equals(this.cpiUrl)
        && ad.adId.equals(this.adId)
        && ad.cpdUrl.equals(this.cpdUrl)
        && ad.cpcUrl.equals(this.cpcUrl)
        && ad.appId.equals(this.appId)
        && ad.clickUrl.equals(this.clickUrl)
        && ad.networkId.equals(this.networkId)
        && ad.packageName.equals(packageName);
  }
}
