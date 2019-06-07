package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.app.migration.AppcMigrationManager;
import cm.aptoide.pt.app.view.AppCoinsViewModel;
import cm.aptoide.pt.app.view.donations.Donation;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.notification.AppcPromotionNotificationStringProvider;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.sync.LocalNotificationSync;
import cm.aptoide.pt.notification.sync.LocalNotificationSyncManager;
import cm.aptoide.pt.promotions.Promotion;
import cm.aptoide.pt.promotions.PromotionsManager;
import cm.aptoide.pt.promotions.WalletApp;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.store.StoreUtilsProxy;
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
  private final StoreManager storeManager;
  private final FlagManager flagManager;
  private final StoreUtilsProxy storeUtilsProxy;
  private final AptoideAccountManager aptoideAccountManager;
  private final AppViewConfiguration appViewConfiguration;
  private final int limit;
  private final InstallAnalytics installAnalytics;
  private final InstalledRepository installedRepository;
  private final MoPubAdsManager moPubAdsManager;
  private final Scheduler ioScheduler;
  private DownloadStateParser downloadStateParser;
  private AppViewAnalytics appViewAnalytics;
  private NotificationAnalytics notificationAnalytics;
  private DetailedApp cachedApp;
  private SearchAdResult searchAdResult;
  private String marketName;
  private boolean isFirstLoad;
  private AppCoinsManager appCoinsManager;
  private AppcMigrationManager appcMigrationManager;
  private AppCoinsViewModel cachedAppCoinsViewModel;
  private SimilarAppsViewModel cachedSimilarAppsViewModel;
  private SimilarAppsViewModel cachedAppcSimilarAppsViewModel;
  private LocalNotificationSyncManager localNotificationSyncManager;
  private AppcPromotionNotificationStringProvider appcPromotionNotificationStringProvider;
  private boolean appcPromotionImpressionSent;
  private boolean migrationImpressionSent;
  private PromotionViewModel cachedPromotionViewModel;

  public AppViewManager(InstallManager installManager, DownloadFactory downloadFactory,
      AppCenter appCenter, ReviewsManager reviewsManager, AdsManager adsManager,
      StoreManager storeManager, FlagManager flagManager, StoreUtilsProxy storeUtilsProxy,
      AptoideAccountManager aptoideAccountManager, AppViewConfiguration appViewConfiguration,
      MoPubAdsManager moPubAdsManager, DownloadStateParser downloadStateParser,
      AppViewAnalytics appViewAnalytics, NotificationAnalytics notificationAnalytics,
      InstallAnalytics installAnalytics, int limit, Scheduler ioScheduler, String marketName,
      AppCoinsManager appCoinsManager, PromotionsManager promotionsManager,
      InstalledRepository installedRepository, AppcMigrationManager appcMigrationManager,
      LocalNotificationSyncManager localNotificationSyncManager,
      AppcPromotionNotificationStringProvider appcPromotionNotificationStringProvider) {
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
    this.installedRepository = installedRepository;
    this.appcMigrationManager = appcMigrationManager;
    this.localNotificationSyncManager = localNotificationSyncManager;
    this.appcPromotionNotificationStringProvider = appcPromotionNotificationStringProvider;
    this.isFirstLoad = true;
    this.appcPromotionImpressionSent = false;
    this.migrationImpressionSent = false;
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
    return adsManager.loadAds(cachedApp.getPackageName(), cachedApp.getStore()
        .getName())
        .map(SearchAdResult::new);
  }

  public Single<Boolean> recordInterstitialImpression() {
    return moPubAdsManager.recordInterstitialAdImpression()
        .subscribeOn(ioScheduler);
  }

  public Single<Boolean> recordInterstitialClick() {
    return moPubAdsManager.recordInterstitialAdClick();
  }

  public Observable<DownloadAppViewModel> loadDownloadAppViewModel(String md5, String packageName,
      int versionCode, boolean paidApp, GetAppMeta.Pay pay, String signature, long storeId,
      boolean hasAppc) {
    return loadDownloadModel(md5, packageName, versionCode, paidApp, pay, signature, storeId,
        hasAppc).map(
        downloadModel -> new DownloadAppViewModel(downloadModel, cachedSimilarAppsViewModel,
            cachedAppcSimilarAppsViewModel, cachedAppCoinsViewModel));
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
            isStoreFollowed, marketName, app.hasBilling(), app.hasAdvertising(), app.getBdsFlags(),
            appViewConfiguration.getCampaignUrl(), app.getSignature()));
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

  public boolean shouldShowRootInstallWarningPopup() {
    return installManager.showWarning();
  }

  public void allowRootInstall(Boolean answer) {
    installManager.rootInstallAllowed(answer);
  }

  public Completable downloadApp(DownloadModel.Action downloadAction, long appId,
      String trustedValue, String editorsChoicePosition) {
    return Observable.just(
        downloadFactory.create(downloadStateParser.parseDownloadAction(downloadAction),
            cachedApp.getName(), cachedApp.getPackageName(), cachedApp.getMd5(),
            cachedApp.getIcon(), cachedApp.getVersionName(), cachedApp.getVersionCode(),
            cachedApp.getPath(), cachedApp.getPathAlt(), cachedApp.getObb(),
            cachedApp.hasAdvertising() || cachedApp.hasBilling(), cachedApp.getSize()))
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
        false, walletApp.getSize()))
        .flatMapSingle(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(offerResponseStatus -> setupDownloadEvents(download,
                walletApp.getDownloadModel()
                    .getAction(), walletApp.getId(), offerResponseStatus))
            .map(__ -> download))
        .flatMapCompletable(download -> installManager.install(download))
        .toCompletable();
  }

  private void setupDownloadEvents(Download download, long appId,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus) {
    setupDownloadEvents(download, null, appId, null, null, offerResponseStatus);
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
        downloadAction != null && downloadAction.equals(DownloadModel.Action.MIGRATE));
  }

  public void setupMigratorUninstallEvent(String packageName) {
    installAnalytics.uninstallStarted(packageName, AnalyticsManager.Action.INSTALL,
        AppContext.APPVIEW);
  }

  public Observable<DownloadModel> loadDownloadModel(String md5, String packageName,
      int versionCode, boolean paidApp, GetAppMeta.Pay pay, String signature, long storeId,
      boolean hasAppc) {
    return Observable.combineLatest(installManager.getInstall(md5, packageName, versionCode),
        appcMigrationManager.isMigrationApp(packageName, signature, versionCode, storeId, hasAppc),
        (install, isMigration) -> new DownloadModel(
            downloadStateParser.parseDownloadType(install.getType(), paidApp,
                pay != null && pay.isPaid(), isMigration), install.getProgress(),
            downloadStateParser.parseDownloadState(install.getState()), pay));
  }

  public Completable pauseDownload(String md5) {
    return Completable.fromAction(() -> installManager.stopInstallation(md5));
  }

  public Completable resumeDownload(String md5, long appId) {
    return installManager.getDownload(md5)
        .flatMap(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(
                offerResponseStatus -> setupDownloadEvents(download, appId, offerResponseStatus))
            .map(__ -> download))
        .flatMapCompletable(download -> installManager.install(download));
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

  private boolean isAppcApp() {
    return cachedAppCoinsViewModel != null && (cachedAppCoinsViewModel.hasAdvertising()
        || cachedAppCoinsViewModel.hasBilling());
  }

  public Completable appBought(String path) {
    return Completable.fromAction(() -> {
      cachedApp.getPay()
          .setPaid();
      cachedApp.setPath(path);
    });
  }

  public void sendAppViewOpenedFromEvent(String packageName, String publisher, String badge,
      boolean hasBilling, boolean hasAdvertising) {
    if (isFirstLoad) {
      appViewAnalytics.sendAppViewOpenedFromEvent(packageName, publisher, badge, hasBilling,
          hasAdvertising);
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

  @SuppressWarnings("unused") public Completable loadAppCoinsInformation() {
    if (cachedAppCoinsViewModel == null) {
      return Completable.fromObservable(Observable.fromCallable(() -> cachedApp)
          .flatMapCompletable(app -> {
            if (app.hasAdvertising()) {
              return appCoinsManager.hasAdvertising(app.getPackageName(), app.getVersionCode())
                  .map(hasAdvertising -> cachedAppCoinsViewModel =
                      new AppCoinsViewModel(false, app.hasBilling(), hasAdvertising))
                  .toCompletable();
            } else {
              cachedAppCoinsViewModel = new AppCoinsViewModel(false, app.hasBilling(), false);
            }
            return Completable.complete();
          }));
    }
    return Completable.complete();
  }

  public Single<List<Donation>> getTopDonations(String packageName) {
    return appCoinsManager.getDonationsList(packageName);
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
        .flatMap(shouldLoadAd -> Single.just(shouldLoadAd
            && !cachedAppCoinsViewModel.hasBilling()
            && !cachedAppCoinsViewModel.hasAdvertising()
            && !cachedApp.isMature()));
  }

  private void sendAdsBlockByOfferEvent() {
    appViewAnalytics.sendAdsBlockByOfferEvent();
  }

  public Single<Boolean> shouldLoadBannerAd() {
    return moPubAdsManager.shouldLoadBannerAd()
        .flatMap(shouldLoadAd -> Single.just(shouldLoadAd
            && !cachedAppCoinsViewModel.hasBilling()
            && !cachedAppCoinsViewModel.hasAdvertising()
            && !cachedApp.isMature()));
  }

  public Single<Boolean> shouldLoadNativeAds() {
    return moPubAdsManager.shouldLoadNativeAds()
        .flatMap(shouldLoadAd -> Single.just(shouldLoadAd
            && !cachedAppCoinsViewModel.hasBilling()
            && !cachedAppCoinsViewModel.hasAdvertising()
            && !cachedApp.isMature()));
  }

  public Observable<DownloadModel> appViewAppDownloadModel() {
    return loadAppViewViewModel().toObservable()
        .flatMap(app -> loadDownloadModel(app.getMd5(), app.getPackageName(), app.getVersionCode(),
            app.isPaid(), app.getPay(), app.getSignature(), app.getStore()
                .getId(), app.hasAdvertising() || app.hasBilling()));
  }

  public Observable<PromotionViewModel> loadPromotionViewModel() {
    Observable<PromotionViewModel> promoViewModelObs = Observable.just(new PromotionViewModel());
    if (cachedPromotionViewModel != null) {
      Observable<DownloadModel> downloadModel = appViewAppDownloadModel();
      Observable<Boolean> isAppMigrated =
          appcMigrationManager.isAppMigrated(cachedApp.getPackageName());
      Observable<PromotionViewModel> cachedViewModel = Observable.just(cachedPromotionViewModel);
      return Observable.combineLatest(cachedViewModel, downloadModel, isAppMigrated,
          this::mergeToCachedPromotionViewModel);
    } else {
      return promotionsManager.getPromotionsForPackage(cachedApp.getPackageName())
          .filter(promotions -> !promotions.isEmpty())
          .flatMap(promotionList -> {
            Observable<List<Promotion>> promObs = Observable.just(promotionList);
            Observable<WalletApp> walletApp = loadWallet();
            Observable<DownloadModel> appDownloadModel = appViewAppDownloadModel();
            Observable<DetailedApp> appViewModel = Observable.just(cachedApp);
            Observable<Boolean> isAppMigrated =
                appcMigrationManager.isAppMigrated(cachedApp.getPackageName());
            return Observable.combineLatest(promObs, walletApp, appDownloadModel, appViewModel,
                isAppMigrated,
                (proms, wallet, appDLM, appVM, migrate) -> mergeToPromotionViewModel(wallet, proms,
                    appDLM, appVM, migrate));
          })
          .doOnNext(promotionViewModel -> cachedPromotionViewModel = promotionViewModel)
          .switchIfEmpty(promoViewModelObs);
    }
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
      DownloadModel appDownloadModel, Boolean isMigrated) {
    cached.setAppMigrated(isMigrated);
    cached.setAppDownloadModel(appDownloadModel);
    return cached;
  }

  private PromotionViewModel mergeToPromotionViewModel(WalletApp walletApp,
      List<Promotion> promotions, DownloadModel appDownloadModel, DetailedApp app,
      Boolean isAppMigrated) {
    return new PromotionViewModel(walletApp, promotions, appDownloadModel, app, isAppMigrated);
  }

  private Observable<WalletApp> loadWallet() {
    return appCenter.loadDetailedApp("com.appcoins.wallet", "bds-store")
        .toObservable()
        .map(this::mapToWalletApp)
        .flatMap(walletApp -> {
          Observable<WalletApp> walletAppObs = Observable.just(walletApp);
          Observable<Boolean> isWalletInstalled =
              installedRepository.isInstalled(walletApp.getPackageName());
          Observable<Install> walletDownload =
              installManager.getInstall(walletApp.getMd5sum(), walletApp.getPackageName(),
                  walletApp.getVersionCode());
          return Observable.combineLatest(walletAppObs, isWalletInstalled, walletDownload,
              this::mergeToWalletApp);
        });
  }

  private WalletApp mergeToWalletApp(WalletApp walletApp, Boolean isInstalled,
      Install walletDownload) {
    DownloadModel downloadModel =
        mapToDownloadModel(walletDownload.getType(), walletDownload.getProgress(),
            walletDownload.getState());
    walletApp.setDownloadModel(downloadModel);
    walletApp.setInstalled(isInstalled);
    return walletApp;
  }

  private WalletApp mapToWalletApp(DetailedAppRequestResult result) {
    if (result.hasError() || result.isLoading()) return new WalletApp();
    DetailedApp app = result.getDetailedApp();
    return new WalletApp(null, false, app.getName(), app.getIcon(), app.getId(),
        app.getPackageName(), app.getMd5(), app.getVersionCode(), app.getVersionName(),
        app.getPath(), app.getPathAlt(), app.getObb(), app.getSize());
  }

  private DownloadModel mapToDownloadModel(Install.InstallationType type, int progress,
      Install.InstallationStatus state) {
    return new DownloadModel(downloadStateParser.parseDownloadType(type, false, false, false),
        progress, downloadStateParser.parseDownloadState(state), null);
  }

  public SimilarAppsViewModel getCachedAppcSimilarAppsViewModel() {
    return cachedAppcSimilarAppsViewModel;
  }

  public Observable<DownloadAppViewModel> downloadStarted() {
    return loadAppViewViewModel().toObservable()
        .filter(app -> !app.isLoading())
        .flatMap(app -> loadDownloadAppViewModel(app.getMd5(), app.getPackageName(),
            app.getVersionCode(), app.isPaid(), app.getPay(), app.getSignature(), app.getStore()
                .getId(), app.hasAdvertising() || app.hasBilling()))
        .filter(download -> download.getDownloadModel()
            .isDownloading());
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