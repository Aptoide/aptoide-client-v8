package cm.aptoide.pt.app;

import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.home.apps.UpdatesManager;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.DetailedApp;
import java.util.List;
import rx.Observable;
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

  public AppViewManager(UpdatesManager updatesManager, InstallManager installManager,
      DownloadFactory downloadFactory, AppCenter appCenter, ReviewsManager reviewsManager,
      AdsManager adsManager, StoreManager storeManager, FlagManager flagManager,
      StoreUtilsProxy storeUtilsProxy) {
    this.updatesManager = updatesManager;
    this.installManager = installManager;
    this.downloadFactory = downloadFactory;
    this.appCenter = appCenter;
    this.reviewsManager = reviewsManager;
    this.adsManager = adsManager;
    this.storeManager = storeManager;
    this.flagManager = flagManager;
    this.storeUtilsProxy = storeUtilsProxy;
  }

  public Single<DetailedAppViewModel> getDetailedAppViewModel(long appId, String packageName) {
    return appCenter.getDetailedApp(appId, packageName)
        .map(app -> new DetailedAppViewModel(app));
  }

  //TODO join with 1st app from similar apps method
  public Single<ListApps> loadRecommended(int limit, String packageName) {
    return appCenter.loadRecommendedApps(limit, packageName);
  }

  public Single<ReviewsViewModel> getReviewsViewModel(String storeName, String packageName,
      int maxReviews, String languagesFilterSort, DetailedApp detailedApp) {
    return reviewsManager.loadReviews(storeName, packageName, maxReviews, languagesFilterSort,
        detailedApp)
        .map(reviews -> new ReviewsViewModel(reviews));
  }

  public Single<AdsViewModel> getSimilarApps(String packageName, String storeName,
      List<String> keyWords) {
    return adsManager.loadSuggestedApps(packageName, keyWords)
        .map(similarApps -> new AdsViewModel(similarApps));
  }

  public Single<MinimalAd> getAd(String packageName, String storeName) {
    return adsManager.loadAd(packageName, storeName);
  }

  public Single<Boolean> addApkFlag(String storeName, String md5,
      GetAppMeta.GetAppMetaFile.Flags.Vote.Type type) {
    return flagManager.loadAddApkFlagRequest(storeName, md5, type.name()
        .toLowerCase())
        .map(response -> (response.isOk() && !response.hasErrors()));
  }

  public Observable<Boolean> isStoreFollowed(long storeId) {
    return storeManager.isSubscribed(storeId);
  }

  public Observable<Boolean> isStoreFollowed(String storeName) {
    return storeManager.isSubscribed(storeName);
  }

  public Observable<GetStoreMeta> subscribeStore(String storeName) {
    return storeUtilsProxy.subscribeStoreObservable(storeName);
  }
}
