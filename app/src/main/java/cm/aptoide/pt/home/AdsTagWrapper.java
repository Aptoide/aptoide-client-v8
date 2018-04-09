package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import java.util.List;

/**
 * Created by jdandrade on 28/03/2018.
 */

public class AdsTagWrapper {
  private final List<GetAdsResponse.Ad> ads;
  private final String tag;

  public AdsTagWrapper(List<GetAdsResponse.Ad> ads, String tag) {
    this.ads = ads;
    this.tag = tag;
  }

  public List<GetAdsResponse.Ad> getAds() {
    return ads;
  }

  public String getTag() {
    return tag;
  }
}
