package cm.aptoide.pt.home;

import cm.aptoide.pt.ads.model.ApplicationAd;
import cm.aptoide.pt.ads.model.AptoideNativeAd;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;

/**
 * Created by jdandrade on 28/03/2018.
 */

public class AdClick {
  private final ApplicationAd ad;
  private final String tag;

  public AdClick(GetAdsResponse.Ad ad, String tag) {
    this.ad = new AptoideNativeAd(ad);
    this.tag = tag;
  }

  public AdClick(ApplicationAd ad, String tag) {
    this.ad = ad;
    this.tag = tag;
  }

  public ApplicationAd getAd() {
    return ad;
  }

  public String getTag() {
    return tag;
  }
}
