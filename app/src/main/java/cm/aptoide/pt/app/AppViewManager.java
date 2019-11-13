package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.app.migration.AppcMigrationManager;
import cm.aptoide.pt.app.view.donations.Donation;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.notification.AppcPromotionNotificationStringProvider;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.sync.LocalNotificationSync;
import cm.aptoide.pt.notification.sync.LocalNotificationSyncManager;
import cm.aptoide.pt.promotions.Promotion;
import cm.aptoide.pt.promotions.PromotionsManager;
import cm.aptoide.pt.promotions.WalletApp;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.AppsList;
import cm.aptoide.pt.view.app.FlagsVote;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

/**
 * Created by D01 on 04/05/18.
 */

public class AppViewManager {

  private final InstallManager installManager;
  private final DownloadFactory downloadFactory;
  private final AppCenter appCenter;
  private final ReviewsManager reviewsManager;
  private final PromotionsManager promotionsManager;
  private final AdsManager adsManager;
  private final FlagManager flagManager;
  private final StoreUtilsProxy storeUtilsProxy;
  private final AptoideAccountManager aptoideAccountManager;
  private final int limit;
  private final InstallAnalytics installAnalytics;
  private final MoPubAdsManager moPubAdsManager;
  private final Scheduler ioScheduler;
  private DownloadStateParser downloadStateParser;
  private AppViewAnalytics appViewAnalytics;
  private NotificationAnalytics notificationAnalytics;
  private SearchAdResult searchAdResult;
  private String marketName;
  private boolean isFirstLoad;
  private AppCoinsManager appCoinsManager;
  private AppcMigrationManager appcMigrationManager;

  private LocalNotificationSyncManager localNotificationSyncManager;
  private AppcPromotionNotificationStringProvider appcPromotionNotificationStringProvider;
  private boolean appcPromotionImpressionSent;
  private boolean migrationImpressionSent;

  private AppViewModelManager appViewModelManager;

  private SimilarAppsViewModel cachedSimilarAppsViewModel;
  private SimilarAppsViewModel cachedAppcSimilarAppsViewModel;
  private PromotionViewModel cachedPromotionViewModel;

  public AppViewManager(AppViewModelManager appViewModelManager, InstallManager installManager,
      DownloadFactory downloadFactory, AppCenter appCenter, ReviewsManager reviewsManager,
      AdsManager adsManager, FlagManager flagManager, StoreUtilsProxy storeUtilsProxy,
      AptoideAccountManager aptoideAccountManager, MoPubAdsManager moPubAdsManager,
      DownloadStateParser downloadStateParser, AppViewAnalytics appViewAnalytics,
      NotificationAnalytics notificationAnalytics, InstallAnalytics installAnalytics, int limit,
      Scheduler ioScheduler, String marketName, AppCoinsManager appCoinsManager,
      PromotionsManager promotionsManager, AppcMigrationManager appcMigrationManager,
      LocalNotificationSyncManager localNotificationSyncManager,
      AppcPromotionNotificationStringProvider appcPromotionNotificationStringProvider) {
    this.appViewModelManager = appViewModelManager;
    this.installManager = installManager;
    this.downloadFactory = downloadFactory;
    this.appCenter = appCenter;
    this.reviewsManager = reviewsManager;
    this.adsManager = adsManager;
    this.flagManager = flagManager;
    this.storeUtilsProxy = storeUtilsProxy;
    this.aptoideAccountManager = aptoideAccountManager;
    this.moPubAdsManager = moPubAdsManager;
    this.downloadStateParser = downloadStateParser;
    this.appViewAnalytics = appViewAnalytics;
    this.notificationAnalytics = notificationAnalytics;
    this.installAnalytics = installAnalytics;
    this.ioScheduler = ioScheduler;
    this.limit = limit;
    this.marketName = marketName;
    this.appCoinsManager = appCoinsManager;
    this.promotionsManager = promotionsManager;
    this.appcMigrationManager = appcMigrationManager;
    this.localNotificationSyncManager = localNotificationSyncManager;
    this.appcPromotionNotificationStringProvider = appcPromotionNotificationStringProvider;
    this.isFirstLoad = true;
    this.appcPromotionImpressionSent = false;
    this.migrationImpressionSent = false;
  }

