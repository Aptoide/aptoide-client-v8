package cm.aptoide.pt.home;

import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.blacklist.BlacklistManager;
import cm.aptoide.pt.blacklist.BlacklistUnitMapper;
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
  private final BannerRepository bannerRepository;
  private final MoPubAdsManager moPubAdsManager;
  private PromotionsPreferencesManager promotionsPreferencesManager;
  private BlacklistManager blacklistManager;
  private BlacklistUnitMapper blacklistUnitMapper;

  public Home(BundlesRepository bundlesRepository, ImpressionManager impressionManager,
      PromotionsManager promotionsManager, BannerRepository bannerRepository,
      MoPubAdsManager moPubAdsManager, PromotionsPreferencesManager promotionsPreferencesManager,
      BlacklistManager blacklistManager, BlacklistUnitMapper blacklistUnitMapper) {
    this.bundlesRepository = bundlesRepository;
    this.impressionManager = impressionManager;
    this.promotionsManager = promotionsManager;
    this.bannerRepository = bannerRepository;
    this.moPubAdsManager = moPubAdsManager;
    this.promotionsPreferencesManager = promotionsPreferencesManager;
    this.blacklistManager = blacklistManager;
    this.blacklistUnitMapper = blacklistUnitMapper;
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
    return moPubAdsManager.shouldLoadBannerAd()
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

  public Completable removeWalletOfferCard(ActionBundle bundle, String cardId) {
    return Completable.fromAction(() -> blacklistManager.blacklist(
        blacklistUnitMapper.mapToBlacklistUnit(bundle.getType()
            .toString() + "_" + cardId)))
        .andThen(bundlesRepository.remove(bundle));
  }

  public Completable actionBundleImpression(ActionBundle bundle) {
    return Completable.fromAction(() -> blacklistManager.addImpression(
        blacklistUnitMapper.mapToBlacklistUnit(bundle.getType() + "_" + bundle.getActionItem()
            .getCardId())));
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
    return moPubAdsManager.shouldLoadNativeAds();
  }

  public void blacklistWalletOfferCard() {
    blacklistManager.blacklist(BlacklistManager.BlacklistUnit.WALLET_ADS_OFFER);
  }
}
