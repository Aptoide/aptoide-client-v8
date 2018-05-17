package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.AppsList;
import cm.aptoide.pt.view.app.DetailedApp;
import cm.aptoide.pt.view.app.DetailedAppRequestResult;
import cm.aptoide.pt.view.app.FlagsVote;
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
      return createDetailedAppViewModel(cachedApp);
    }
    return appCenter.getDetailedApp(appId, packageName)
        .flatMap(requestResult -> mapResultToCorrectDetailedAppViewModel(requestResult));
  }

  public Single<DetailedAppViewModel> getDetailedAppViewModel(long appId, String storeName,
      String packageName) {
    if (cachedApp != null && cachedApp.getId() == appId && cachedApp.getPackageName()
        .equals(packageName) && cachedApp.getStore()
        .getName()
        .equals(storeName)) {
      return createDetailedAppViewModel(cachedApp);
    }
    return appCenter.getDetailedApp(appId, storeName, packageName)
        .flatMap(requestResult -> mapResultToCorrectDetailedAppViewModel(requestResult));
  }

  public Single<DetailedAppViewModel> getDetailedAppViewModel(String packageName,
      String storeName) {
    if (cachedApp != null && cachedApp.getPackageName()
        .equals(packageName) && cachedApp.getStore()
        .getName()
        .equals(storeName)) {
      return createDetailedAppViewModel(cachedApp);
    }
    return appCenter.getDetailedApp(packageName, storeName)
        .flatMap(requestResult -> mapResultToCorrectDetailedAppViewModel(requestResult));
  }

  public Single<DetailedAppViewModel> getDetailedAppViewModelFromMd5(String md5) {
    if (cachedApp != null && cachedApp.getMd5()
        .equals(md5)) {
      return createDetailedAppViewModel(cachedApp);
    }
    return appCenter.getDetailedAppFromMd5(md5)
        .flatMap(requestResult -> mapResultToCorrectDetailedAppViewModel(requestResult));
  }

  public Single<DetailedAppViewModel> getDetailedAppViewModelFromUname(String uName) {
    if (cachedApp != null && cachedApp.getUname()
        .equals(uName)) {
      return createDetailedAppViewModel(cachedApp);
    }
    return appCenter.getDetailedAppAppFromUname(uName)
        .flatMap(requestResult -> mapResultToCorrectDetailedAppViewModel(requestResult));
  }

  public Single<ReviewsViewModel> getReviewsViewModel(String storeName, String packageName,
      int maxReviews, String languagesFilterSort) {
    return reviewsManager.loadReviews(storeName, packageName, maxReviews, languagesFilterSort)
        .map(reviewsRequestResult -> new ReviewsViewModel(reviewsRequestResult.getReviewList(),
            reviewsRequestResult.isLoading(), reviewsRequestResult.getError()));
  }

  public Single<SimilarAppsViewModel> loadSimilarApps(String packageName, List<String> keyWords,
      int limit) {
    return loadAdForSimilarApps(packageName, keyWords).flatMap(
        ad -> loadRecommended(limit, packageName).map(
            recommendedAppsRequestResult -> new SimilarAppsViewModel(ad,
                recommendedAppsRequestResult.getList(), recommendedAppsRequestResult.isLoading(),
                recommendedAppsRequestResult.getError())));
  }

  public Single<MinimalAd> loadAdsFromAppView(String packageName, String storeName) {
    return adsManager.loadAdsFromAppView(packageName, storeName);
  }

  public Single<Boolean> addReviewRatingRequestAction(long reviewId, boolean helpful) {
    return reviewsManager.doReviewRatingRequest(reviewId, helpful)
        .map(response -> (response.isOk() && response.getErrors()
            .isEmpty()));
  }

  public Single<Boolean> addApkFlagRequestAction(String storeName, String md5,
      FlagsVote.VoteType type) {
    return flagManager.loadAddApkFlagRequest(storeName, md5, type.name()
        .toLowerCase())
        .map(response -> (response.isOk() && !response.hasErrors()));
  }

  public Completable subscribeStore(String storeName) {
    return Completable.fromAction(
        () -> storeUtilsProxy.subscribeStore(storeName, null, null, aptoideAccountManager));
  }

  private Single<AppsList> loadRecommended(int limit, String packageName) {
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

  private Single<DetailedAppViewModel> createDetailedAppViewModel(DetailedApp app) {
    GetAppMeta.Stats stats = app.getStats();
    cachedApp = app;
    return isStoreFollowed(cachedApp.getStore()
        .getId()).map(
        isStoreFollowed -> new DetailedAppViewModel(app, app.getId(), app.getName(), app.getStore(),
            app.isGoodApp(), app.getMalware(), app.getAppFlags(), app.getTags(),
            app.getUsedFeatures(), app.getUsedPermissions(), app.getFileSize(), app.getMd5(),
            app.getMd5Sum(), app.getPath(), app.getPathAlt(), app.getVerCode(), app.getVerName(),
            app.getPackageName(), app.getSize(), stats.getDownloads(), stats.getGlobalRating(),
            stats.getPdownloads(), stats.getRating(), app.getDeveloper(), app.getGraphic(),
            app.getIcon(), app.getMedia(), app.getModified(), app.getAdded(), app.getObb(),
            app.getPay(), app.getwUrls(), app.isPaid(), app.getUname(), isStoreFollowed));
  }

  private Single<DetailedAppViewModel> mapResultToCorrectDetailedAppViewModel(
      DetailedAppRequestResult requestResult) {
    if (requestResult.getDetailedApp() != null) {
      return createDetailedAppViewModel(requestResult.getDetailedApp());
    } else if (requestResult.isLoading()) {
      return Single.just(new DetailedAppViewModel(requestResult.isLoading()));
    } else if (requestResult.hasError()) {
      return Single.just(new DetailedAppViewModel(requestResult.getError()));
    } else {
      return Single.just(new DetailedAppViewModel(DetailedAppRequestResult.Error.GENERIC));
    }
  }
}
