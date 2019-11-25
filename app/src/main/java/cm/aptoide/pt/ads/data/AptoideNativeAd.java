package cm.aptoide.pt.ads.data;

import android.view.View;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.view.app.Application;

/**
 * Created by franciscoaleixo on 04/10/2018.
 */

public class AptoideNativeAd extends Application implements ApplicationAd {
  private String cpdUrl;
  private String description;
  private Long networkId;
  private String clickUrl;
  private String cpcUrl;
  private Long adId;
  private String cpiUrl;
  private Integer stars;
  private Long modified;

  private Payout payout;

  public AptoideNativeAd(MinimalAd ad) {
    super(ad.getName(), ad.getIconPath(), 0f, ad.getDownloads(), ad.getPackageName(), ad.getAppId(),
        "", false);
    this.networkId = ad.getNetworkId();
    this.clickUrl = ad.getClickUrl();
    this.cpcUrl = ad.getCpcUrl();
    this.cpdUrl = ad.getCpdUrl();
    this.adId = ad.getAdId();
    this.cpiUrl = ad.getCpiUrl();
    this.description = ad.getDescription();
    this.stars = ad.getStars();
    this.modified = ad.getModified();
  }

  public AptoideNativeAd(GetAdsResponse.Ad ad) {
    super(ad.getData()
        .getName(), ad.getData()
        .getIcon(), 0f, ad.getData()
        .getDownloads(), ad.getData()
        .getPackageName(), ad.getData()
        .getId(), "", false);
    GetAdsResponse.Partner partner = ad.getPartner();
    int id = 0;
    String clickUrl = null;
    if (partner != null) {
      id = partner.getInfo()
          .getId();
      clickUrl = partner.getData()
          .getClickUrl();
    }
    this.networkId = (long) id;
    this.clickUrl = clickUrl;
    this.cpcUrl = ad.getInfo()
        .getCpcUrl();
    this.cpdUrl = ad.getInfo()
        .getCpdUrl();
    this.adId = ad.getInfo()
        .getAdId();
    this.cpiUrl = ad.getInfo()
        .getCpiUrl();
    this.description = ad.getData()
        .getDescription();
    this.stars = ad.getData()
        .getStars();
    this.modified = ad.getData()
        .getModified()
        .getTime();
    GetAdsResponse.Info.Payout appcPayout = ad.getInfo()
        .getPayout();
    if (appcPayout != null) {
      GetAdsResponse.Info.Fiat fiat = appcPayout.getFiat();
      this.payout =
          new Payout(appcPayout.getAppc(), fiat.getAmount(), fiat.getCurrency(), fiat.getSymbol());
    }
  }

  @Override public String getAdTitle() {
    return super.getName();
  }

  @Override public String getIconUrl() {
    return super.getIcon();
  }

  @Override public Integer getStars() {
    return stars;
  }

  @Override public void registerClickableView(View view) {
  }

  @Override public Network getNetwork() {
    return Network.SERVER;
  }

  @Override public boolean hasAppcPayout() {
    return payout != null;
  }

  @Override public Payout getAppcPayout() {
    return payout;
  }

  @Override public void setAdView(View adView) {
  }

  @Override public String getPackageName() {
    return super.getPackageName();
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

  public Long getAdId() {
    return adId;
  }

  public String getCpiUrl() {
    return cpiUrl;
  }

  public Long getModified() {
    return modified;
  }

  @Override public int hashCode() {
    int result = cpdUrl.hashCode();
    result = 31 * result + description.hashCode();
    result = 31 * result + getPackageName().hashCode();
    result = 31 * result + clickUrl.hashCode();
    result = 31 * result + cpcUrl.hashCode();
    result = 31 * result + cpiUrl.hashCode();
    result = 31 * result + getName().hashCode();
    result = 31 * result + getIcon().hashCode();
    result = 31 * result + ((Integer) getDownloads()).hashCode();
    result = 31 * result + stars.hashCode();
    result = 31 * result + (int) (networkId ^ (networkId >>> 32));
    result = 31 * result + (int) (getAppId() ^ (getAppId() >>> 32));
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
        && ((Integer) ad.getDownloads()).equals(this.getDownloads())
        && ad.description.equals(this.description)
        && ad.getIcon()
        .equals(this.getIcon())
        && ad.getName()
        .equals(this.getName())
        && ad.cpiUrl.equals(this.cpiUrl)
        && ad.adId.equals(this.adId)
        && ad.cpdUrl.equals(this.cpdUrl)
        && ad.cpcUrl.equals(this.cpcUrl)
        && ((Long) ad.getAppId()).equals(this.getAppId())
        && ad.clickUrl.equals(this.clickUrl)
        && ad.networkId.equals(this.networkId)
        && ad.getPackageName()
        .equals(getPackageName());
  }
}
