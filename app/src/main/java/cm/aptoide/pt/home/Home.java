package cm.aptoide.pt.home;

import cm.aptoide.pt.app.AdsManager;
import cm.aptoide.pt.impressions.ImpressionManager;
import cm.aptoide.pt.promotions.PromotionApp;
import cm.aptoide.pt.promotions.PromotionsManager;
import java.util.List;
import rx.Completable;
import rx.Single;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class Home {

  private final BundlesRepository bundlesRepository;
  private final ImpressionManager impressionManager;
  private final PromotionsManager promotionsManager;
  private final AdsManager adsManager;

  public Home(BundlesRepository bundlesRepository, ImpressionManager impressionManager,
      AdsManager adsManager, PromotionsManager promotionsManager) {
    this.bundlesRepository = bundlesRepository;
    this.impressionManager = impressionManager;
    this.adsManager = adsManager;
    this.promotionsManager = promotionsManager;
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

  public Single<HomePromotionsWrapper> hasPromotionApps() {
    return promotionsManager.getPromotionApps()
        .map(this::mapPromotions);
  }

  private HomePromotionsWrapper mapPromotions(List<PromotionApp> apps) {
    boolean hasPromotions = false;
    int promotions = 0;
    int appcValue = 0;
    if (apps.size() > 0) {
      hasPromotions = true;
      for (PromotionApp app : apps) {
        if (!app.isClaimed()) {
          promotions++;
          appcValue += app.getAppcValue();
        }
      }
    }
    return new HomePromotionsWrapper(hasPromotions, promotions, appcValue);
  }
}
