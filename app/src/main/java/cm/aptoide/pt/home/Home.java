package cm.aptoide.pt.home;

import cm.aptoide.pt.app.AdsManager;
import cm.aptoide.pt.app.AppNextAdResult;
import cm.aptoide.pt.impressions.ImpressionManager;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class Home {

  private final BundlesRepository bundlesRepository;
  private final ImpressionManager impressionManager;
  private final AdsManager adsManager;

  public Home(BundlesRepository bundlesRepository, ImpressionManager impressionManager,
      AdsManager adsManager) {
    this.bundlesRepository = bundlesRepository;
    this.impressionManager = impressionManager;
    this.adsManager = adsManager;
  }

  public Single<HomeBundlesModel> loadHomeBundles() {
    return bundlesRepository.loadHomeBundles();
  }

  public Single<HomeBundlesModel> loadFreshHomeBundles() {
    return bundlesRepository.loadFreshHomeBundles();
  }

  public Single<HomeBundlesModel> loadNextHomeBundles() {
    return bundlesRepository.loadNextHomeBundles();
  }

  public PublishSubject<AppNextAdResult> appNextClick() {
    return adsManager.appNextAdClick();
  }

  public boolean hasMore() {
    return bundlesRepository.hasMore();
  }

  public Completable remove(ActionBundle bundle) {
    return impressionManager.markAsRead(bundle.getActionItem()
        .getCardId(), true)
        .andThen(bundlesRepository.remove(bundle));
  }

  public Completable actionBundleImpression(ActionBundle bundle) {
    return impressionManager.markAsRead(bundle.getActionItem()
        .getCardId(), false);
  }
}
