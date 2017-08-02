package cm.aptoide.pt.v8engine.ads;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.database.realm.StoredMinimalAd;
import cm.aptoide.pt.dataprovider.model.MinimalAdInterface;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;

public class MinimalAdMapper {

  public MinimalAd map(GetAdsResponse.Ad ad) {
    GetAdsResponse.Partner partner = ad.getPartner();
    int id = 0;
    String clickUrl = null;
    if (partner != null) {
      id = partner.getInfo()
          .getId();
      clickUrl = partner.getData()
          .getClickUrl();
    }
    return new MinimalAd(ad.getData()
        .getPackageName(), id, clickUrl, ad.getInfo()
        .getCpcUrl(), ad.getInfo()
        .getCpdUrl(), ad.getData()
        .getId(), ad.getInfo()
        .getAdId(), ad.getInfo()
        .getCpiUrl(), ad.getData()
        .getName(), ad.getData()
        .getIcon(), ad.getData()
        .getDescription(), ad.getData()
        .getDownloads(), ad.getData()
        .getStars(), ad.getData()
        .getModified()
        .getTime());
  }

  public StoredMinimalAd map(MinimalAd minimalAd, String referrer) {

    String packageName = minimalAd.getPackageName();
    String cpcUrl = minimalAd.getCpcUrl();
    String cpdUrl = minimalAd.getCpdUrl();
    String cpiUrl = minimalAd.getCpiUrl();
    Long adId = minimalAd.getAdId();

    return new StoredMinimalAd(packageName, referrer, cpcUrl, cpdUrl, cpiUrl, adId);
  }

  public MinimalAdInterface map(StoredMinimalAd storedMinimalAd) {
    return new MinimalAdInterface() {

      public String cpdUrl;

      @Override public String getCpcUrl() {
        return storedMinimalAd.getCpcUrl();
      }

      @Override public String getCpdUrl() {
        return (cpdUrl == null) ? storedMinimalAd.getCpdUrl() : cpdUrl;
      }

      @Override public void setCpdUrl(String cpdUrl) {
        this.cpdUrl = cpdUrl;
      }

      @Override public String getCpiUrl() {
        return storedMinimalAd.getCpiUrl();
      }
    };
  }

  public MinimalAdInterface map(MinimalAd minimalAd) {
    return new MinimalAdInterface() {

      public String cpdUrl;

      @Override public String getCpcUrl() {
        return minimalAd.getCpcUrl();
      }

      @Override public String getCpdUrl() {
        return (cpdUrl == null) ? minimalAd.getCpdUrl() : cpdUrl;
      }

      @Override public void setCpdUrl(String cpdUrl) {
        this.cpdUrl = cpdUrl;
      }

      @Override public String getCpiUrl() {
        return minimalAd.getCpiUrl();
      }
    };
  }
}