  public Observable<AppViewModel> observeAppViewModel() {
    return appViewModelManager.observeAppViewModel();
  }

  public Single<AppViewModel> getAppViewModel() {
    return appViewModelManager.getAppViewModel();
  }

  public Single<AppModel> getAppModel() {
    return appViewModelManager.getAppModel();
  }

  public Single<ReviewsViewModel> loadReviewsViewModel(String storeName, String packageName,
      String languagesFilterSort) {
    return reviewsManager.loadReviews(storeName, packageName, 3, languagesFilterSort)
        .map(result -> new ReviewsViewModel(result.getReviewList(), result.isLoading(),
            result.getError()));
  }

  public Single<SimilarAppsViewModel> loadAppcSimilarAppsViewModel(String packageName,
      List<String> keyWords) {
    if (cachedAppcSimilarAppsViewModel != null) {
      return Single.just(cachedAppcSimilarAppsViewModel);
    } else {
      return loadAppcRecommended(limit, packageName).map(recommendedAppsRequestResult -> {
        cachedAppcSimilarAppsViewModel =
            new SimilarAppsViewModel(null, recommendedAppsRequestResult.getList(),
                recommendedAppsRequestResult.isLoading(), recommendedAppsRequestResult.getError(),
                null);
        return cachedAppcSimilarAppsViewModel;
      });
    }
  }

  public Single<SimilarAppsViewModel> loadSimilarAppsViewModel(String packageName,
      List<String> keyWords) {
    if (cachedSimilarAppsViewModel != null) {
      return Single.just(cachedSimilarAppsViewModel);
    } else {
      return adsManager.loadAd(packageName, keyWords)
          .flatMap(
              adResult -> loadRecommended(limit, packageName).map(recommendedAppsRequestResult -> {
                cachedSimilarAppsViewModel = new SimilarAppsViewModel(adResult.getAd(),
                    recommendedAppsRequestResult.getList(),
                    recommendedAppsRequestResult.isLoading(),
                    recommendedAppsRequestResult.getError(), adResult.getError());
                return cachedSimilarAppsViewModel;
              }));
    }
  }

  public Single<SimilarAppsViewModel> loadAptoideSimilarAppsViewModel(String packageName,
      List<String> keyWords) {
    if (cachedSimilarAppsViewModel != null) {
      return Single.just(cachedSimilarAppsViewModel);
    } else {
      return loadAdForSimilarApps(packageName, keyWords).flatMap(
          adResult -> loadRecommended(limit, packageName).map(recommendedAppsRequestResult -> {
            ApplicationAd applicationAd = null;
            if (adResult.getMinimalAd() != null) {
              applicationAd = new AptoideNativeAd(adResult.getMinimalAd());
            }
            cachedSimilarAppsViewModel =
                new SimilarAppsViewModel(applicationAd, recommendedAppsRequestResult.getList(),
                    recommendedAppsRequestResult.isLoading(),
                    recommendedAppsRequestResult.getError(), adResult.getError());
            return cachedSimilarAppsViewModel;
          }));
    }
  }

  public Single<SearchAdResult> loadAdsFromAppView() {
    return getAppModel().flatMap(appModel -> adsManager.loadAds(appModel.getPackageName(),
        appModel.getStore()
            .getName())
        .map(SearchAdResult::new));
  }

  public Single<Boolean> recordInterstitialImpression() {
    return moPubAdsManager.recordInterstitialAdImpression()
        .subscribeOn(ioScheduler);
  }

