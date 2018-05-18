package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.AppViewConfiguration;
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
  private final AppViewConfiguration appViewConfiguration;
  private DetailedApp cachedApp;

  public AppViewManager(UpdatesManager updatesManager, InstallManager installManager,
      DownloadFactory downloadFactory, AppCenter appCenter, ReviewsManager reviewsManager,
      AdsManager adsManager, StoreManager storeManager, FlagManager flagManager,
      StoreUtilsProxy storeUtilsProxy, AptoideAccountManager aptoideAccountManager,
      AppViewConfiguration appViewConfiguration) {
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
    this.appViewConfiguration = appViewConfiguration;
  }

  public Single<AppViewViewModel> loadAppViewViewModel() {
    if (appViewConfiguration.hasIdStoreNamePackageName()) {
      return loadAppViewViewModel(appViewConfiguration.getAppId(),
          appViewConfiguration.getStoreName(), appViewConfiguration.getPackageName());
    } else if (appViewConfiguration.hasMd5()) {
      return loadAppViewViewModelFromMd5(appViewConfiguration.getMd5());
    } else if (appViewConfiguration.hasUname()) {
      return loadAppViewViewModelFromUname(appViewConfiguration.getuName());
    } else {
      return loadAppViewViewModel(appViewConfiguration.getPackageName(),
          appViewConfiguration.getStoreName());
    }
  }

  private Single<AppViewViewModel> loadAppViewViewModel(long appId, String storeName,
      String packageName) {
    if (cachedApp != null && cachedApp.getId() == appId && cachedApp.getPackageName()
        .equals(packageName) && cachedApp.getStore()
        .getName()
        .equals(storeName)) {
      return createAppViewViewModel(cachedApp);
    }
    return appCenter.loadDetailedApp(appId, storeName, packageName)
        .flatMap(result -> map(result));
  }

  private Single<AppViewViewModel> loadAppViewViewModel(String packageName, String storeName) {
    if (cachedApp != null && cachedApp.getPackageName()
        .equals(packageName) && cachedApp.getStore()
        .getName()
        .equals(storeName)) {
      return createAppViewViewModel(cachedApp);
    }
    return appCenter.loadDetailedApp(packageName, storeName)
        .flatMap(result -> map(result));
  }

  private Single<AppViewViewModel> loadAppViewViewModelFromMd5(String md5) {
    if (cachedApp != null && cachedApp.getMd5()
        .equals(md5)) {
      return createAppViewViewModel(cachedApp);
    }
    return appCenter.loadDetailedAppFromMd5(md5)
        .flatMap(result -> map(result));
  }

  private Single<AppViewViewModel> loadAppViewViewModelFromUname(String uName) {
    if (cachedApp != null && cachedApp.getUname()
        .equals(uName)) {
      return createAppViewViewModel(cachedApp);
    }
    return appCenter.loadDetailedAppAppFromUname(uName)
        .flatMap(result -> map(result));
  }

  public Single<ReviewsViewModel> loadReviewsViewModel(String storeName, String packageName,
      int maxReviews, String languagesFilterSort) {
    return reviewsManager.loadReviews(storeName, packageName, maxReviews, languagesFilterSort)
        .map(result -> new ReviewsViewModel(result.getReviewList(), result.isLoading(),
            result.getError()));
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

  public Single<Boolean> rateApp(long reviewId, boolean helpful) {
    return reviewsManager.rateApp(reviewId, helpful)
        .map(response -> (response.isOk() && response.getErrors()
            .isEmpty()));
  }

  public Single<Boolean> flagApp(String storeName, String md5, FlagsVote.VoteType type) {
    return flagManager.flagApp(storeName, md5, type.name()
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
    return adsManager.loadAdForSimilarApps(packageName, keyWords);
  }

  private Single<Boolean> isStoreFollowed(long storeId) {
    return storeManager.isSubscribed(storeId)
        .first()
        .toSingle();
  }

  private Single<AppViewViewModel> createAppViewViewModel(DetailedApp app) {
    GetAppMeta.Stats stats = app.getStats();
    cachedApp = app;
    return isStoreFollowed(cachedApp.getStore()
        .getId()).map(
        isStoreFollowed -> new AppViewViewModel(app, app.getId(), app.getName(), app.getStore(),
            appViewConfiguration.getStoreTheme(), app.isGoodApp(), app.getMalware(),
            app.getAppFlags(), app.getTags(), app.getUsedFeatures(), app.getUsedPermissions(),
            app.getFileSize(), app.getMd5(), app.getMd5Sum(), app.getPath(), app.getPathAlt(),
            app.getVerCode(), app.getVerName(), app.getPackageName(), app.getSize(),
            stats.getDownloads(), stats.getGlobalRating(), stats.getPdownloads(), stats.getRating(),
            app.getDeveloper(), app.getGraphic(), app.getIcon(), app.getMedia(), app.getModified(),
            app.getAdded(), app.getObb(), app.getPay(), app.getwUrls(), app.isPaid(),
            app.getUname(), appViewConfiguration.shouldInstall(), appViewConfiguration.getAppc(),
            appViewConfiguration.getMinimalAd(), isStoreFollowed));
  }

  private Single<AppViewViewModel> map(DetailedAppRequestResult result) {
    if (result.getDetailedApp() != null) {
      return createAppViewViewModel(result.getDetailedApp());
    } else if (result.isLoading()) {
      return Single.just(new AppViewViewModel(result.isLoading()));
    } else if (result.hasError()) {
      return Single.just(new AppViewViewModel(result.getError()));
    } else {
      return Single.just(new AppViewViewModel(DetailedAppRequestResult.Error.GENERIC));
    }
  }
}
