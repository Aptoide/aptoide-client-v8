package cm.aptoide.pt.home;

import cm.aptoide.pt.abtesting.experiments.HighlightedAdExperiment;
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
  private final HighlightedAdExperiment highlightedAdExperiment;

  public Home(BundlesRepository bundlesRepository, ImpressionManager impressionManager,
      AdsManager adsManager, HighlightedAdExperiment highlightedAdExperiment) {
    this.bundlesRepository = bundlesRepository;
    this.impressionManager = impressionManager;
    this.adsManager = adsManager;
    this.highlightedAdExperiment = highlightedAdExperiment;
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

  public Single<AppNextAdResult> loadAppNextAd() {
    return highlightedAdExperiment.getAppNextAd();
  }

  public Observable<Boolean> recordAppNextImpression() {
    return highlightedAdExperiment.recordAdImpression();
  }

  public Observable<Boolean> recordAppNextClick() {
    return highlightedAdExperiment.recordAdClick();
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
