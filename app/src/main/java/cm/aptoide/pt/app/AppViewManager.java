package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.DetailedApp;
import java.util.List;
import rx.Completable;
import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class AppViewManager {

  private final UpdatesManager updatesManager;
  private final InstallManager installManager;
  private final DownloadFactory downloadFactory;
  private final AppCenter appCenter;
  private final ReviewsManager reviewsManager;
  private final AdsManager adsManager;
  private final StoreManager storeManager;
  private final FlagManager flagManager;
  private final StoreUtilsProxy storeUtilsProxy;
  private final AptoideAccountManager aptoideAccountManager;
  private DetailedApp cachedApp;

  public AppViewManager(UpdatesManager updatesManager, InstallManager installManager,
      DownloadFactory downloadFactory, AppCenter appCenter, ReviewsManager reviewsManager,
      AdsManager adsManager, StoreManager storeManager, FlagManager flagManager,
      StoreUtilsProxy storeUtilsProxy, AptoideAccountManager aptoideAccountManager) {
    this.updatesManager = updatesManager;
    this.installManager = installManager;
    this.downloadFactory = downloadFactory;
    this.appCenter = appCenter;
    this.reviewsManager = reviewsManager;
    this.adsManager = adsManager;
    this.storeManager = storeManager;
    this.flagManager = flagManager;
    this.storeUtilsProxy = storeUtilsProxy;
    this.aptoideAccountManager = aptoideAccountManager;
  }

  public Single<DetailedAppViewModel> getDetailedAppViewModel(long appId, String packageName) {
    if (cachedApp != null && cachedApp.getId() == appId && cachedApp.getPackageName()
        .equals(packageName)) {
      return isStoreFollowed(cachedApp.getStore()
          .getId()).map(isStoreFollowed -> createDetailedAppViewModel(cachedApp, isStoreFollowed));
    }
    return appCenter.getDetailedApp(appId, packageName)
        .flatMap(app -> isStoreFollowed(app.getStore()
            .getId()).map(isStoreFollowed -> {
          cachedApp = app;
          return createDetailedAppViewModel(app, isStoreFollowed);
        }));
  }

  public Single<ReviewsViewModel> getReviewsViewModel(String storeName, String packageName,
      int maxReviews, String languagesFilterSort) {
    return reviewsManager.loadReviews(storeName, packageName, maxReviews, languagesFilterSort)
        .map(reviews -> new ReviewsViewModel(reviews));
  }

  public Single<AdsViewModel> loadSimilarApps(String packageName, List<String> keyWords,
      int limit) {
    return loadAdForSimilarApps(packageName, keyWords).flatMap(
        ad -> loadRecommended(limit, packageName).map(
            recommendedApps -> new AdsViewModel(ad, recommendedApps)));
  }

  public Single<MinimalAd> loadAd(String packageName, String storeName) {
    return adsManager.loadAd(packageName, storeName);
  }

  public Single<Boolean> addReviewRatingRequestAction(long reviewId, boolean helpful) {
    return reviewsManager.doReviewRatingRequest(reviewId, helpful)
        .map(response -> (response.isOk() && response.getErrors()
            .isEmpty()));
  }

  public Single<Boolean> addApkFlagRequestAction(String storeName, String md5,
      GetAppMeta.GetAppMetaFile.Flags.Vote.Type type) {
    return flagManager.loadAddApkFlagRequest(storeName, md5, type.name()
        .toLowerCase())
        .map(response -> (response.isOk() && !response.hasErrors()));
  }

  public Completable subscribeStore(String storeName) {
    return Completable.fromAction(
        () -> storeUtilsProxy.subscribeStore(storeName, null, null, aptoideAccountManager));
  }

  private Single<List<App>> loadRecommended(int limit, String packageName) {
    return appCenter.loadRecommendedApps(limit, packageName);
  }

  private Single<MinimalAd> loadAdForSimilarApps(String packageName, List<String> keyWords) {
    return adsManager.loadSuggestedApps(packageName, keyWords)
        .map(adsForSimilarApps -> adsForSimilarApps.get(0));
  }

  private Single<Boolean> isStoreFollowed(long storeId) {
    return storeManager.isSubscribed(storeId)
        .first()
        .toSingle();
  }

  private DetailedAppViewModel createDetailedAppViewModel(DetailedApp app,
      boolean isStoreFollowed) {
    GetAppMeta.Stats stats = app.getStats();
    return new DetailedAppViewModel(app, app.getId(), app.getName(), app.getStore(), app.getFile(),
        app.getPackageName(), app.getSize(), stats.getDownloads(), stats.getGlobalRating(),
        stats.getPdownloads(), stats.getRating(), app.getDeveloper(), app.getGraphic(),
        app.getIcon(), app.getMedia(), app.getModified(), app.getAdded(), app.getObb(),
        app.getPay(), isStoreFollowed);
  }
}