  public Single<Boolean> recordInterstitialClick() {
    return moPubAdsManager.recordInterstitialAdClick();
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

  private Single<AppsList> loadRecommended(int limit, String packageName) {
    return appCenter.loadRecommendedApps(limit, packageName);
  }

  private Single<AppsList> loadAppcRecommended(int limit, String packageName) {
    return appCenter.loadAppcRecommendedApps(limit, packageName);
  }

  private Single<MinimalAdRequestResult> loadAdForSimilarApps(String packageName,
      List<String> keyWords) {
    return adsManager.loadAd(packageName, keyWords);
  }

  public SimilarAppsViewModel getCachedSimilarAppsViewModel() {
    return cachedSimilarAppsViewModel;
  }

  public boolean shouldShowRootInstallWarningPopup() {
    return installManager.showWarning();
  }

  public void allowRootInstall(Boolean answer) {
    installManager.rootInstallAllowed(answer);
  }

  public Completable downloadApp(DownloadModel.Action downloadAction, long appId,
      String trustedValue, String editorsChoicePosition) {
    return getAppModel().flatMapObservable(app -> Observable.just(
        downloadFactory.create(downloadStateParser.parseDownloadAction(downloadAction),
            app.getAppName(), app.getPackageName(), app.getMd5(), app.getIcon(),
            app.getVersionName(), app.getVersionCode(), app.getPath(), app.getPathAlt(),
            app.getObb(), app.hasAdvertising() || app.hasBilling(), app.getSize(), app.getSplits(),
            app.getRequiredSplits())))
        .flatMapSingle(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(status -> {
              setupDownloadEvents(download, downloadAction, appId, trustedValue,
                  editorsChoicePosition, status);
              if (DownloadModel.Action.MIGRATE.equals(downloadAction)) {
                setupMigratorUninstallEvent(download.getPackageName());
              }
            })
            .map(__ -> download))
        .doOnNext(download -> {
          if (downloadAction == DownloadModel.Action.MIGRATE) {
            appcMigrationManager.addMigrationCandidate(download.getPackageName());
          }
        })
        .flatMapCompletable(download -> installManager.install(download))
        .toCompletable();
  }

  public Completable downloadApp(WalletApp walletApp) {
    return Observable.just(downloadFactory.create(downloadStateParser.parseDownloadAction(
        walletApp.getDownloadModel()
            .getAction()), walletApp.getAppName(), walletApp.getPackageName(),
        walletApp.getMd5sum(), walletApp.getIcon(), walletApp.getVersionName(),
        walletApp.getVersionCode(), walletApp.getPath(), walletApp.getPathAlt(), walletApp.getObb(),
        false, walletApp.getSize(), walletApp.getSplits(), walletApp.getRequiredSplits()))
        .flatMapSingle(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(offerResponseStatus -> setupDownloadEvents(download,
                walletApp.getDownloadModel()
                    .getAction(), walletApp.getId(), offerResponseStatus))
            .map(__ -> download))
        .flatMapCompletable(download -> installManager.install(download))
        .toCompletable();
  }

  private void setupDownloadEvents(Download download, DownloadModel.Action downloadAction,
      long appId, WalletAdsOfferManager.OfferResponseStatus offerResponseStatus) {
    setupDownloadEvents(download, downloadAction, appId, null, null, offerResponseStatus);
  }

  private void setupDownloadEvents(Download download, DownloadModel.Action downloadAction,
      long appId, String malwareRank, String editorsChoice,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus) {
    int campaignId = notificationAnalytics.getCampaignId(download.getPackageName(), appId);
    String abTestGroup = notificationAnalytics.getAbTestingGroup(download.getPackageName(), appId);
    appViewAnalytics.setupDownloadEvents(download, campaignId, abTestGroup, downloadAction,
        AnalyticsManager.Action.CLICK, malwareRank, editorsChoice, offerResponseStatus);
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, AppContext.APPVIEW,
        downloadStateParser.getOrigin(download.getAction()), campaignId, abTestGroup,
        downloadAction != null && downloadAction.equals(DownloadModel.Action.MIGRATE),
        download.hasAppc(), download.hasSplits());
  }

