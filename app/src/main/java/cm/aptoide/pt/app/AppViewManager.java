package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.abtesting.ABTestManager;
import cm.aptoide.pt.abtesting.Experiment;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.appview.PreferencesManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.timeline.SocialRepository;
import cm.aptoide.pt.view.AppViewConfiguration;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.AppStats;
import cm.aptoide.pt.view.app.AppsList;
import cm.aptoide.pt.view.app.DetailedApp;
import cm.aptoide.pt.view.app.DetailedAppRequestResult;
import cm.aptoide.pt.view.app.FlagsVote;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class AppViewManager {

  private final InstallManager installManager;
  private final DownloadFactory downloadFactory;
  private final AppCenter appCenter;
  private final ReviewsManager reviewsManager;
  private final AdsManager adsManager;
  private final StoreManager storeManager;
  private final FlagManager flagManager;
  private final StoreUtilsProxy storeUtilsProxy;
  private final AptoideAccountManager aptoideAccountManager;
  private final ABTestManager abTestManager;
  private final AppViewConfiguration appViewConfiguration;
  private final int limit;
  private final InstallAnalytics installAnalytics;
  private PreferencesManager preferencesManager;
  private DownloadStateParser downloadStateParser;
  private AppViewAnalytics appViewAnalytics;
  private NotificationAnalytics notificationAnalytics;
  private DetailedApp cachedApp;
  private SearchAdResult searchAdResult;
  private SocialRepository socialRepository;
  private String marketName;
  private boolean isFirstLoad;

  public AppViewManager(InstallManager installManager, DownloadFactory downloadFactory,
      AppCenter appCenter, ReviewsManager reviewsManager, AdsManager adsManager,
      StoreManager storeManager, FlagManager flagManager, ABTestManager abTestManager,
      StoreUtilsProxy storeUtilsProxy,
      AptoideAccountManager aptoideAccountManager, AppViewConfiguration appViewConfiguration,
      PreferencesManager preferencesManager, DownloadStateParser downloadStateParser,
      AppViewAnalytics appViewAnalytics, NotificationAnalytics notificationAnalytics,
      InstallAnalytics installAnalytics, int limit, SocialRepository socialRepository,
      String marketName) {
    this.installManager = installManager;
    this.downloadFactory = downloadFactory;
    this.appCenter = appCenter;
    this.reviewsManager = reviewsManager;
    this.adsManager = adsManager;
    this.storeManager = storeManager;
    this.flagManager = flagManager;
    this.abTestManager = abTestManager;
    this.storeUtilsProxy = storeUtilsProxy;
    this.aptoideAccountManager = aptoideAccountManager;
    this.appViewConfiguration = appViewConfiguration;
    this.preferencesManager = preferencesManager;
    this.downloadStateParser = downloadStateParser;
    this.appViewAnalytics = appViewAnalytics;
    this.notificationAnalytics = notificationAnalytics;
    this.installAnalytics = installAnalytics;
    this.socialRepository = socialRepository;
    this.limit = limit;
    this.marketName = marketName;
    this.isFirstLoad = true;
  }

  public Single<AppViewViewModel> loadAppViewViewModel() {
    if (appViewConfiguration.getAppId() >= 0) {
      return loadAppViewViewModel(appViewConfiguration.getAppId(),
          appViewConfiguration.getStoreName(), appViewConfiguration.getPackageName());
    } else if (appViewConfiguration.hasMd5()) {
      return loadAppViewViewModelFromMd5(appViewConfiguration.getMd5());
    } else if (appViewConfiguration.hasUniqueName()) {
      return loadAppViewViewModelFromUniqueName(appViewConfiguration.getUniqueName());
    } else {
      return loadAppViewViewModel(appViewConfiguration.getPackageName(),
          appViewConfiguration.getStoreName());
    }
  }

  public Single<ReviewsViewModel> loadReviewsViewModel(String storeName, String packageName,
      String languagesFilterSort) {
    return reviewsManager.loadReviews(storeName, packageName, 3, languagesFilterSort)
        .map(result -> new ReviewsViewModel(result.getReviewList(), result.isLoading(),
            result.getError()));
  }

  public Single<SimilarAppsViewModel> loadSimilarApps(String packageName, List<String> keyWords) {
    return loadAdForSimilarApps(packageName, keyWords).flatMap(
        adResult -> loadRecommended(limit, packageName).map(
            recommendedAppsRequestResult -> new SimilarAppsViewModel(adResult.getMinimalAd(),
                recommendedAppsRequestResult.getList(), recommendedAppsRequestResult.isLoading(),
                recommendedAppsRequestResult.getError(), adResult.getError())));
  }

  public Single<SearchAdResult> loadAdsFromAppView() {
    return adsManager.loadAds(cachedApp.getPackageName(), cachedApp.getStore()
        .getName())
        .map(SearchAdResult::new);
  }

  public Single<Boolean> flagApk(String storeName, String md5, FlagsVote.VoteType type) {
    return flagManager.flagApk(storeName, md5, type.name()
        .toLowerCase())
        .map(response -> (response.isOk() && !response.hasErrors()));
  }

  public Completable subscribeStore(String storeName) {
    return Completable.fromAction(
        () -> storeUtilsProxy.subscribeStore(storeName, null, null, aptoideAccountManager));
  }

  private Single<AppViewViewModel> loadAppViewViewModel(long appId, String storeName,
      String packageName) {
    if (cachedApp != null) {
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

  private Single<AppViewViewModel> loadAppViewViewModelFromUniqueName(String uniqueName) {
    if (cachedApp != null && cachedApp.getUniqueName()
        .equals(uniqueName)) {
      return createAppViewViewModel(cachedApp);
    }
    return appCenter.loadDetailedAppFromUniqueName(uniqueName)
        .flatMap(result -> map(result));
  }

  private Single<AppsList> loadRecommended(int limit, String packageName) {
    return appCenter.loadRecommendedApps(limit, packageName);
  }

  private Single<MinimalAdRequestResult> loadAdForSimilarApps(String packageName,
      List<String> keyWords) {
    return adsManager.loadAd(packageName, keyWords);
  }

  private Single<Boolean> isStoreFollowed(long storeId) {
    return storeManager.isSubscribed(storeId)
        .first()
        .toSingle();
  }

  private Single<AppViewViewModel> createAppViewViewModel(DetailedApp app) {
    AppStats stats = app.getStats();
    cachedApp = app;
    return isStoreFollowed(app.getStore()
        .getId()).map(
        isStoreFollowed -> new AppViewViewModel(app.getId(), app.getName(), app.getStore(),
            appViewConfiguration.getStoreTheme(), app.isGoodApp(), app.getMalware(),
            app.getAppFlags(), app.getTags(), app.getUsedFeatures(), app.getUsedPermissions(),
            app.getFileSize(), app.getMd5(), app.getPath(), app.getPathAlt(), app.getVersionCode(),
            app.getVersionName(), app.getPackageName(), app.getSize(), stats.getDownloads(),
            stats.getGlobalRating(), stats.getPackageDownloads(), stats.getRating(),
            app.getDeveloper(), app.getGraphic(), app.getIcon(), app.getMedia(), app.getModified(),
            app.getAdded(), app.getObb(), app.getPay(), app.getWebUrls(), app.isPaid(),
            app.wasPaid(), app.getPaidAppPath(), app.getPaymentStatus(),
            app.isLatestTrustedVersion(), app.getUniqueName(), appViewConfiguration.shouldInstall(),
            appViewConfiguration.getAppc(), appViewConfiguration.getMinimalAd(),
            appViewConfiguration.getEditorsChoice(), appViewConfiguration.getOriginTag(),
            isStoreFollowed, marketName));
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

  private void increaseInstallClick() {
    preferencesManager.increaseNotLoggedInInstallClicks();
  }

  public boolean shouldShowRootInstallWarningPopup() {
    return installManager.showWarning();
  }

  public void allowRootInstall(Boolean answer) {
    installManager.rootInstallAllowed(answer);
  }

  public Completable downloadApp(DownloadAppViewModel.Action downloadAction, String packageName,
      long appId) {
    increaseInstallClick();
    return Observable.just(
        downloadFactory.create(downloadStateParser.parseDownloadAction(downloadAction),
            cachedApp.getName(), cachedApp.getPackageName(), cachedApp.getMd5(),
            cachedApp.getIcon(), cachedApp.getVersionName(), cachedApp.getVersionCode(),
            cachedApp.getPath(), cachedApp.getPathAlt(), cachedApp.getObb()))
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(__ -> setupDownloadEvents(download, packageName, appId)))
        .toCompletable();
  }

  private void setupDownloadEvents(Download download, String packageName, long appId) {
    int campaignId = notificationAnalytics.getCampaignId(packageName, appId);
    String abTestGroup = notificationAnalytics.getAbTestingGroup(packageName, appId);
    appViewAnalytics.setupDownloadEvents(download, campaignId, abTestGroup,
        AnalyticsManager.Action.CLICK);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        downloadStateParser.getInstallType(download.getAction()), AnalyticsManager.Action.INSTALL,
        AppContext.APPVIEW, downloadStateParser.getOrigin(download.getAction()), campaignId,
        abTestGroup);
  }

  public Observable<DownloadAppViewModel> loadDownloadAppViewModel(String md5, String packageName,
      int versionCode, boolean paidApp, GetAppMeta.Pay pay) {
    return installManager.getInstall(md5, packageName, versionCode)
        .map(install -> new DownloadAppViewModel(
            downloadStateParser.parseDownloadType(install.getType(), paidApp,
                pay != null && pay.isPaid()), install.getProgress(),
            downloadStateParser.parseDownloadState(install.getState()), pay));
  }

  public Completable pauseDownload(String md5) {
    return Completable.fromAction(() -> installManager.stopInstallation(md5));
  }

  public Completable resumeDownload(String md5, String packageName, long appId) {
    return installManager.getDownload(md5)
        .flatMapCompletable(download -> installManager.install(download)
            .doOnSubscribe(__ -> setupDownloadEvents(download, packageName, appId)));
  }

  public Completable cancelDownload(String md5, String packageName, int versionCode) {
    return Completable.fromAction(
        () -> installManager.removeInstallationFile(md5, packageName, versionCode));
  }

  public SearchAdResult getSearchAdResult() {
    return searchAdResult;
  }

  public void setSearchAdResult(SearchAdResult searchAdResult) {
    this.searchAdResult = searchAdResult;
  }

  public void handleAdsLogic(SearchAdResult searchAdResult) {
    adsManager.handleAdsLogic(searchAdResult);
  }

  public boolean shouldShowRecommendsPreviewDialog() {
    return preferencesManager.shouldShowInstallRecommendsPreviewDialog();
  }

  public boolean canShowNotLoggedInDialog() {
    return preferencesManager.canShowNotLoggedInDialog();
  }

  public Completable shareOnTimeline(String packageName, long storeId, String shareType) {
    return Completable.fromAction(() -> socialRepository.share(packageName, storeId, shareType));
  }

  public Completable dontShowLoggedInInstallRecommendsPreviewDialog() {
    return Completable.fromAction(
        () -> preferencesManager.setShouldShowInstallRecommendsPreviewDialog(false));
  }

  public Completable shareOnTimelineAsync(String packageName, long storeId) {
    return Completable.fromAction(() -> socialRepository.asyncShare(packageName, storeId, "app"));
  }

  public Completable appBought(String path) {
    return Completable.fromAction(() -> {
      cachedApp.getPay()
          .setPaid();
      cachedApp.setPath(path);
    });
  }

  public void sendAppViewOpenedFromEvent(String packageName, String publisher, String badge,
      double appc) {
    if (isFirstLoad) {
      appViewAnalytics.sendAppViewOpenedFromEvent(packageName, publisher, badge, appc);
      isFirstLoad = false;
    }
  }

  public void sendEditorsChoiceClickEvent(String packageName, String editorsBrickPosition) {
    if (isFirstLoad) {
      appViewAnalytics.sendEditorsChoiceClickEvent(packageName, editorsBrickPosition);
      isFirstLoad = false;
    }
  }

  public String getMarketName() {
    return marketName;
  }

  public Single<Experiment> getABTestingExperiment(ABTestManager.ExperimentType experimentType) {
    return abTestManager.getExperiment(experimentType);
  }

  public Observable<Boolean> recordABTestImpression(ABTestManager.ExperimentType experimentType) {
    return abTestManager.recordImpression(experimentType);
  }

  public Observable<Boolean> recordABTestAction(ABTestManager.ExperimentType experimentType) {
    return abTestManager.recordAction(experimentType);
  }
}
