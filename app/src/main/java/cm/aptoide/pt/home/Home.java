package cm.aptoide.pt.home;

import cm.aptoide.pt.abtesting.experiments.MoPubBannerAdExperiment;
import cm.aptoide.pt.abtesting.experiments.MoPubNativeAdExperiment;
import cm.aptoide.pt.impressions.ImpressionManager;
import cm.aptoide.pt.promotions.PromotionApp;
import cm.aptoide.pt.promotions.PromotionsManager;
import cm.aptoide.pt.promotions.PromotionsPreferencesManager;
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
  private final MoPubBannerAdExperiment bannerAdExperiment;
  private final MoPubNativeAdExperiment nativeAdExperiment;
  private final BannerRepository bannerRepository;
  private PromotionsPreferencesManager promotionsPreferencesManager;

  public Home(BundlesRepository bundlesRepository, ImpressionManager impressionManager,
      PromotionsManager promotionsManager, MoPubBannerAdExperiment bannerAdExperiment,
      MoPubNativeAdExperiment nativeAdExperiment, BannerRepository bannerRepository,
      PromotionsPreferencesManager promotionsPreferencesManager) {
    this.bundlesRepository = bundlesRepository;
    this.impressionManager = impressionManager;
    this.promotionsManager = promotionsManager;
    this.bannerAdExperiment = bannerAdExperiment;
    this.nativeAdExperiment = nativeAdExperiment;
    this.bannerRepository = bannerRepository;
    this.promotionsPreferencesManager = promotionsPreferencesManager;
  }

  public Single<HomeBundlesModel> loadHomeBundles() {
    return bundlesRepository.loadHomeBundles()
        .flatMap(bundlesModel -> {
          if (bundlesModel.hasErrors() || bundlesModel.isLoading()) {
            return Single.just(bundlesModel);
          }
          return addAdBundle(bundlesModel);
        });
  }

  public Single<HomeBundlesModel> loadFreshHomeBundles() {
    return bundlesRepository.loadFreshHomeBundles()
        .flatMap(bundlesModel -> {
          if (bundlesModel.hasErrors() || bundlesModel.isLoading()) {
            return Single.just(bundlesModel);
          }
          return addAdBundle(bundlesModel);
        });
  }

  private Single<HomeBundlesModel> addAdBundle(HomeBundlesModel bundlesModel) {
    return bannerAdExperiment.shouldLoadBanner()
        .flatMap(shouldLoadBanner -> {
          if (shouldLoadBanner) {
            return bannerRepository.getBannerBundle()
                .map(banner -> addBannerToHomeBundleModel(bundlesModel, banner));
          } else {
            return Single.just(bundlesModel);
          }
        });
  }

  private HomeBundlesModel addBannerToHomeBundleModel(HomeBundlesModel bundlesModel,
      HomeBundle banner) {
    if (bundlesModel.isLoading()) {
      return bundlesModel;
    } else if (bundlesModel.hasErrors()) {
      return bundlesModel;
    } else {

      List<HomeBundle> bundleList = bundlesModel.getList();
      bundleList.add(1, banner);
      return new HomeBundlesModel(bundleList, bundlesModel.isLoading(), bundlesModel.getOffset());
    }
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

  public void setPromotionsDialogShown() {
    promotionsPreferencesManager.setPromotionsDialogShown();
  }

  private HomePromotionsWrapper mapPromotions(List<PromotionApp> apps) {
    int promotions = 0;
    float unclaimedAppcValue = 0;
    float totalAppcValue = 0;
    if (apps.size() > 0) {
      for (PromotionApp app : apps) {
        totalAppcValue += app.getAppcValue();

        if (!app.isClaimed()) {
          promotions++;
          unclaimedAppcValue += app.getAppcValue();
        }
      }
    }

    return new HomePromotionsWrapper(!apps.isEmpty(), promotions, unclaimedAppcValue,
        (promotionsPreferencesManager.shouldShowPromotionsDialog() && unclaimedAppcValue > 0),
        totalAppcValue);
  }

  public Single<Boolean> shouldLoadNativeAd() {
    return nativeAdExperiment.shouldLoadNative();
  }
}
