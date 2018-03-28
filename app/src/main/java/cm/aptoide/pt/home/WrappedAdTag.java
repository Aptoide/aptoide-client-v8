package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;

/**
 * Created by jdandrade on 28/03/2018.
 */

public class WrappedAdTag {
  private final GetAdsResponse.Ad ad;
  private final String tag;

  public WrappedAdTag(GetAdsResponse.Ad ad, String tag) {
    this.ad = ad;
    this.tag = tag;
  }

  public GetAdsResponse.Ad getAd() {
    return ad;
  }

  public String getTag() {
    return tag;
  }
}
