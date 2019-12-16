package cm.aptoide.pt.home.bundles.ads.banner;

import cm.aptoide.pt.home.bundles.base.HomeBundle;
import rx.Single;

public class BannerRepository {

  public Single<HomeBundle> getBannerBundle() {
    return Single.just(new BannerBundle());
  }
}
