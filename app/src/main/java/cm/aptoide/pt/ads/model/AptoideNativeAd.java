package cm.aptoide.pt.ads.model;

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
}
