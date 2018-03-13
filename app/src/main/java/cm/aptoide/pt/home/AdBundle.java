package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import java.util.List;

/**
 * Created by jdandrade on 13/03/2018.
 */

public class AdBundle implements HomeBundle {
  private final String title;
  private final List<GetAdsResponse.Ad> ads;

  public AdBundle(String title, List<GetAdsResponse.Ad> ads) {
    this.title = title;
    this.ads = ads;
  }

  @Override public String getTitle() {
    return title;
  }

  @Override public List<?> getContent() {
    return ads;
  }

  @Override public BundleType getType() {
    return BundleType.ADS;
  }

  public List<GetAdsResponse.Ad> getAds() {
    return ads;
  }
}
