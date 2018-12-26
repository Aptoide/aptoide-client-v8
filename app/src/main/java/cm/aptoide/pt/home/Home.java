package cm.aptoide.pt.home;

import cm.aptoide.pt.app.AdsManager;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.impressions.ImpressionManager;
import java.util.ArrayList;
import java.util.List;
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
  private PromotionsPreferencesManager promotionsPreferencesManager;

  public Home(BundlesRepository bundlesRepository, ImpressionManager impressionManager,
      PromotionsManager promotionsManager,
      PromotionsPreferencesManager promotionsPreferencesManager) {
    this.bundlesRepository = bundlesRepository;
    this.impressionManager = impressionManager;
    this.promotionsManager = promotionsManager;
    this.promotionsPreferencesManager = promotionsPreferencesManager;
  }

  public Single<HomeBundlesModel> loadHomeBundles() {
    return bundlesRepository.loadHomeBundles()
        .map(homeBundlesModel -> {
          if (homeBundlesModel.hasErrors()) {
            return new HomeBundlesModel(homeBundlesModel.getError());
          } else {
            return new HomeBundlesModel(injectLargeBanner(homeBundlesModel.getList()),
                homeBundlesModel.isLoading(), homeBundlesModel.getOffset());
          }
        });
  }

  private List<HomeBundle> injectLargeBanner(List<HomeBundle> list) {
    list.add(1, new HomeBundle() {
      @Override public String getTitle() {
        return "Advertising";
      }

      @Override public List<?> getContent() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(new BannerAd());
        return objects;
      }

      @Override public BundleType getType() {
        return BundleType.LARGE_BANNER;
      }

      @Override public Event getEvent() {
        return null;
      }

      @Override public String getTag() {
        return null;
      }
    });
    return list;
  }

  public Single<HomeBundlesModel> loadFreshHomeBundles() {
    return bundlesRepository.loadFreshHomeBundles()
        .map(homeBundlesModel -> {
          if (homeBundlesModel.hasErrors()) {
            return new HomeBundlesModel(homeBundlesModel.getError());
          } else {
            return new HomeBundlesModel(injectLargeBanner(homeBundlesModel.getList()),
                homeBundlesModel.isLoading(), homeBundlesModel.getOffset());
          }
        });
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
    float appcValue = 0;
    if (apps.size() > 0) {
      for (PromotionApp app : apps) {
        if (!app.isClaimed()) {
          promotions++;
          appcValue += app.getAppcValue();
        }
      }
    }

    return new HomePromotionsWrapper(!apps.isEmpty(), promotions, appcValue,
        promotionsPreferencesManager.shouldShowPromotionsDialog());
  }
}
