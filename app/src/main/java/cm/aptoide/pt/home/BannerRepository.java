package cm.aptoide.pt.home;

import rx.Single;

public class BannerRepository {

  public Single<HomeBundle> getBannerBundle() {
    return Single.just(new BannerBundle());
  }
}