  public void setupMigratorUninstallEvent(String packageName) {
    installAnalytics.uninstallStarted(packageName, AnalyticsManager.Action.INSTALL,
        AppContext.APPVIEW);
  }

  public Observable<DownloadModel> loadDownloadModel(String md5, String packageName,
      int versionCode, String signature, long storeId, boolean hasAppc) {
    return Observable.combineLatest(installManager.getInstall(md5, packageName, versionCode),
        appcMigrationManager.isMigrationApp(packageName, signature, versionCode, storeId, hasAppc),
        (install, isMigration) -> new DownloadModel(
            downloadStateParser.parseDownloadType(install.getType(), isMigration),
            install.getProgress(), downloadStateParser.parseDownloadState(install.getState())));
  }

  public Completable pauseDownload(String md5) {
    return installManager.stopInstallation(md5);
  }

  public Completable resumeDownload(String md5, long appId, DownloadModel.Action action) {
    return installManager.getDownload(md5)
        .flatMap(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(offerResponseStatus -> setupDownloadEvents(download, action, appId,
                offerResponseStatus))
            .map(__ -> download))
        .doOnError(throwable -> throwable.printStackTrace())
        .flatMapCompletable(download -> installManager.install(download));
  }

  public Completable cancelDownload(String md5, String packageName, int versionCode) {
    return installManager.removeInstallationFile(md5, packageName, versionCode);
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

  public void sendAppOpenAnalytics(String packageName, String publisher, String badge,
      boolean hasBilling, boolean hasAdvertising) {
    if (isFirstLoad) {
      appViewAnalytics.sendAppViewOpenedFromEvent(packageName, publisher, badge, hasBilling,
          hasAdvertising);
      isFirstLoad = false;
    }
  }

  public void sendEditorsAppOpenAnalytics(String packageName, String publisher, String badge,
      boolean hasBilling, boolean hasAdvertising, String editorsBrickPosition) {
    if (isFirstLoad) {
      appViewAnalytics.sendAppViewOpenedFromEvent(packageName, publisher, badge, hasBilling,
          hasAdvertising);
      appViewAnalytics.sendEditorsChoiceClickEvent(packageName, editorsBrickPosition);
      isFirstLoad = false;
    }
  }

  public String getMarketName() {
    return marketName;
  }

  public Single<List<Donation>> getTopDonations(String packageName) {
    return appCoinsManager.getDonationsList(packageName);
  }

  private Single<Boolean> shouldLoadAds(boolean shouldLoad) {
    return appViewModelManager.getAppViewModel()
        .flatMap(appViewModel -> Single.just(shouldLoad && !appViewModel.getAppCoinsViewModel()
            .hasBilling() && !appViewModel.getAppCoinsViewModel()
            .hasAdvertising() && !appViewModel.getAppModel()
            .isMature()));
  }

  public Single<Boolean> shouldLoadInterstitialAd() {
    return moPubAdsManager.shouldHaveInterstitialAds()
        .flatMap(hasAds -> {
          if (hasAds) {
            return moPubAdsManager.shouldShowAds()
                .doOnSuccess(showAds -> {
                  if (!showAds) {
                    sendAdsBlockByOfferEvent();
                  }
                });
          } else {
            return Single.just(false);
          }
        })
        .flatMap(this::shouldLoadAds);
  }

  private void sendAdsBlockByOfferEvent() {
    appViewAnalytics.sendAdsBlockByOfferEvent();
  }

  public Single<Boolean> shouldLoadBannerAd() {
    return moPubAdsManager.shouldLoadBannerAd()
        .flatMap(this::shouldLoadAds);
  }

  public Single<Boolean> shouldLoadNativeAds() {
    return moPubAdsManager.shouldLoadNativeAds()
        .flatMap(this::shouldLoadAds);
  }

  public Observable<PromotionViewModel> loadPromotionViewModel() {
    Observable<PromotionViewModel> promoViewModelObs = Observable.just(new PromotionViewModel());
    if (cachedPromotionViewModel != null) {
      Observable<PromotionViewModel> cachedViewModel = Observable.just(cachedPromotionViewModel);
      Observable<AppViewModel> appViewModel = observeAppViewModel();
      return Observable.combineLatest(cachedViewModel, appViewModel,
          this::mergeToCachedPromotionViewModel);
    } else {
      return getPromotions().filter(promotions -> !promotions.isEmpty())
          .flatMap(promotionList -> {
            Observable<List<Promotion>> promObs = Observable.just(promotionList);
            Observable<WalletApp> walletApp = promotionsManager.getWalletApp();
            Observable<AppViewModel> appViewModel = observeAppViewModel();
            return Observable.combineLatest(promObs, walletApp, appViewModel,
                (proms, wallet, appVM) -> mergeToPromotionViewModel(wallet, proms, appVM));
          })
          .doOnNext(promotionViewModel -> cachedPromotionViewModel = promotionViewModel)
          .switchIfEmpty(promoViewModelObs);
    }
  }

  private Observable<List<Promotion>> getPromotions() {
    return appViewModelManager.getAppModel()
        .flatMapObservable(
            appModel -> promotionsManager.getPromotionsForPackage(appModel.getPackageName()));
  }

  /**
   * If the user clicks before cachedPromotionViewModel is set, this will return false, even though
   * it might be true later. We could call loadPromotionViewModel(), but in this case it will do a
   * lot of work twice. I think it's acceptable to assume it's false if the promotion wasn't shown
   * yet.
   */
  public boolean hasClaimablePromotion(Promotion.ClaimAction action) {
    return cachedPromotionViewModel != null
        && getClaimablePromotion(cachedPromotionViewModel.getPromotions(), action) != null;
  }

  public Promotion getClaimablePromotion(List<Promotion> promotions,
      Promotion.ClaimAction claimAction) {
    return promotionsManager.getClaimablePromotion(promotions, claimAction);
  }

  private PromotionViewModel mergeToCachedPromotionViewModel(PromotionViewModel cached,
      AppViewModel appViewModel) {
    cached.setAppViewModel(appViewModel);
    return cached;
  }

  private PromotionViewModel mergeToPromotionViewModel(WalletApp walletApp,
      List<Promotion> promotions, AppViewModel appViewModel) {
    return new PromotionViewModel(walletApp, promotions, appViewModel);
  }

  public SimilarAppsViewModel getCachedAppcSimilarAppsViewModel() {
    return cachedAppcSimilarAppsViewModel;
  }

  public Observable<DownloadModel> downloadStarted() {
    return appViewModelManager.observeAppViewModel()
        .flatMap(appViewModel -> Observable.just(appViewModel.getDownloadModel()))
        .filter(download -> download.isDownloading());
  }

  public boolean isAppcPromotionImpressionSent() {
    return appcPromotionImpressionSent;
  }

  public void setAppcPromotionImpressionSent() {
    this.appcPromotionImpressionSent = true;
  }

  public boolean isMigrationImpressionSent() {
    return migrationImpressionSent;
  }

  public void setMigrationImpressionSent() {
    this.migrationImpressionSent = true;
  }

  public void scheduleNotification(String appcValue, String image, String packageName,
      String storeName) {
    localNotificationSyncManager.schedule(
        String.format(appcPromotionNotificationStringProvider.getNotificationTitle(), appcValue),
        appcPromotionNotificationStringProvider.getNotificationBody(), image,
        "aptoideinstall://package="
            + packageName
            + "&store="
            + storeName
            + "&show_install_popup=false", LocalNotificationSync.APPC_CAMPAIGN_NOTIFICATION);
  }

  public void unscheduleNotificationSync() {
    localNotificationSyncManager.unschedule(LocalNotificationSync.APPC_CAMPAIGN_NOTIFICATION);
  }

  public Single<Boolean> shouldShowConsentDialog() {
    return moPubAdsManager.shouldShowConsentDialog();
  }
}