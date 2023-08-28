package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.AppCoinsManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.aab.DynamicSplitsManager;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.app.appsflyer.AppsFlyerManager;
import cm.aptoide.pt.app.migration.AppcMigrationManager;
import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.download.DownloadAnalytics;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.InvalidAppException;
import cm.aptoide.pt.download.SplitAnalyticsMapper;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.notification.AppcPromotionNotificationStringProvider;
import cm.aptoide.pt.notification.AptoideNotification;
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
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import java.util.List;
import org.jetbrains.annotations.NotNull;
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
  private final PromotionsManager promotionsManager;
  private final AdsManager adsManager;
  private final FlagManager flagManager;
  private final StoreUtilsProxy storeUtilsProxy;
  private final AptoideAccountManager aptoideAccountManager;
  private final int limit;
  private final InstallAnalytics installAnalytics;
  private final MoPubAdsManager moPubAdsManager;
  private final DownloadStateParser downloadStateParser;
  private final AppViewAnalytics appViewAnalytics;
  private final NotificationAnalytics notificationAnalytics;
  private final String marketName;
  private final AppCoinsManager appCoinsManager;
  private final AppcMigrationManager appcMigrationManager;
  private final LocalNotificationSyncManager localNotificationSyncManager;
  private final AppcPromotionNotificationStringProvider appcPromotionNotificationStringProvider;
  private final AppViewModelManager appViewModelManager;
  private final DynamicSplitsManager dynamicSplitsManager;
  private final SplitAnalyticsMapper splitAnalyticsMapper;
  private SearchAdResult searchAdResult;
  private boolean isFirstLoad;
  private boolean appcPromotionImpressionSent;
  private boolean migrationImpressionSent;
  private SimilarAppsViewModel cachedSimilarAppsViewModel;
  private SimilarAppsViewModel cachedAppcSimilarAppsViewModel;
  private PromotionViewModel cachedPromotionViewModel;

  private AppsFlyerManager appsFlyerManager;

  public AppViewManager(AppViewModelManager appViewModelManager, InstallManager installManager,
      DownloadFactory downloadFactory, AppCenter appCenter, ReviewsManager reviewsManager,
      AdsManager adsManager, FlagManager flagManager, StoreUtilsProxy storeUtilsProxy,
      AptoideAccountManager aptoideAccountManager, MoPubAdsManager moPubAdsManager,
      DownloadStateParser downloadStateParser, AppViewAnalytics appViewAnalytics,
      NotificationAnalytics notificationAnalytics, InstallAnalytics installAnalytics, int limit,
      String marketName, AppCoinsManager appCoinsManager, PromotionsManager promotionsManager,
      AppcMigrationManager appcMigrationManager,
      LocalNotificationSyncManager localNotificationSyncManager,
      AppcPromotionNotificationStringProvider appcPromotionNotificationStringProvider,
      DynamicSplitsManager dynamicSplitsManager, SplitAnalyticsMapper splitAnalyticsMapper,
      AppsFlyerManager appsFlyerManager) {
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
    this.limit = limit;
    this.marketName = marketName;
    this.appCoinsManager = appCoinsManager;
    this.promotionsManager = promotionsManager;
    this.appcMigrationManager = appcMigrationManager;
    this.localNotificationSyncManager = localNotificationSyncManager;
    this.appcPromotionNotificationStringProvider = appcPromotionNotificationStringProvider;
    this.dynamicSplitsManager = dynamicSplitsManager;
    this.splitAnalyticsMapper = splitAnalyticsMapper;
    this.appsFlyerManager = appsFlyerManager;
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

  public Single<SimilarAppsViewModel> loadAppcSimilarAppsViewModel(String packageName) {
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

  public Single<SearchAdResult> loadAdsFromAppView() {
    return getAppModel().flatMap(appModel -> adsManager.loadAds(appModel.getPackageName(),
            appModel.getStore()
                .getName())
        .map(SearchAdResult::new));
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
      String trustedValue, String editorsChoicePosition,
      WalletAdsOfferManager.OfferResponseStatus status, boolean isApkfy) {
    return getAppModel().flatMapObservable(app -> Observable.just(app)
            .flatMapSingle(
                __ -> RxJavaInterop.toV1Single(dynamicSplitsManager.getAppSplitsByMd5(app.getMd5())))
            .flatMap(dynamicSplitsModel -> createDownload(downloadAction, status, isApkfy, app,
                dynamicSplitsModel)))
        .doOnNext(download -> {
          setupDownloadEvents(download, downloadAction, appId, trustedValue, editorsChoicePosition,
              status, download.getStoreName(), isApkfy);
          if (DownloadModel.Action.MIGRATE.equals(downloadAction)) {
            setupMigratorUninstallEvent(download.getPackageName());
          }
        })
        .doOnNext(download -> {
          if (downloadAction == DownloadModel.Action.MIGRATE) {
            appcMigrationManager.addMigrationCandidate(download.getPackageName());
          }
        })
        .flatMapCompletable(download -> installManager.install(download))
        .toCompletable();
  }

  @NotNull private Observable<RoomDownload> createDownload(DownloadModel.Action downloadAction,
      WalletAdsOfferManager.OfferResponseStatus status, boolean isApkfy, AppModel app,
      cm.aptoide.pt.aab.DynamicSplitsModel dynamicSplitsModel) {
    return Observable.just(app)
        .flatMap(download -> Observable.just(
            downloadFactory.create(downloadStateParser.parseDownloadAction(downloadAction),
                app.getAppName(), app.getPackageName(), app.getMd5(), app.getIcon(),
                app.getVersionName(), app.getVersionCode(), app.getPath(), app.getPathAlt(),
                app.getObb(), app.hasAdvertising() || app.hasBilling(), app.getSize(),
                app.getSplits(), app.getRequiredSplits(), app.getMalware()
                    .getRank()
                    .toString(), app.getStore()
                    .getName(), app.getOemId(), dynamicSplitsModel.getDynamicSplitsList())))
        .doOnError(throwable -> {
          if (throwable instanceof InvalidAppException) {
            appViewAnalytics.sendInvalidAppEventError(app.getPackageName(), app.getVersionCode(),
                downloadAction, status,
                downloadAction != null && downloadAction.equals(DownloadModel.Action.MIGRATE),
                !app.getSplits()
                    .isEmpty(), app.hasAdvertising() || app.hasBilling(), app.getMalware()
                    .getRank()
                    .toString(), app.getStore()
                    .getName(), isApkfy, throwable, app.getObb() != null,
                splitAnalyticsMapper.getSplitTypesAsString(app.hasSplits(),
                    dynamicSplitsModel.getDynamicSplitsList()));
          }
        });
  }

  public Completable downloadApp(WalletApp walletApp) {
    return RxJavaInterop.toV1Single(dynamicSplitsManager.getAppSplitsByMd5(walletApp.getMd5sum()))
        .flatMapObservable(dynamicSplitsModel -> Observable.just(downloadFactory.create(
            downloadStateParser.parseDownloadAction(walletApp.getDownloadModel()
                .getAction()), walletApp.getAppName(), walletApp.getPackageName(),
            walletApp.getMd5sum(), walletApp.getIcon(), walletApp.getVersionName(),
            walletApp.getVersionCode(), walletApp.getPath(), walletApp.getPathAlt(),
            walletApp.getObb(), false, walletApp.getSize(), walletApp.getSplits(),
            walletApp.getRequiredSplits(), walletApp.getTrustedBadge(), walletApp.getStoreName(),
            dynamicSplitsModel.getDynamicSplitsList())))
        .flatMapSingle(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(offerResponseStatus -> setupDownloadEvents(download,
                walletApp.getDownloadModel()
                    .getAction(), walletApp.getId(), offerResponseStatus, walletApp.getStoreName(),
                walletApp.getTrustedBadge(), false))
            .map(__ -> download))
        .flatMapCompletable(download -> installManager.install(download))
        .toCompletable();
  }

  private void setupDownloadEvents(RoomDownload download, DownloadModel.Action downloadAction,
      long appId, WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, String storeName,
      String trustedBadge, boolean isApkfy) {
    setupDownloadEvents(download, downloadAction, appId, trustedBadge, null, offerResponseStatus,
        storeName, isApkfy);
  }

  private void setupDownloadEvents(RoomDownload download, DownloadModel.Action downloadAction,
      long appId, String malwareRank, String editorsChoice,
      WalletAdsOfferManager.OfferResponseStatus offerResponseStatus, String storeName,
      boolean isApkfy) {
    int campaignId = notificationAnalytics.getCampaignId(download.getPackageName(), appId);
    String abTestGroup = notificationAnalytics.getAbTestingGroup(download.getPackageName(), appId);
    appViewAnalytics.setupDownloadEvents(download, campaignId, abTestGroup, downloadAction,
        AnalyticsManager.Action.CLICK, malwareRank, editorsChoice, offerResponseStatus, storeName,
        isApkfy, splitAnalyticsMapper.getSplitTypesAsString(download.getSplits()));
    installAnalytics.installStarted(download.getPackageName(), download.getVersionCode(),
        AnalyticsManager.Action.INSTALL, DownloadAnalytics.AppContext.APPVIEW,
        downloadStateParser.getOrigin(download.getAction()), campaignId, abTestGroup,
        downloadAction != null && downloadAction.equals(DownloadModel.Action.MIGRATE),
        download.hasAppc(), download.hasSplits(), offerResponseStatus.toString(), malwareRank,
        storeName, isApkfy, download.hasObbs(),
        splitAnalyticsMapper.getSplitTypesAsString(download.getSplits()));
  }

  public void setupMigratorUninstallEvent(String packageName) {
    installAnalytics.uninstallStarted(packageName, AnalyticsManager.Action.INSTALL,
        DownloadAnalytics.AppContext.APPVIEW);
  }

  public Observable<DownloadModel> loadDownloadModel(String md5, String packageName,
      int versionCode, String signature, long storeId, boolean hasAppc) {
    return Observable.combineLatest(installManager.getInstall(md5, packageName, versionCode),
        appcMigrationManager.isMigrationApp(packageName, signature, versionCode, storeId, hasAppc),
        (install, isMigration) -> new DownloadModel(
            downloadStateParser.parseDownloadType(install.getType(), isMigration),
            install.getProgress(),
            downloadStateParser.parseDownloadState(install.getState(), install.isIndeterminate()),
            install.getAppSize()));
  }

  public Completable pauseDownload(String md5) {
    return installManager.pauseInstall(md5);
  }

  public Completable resumeDownload(String md5, long appId, DownloadModel.Action action,
      String trustedBadge, boolean isApkfy) {
    return installManager.getDownload(md5)
        .flatMap(download -> moPubAdsManager.getAdsVisibilityStatus()
            .doOnSuccess(offerResponseStatus -> setupDownloadEvents(download, action, appId,
                offerResponseStatus, download.getStoreName(), trustedBadge, isApkfy))
            .map(__ -> download))
        .doOnError(throwable -> throwable.printStackTrace())
        .flatMapCompletable(download -> installManager.install(download));
  }

  public Completable cancelDownload(String md5, String packageName, int versionCode) {
    return installManager.cancelInstall(md5, packageName, versionCode);
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

  public Observable<PromotionViewModel> loadPromotionViewModel() {
    Observable<PromotionViewModel> promoViewModelObs = Observable.just(new PromotionViewModel());
    if (cachedPromotionViewModel != null) {
      Observable<PromotionViewModel> cachedViewModel = Observable.just(cachedPromotionViewModel);
      Observable<WalletApp> walletApp = promotionsManager.getWalletApp();
      Observable<AppViewModel> appViewModel = observeAppViewModel();
      return Observable.combineLatest(cachedViewModel, walletApp, appViewModel,
          (proms, wallet, appVM) -> mergeToPromotionViewModel(wallet, proms.getPromotions(),
              appVM));
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
        R.string.promo_update2appc_notification_claim_button, "aptoideinstall://package="
            + packageName
            + "&store="
            + storeName
            + "&show_install_popup=false", LocalNotificationSync.APPC_CAMPAIGN_NOTIFICATION,
        AptoideNotification.APPC_PROMOTION, LocalNotificationSyncManager.FIVE_MINUTES);
  }

  public void unscheduleNotificationSync() {
    localNotificationSyncManager.unschedule(LocalNotificationSync.APPC_CAMPAIGN_NOTIFICATION);
  }

  public Single<WalletAdsOfferManager.OfferResponseStatus> getAdsVisibilityStatus() {
    return moPubAdsManager.getAdsVisibilityStatus();
  }

  public Single<Boolean> registerAppsFlyerImpression(String packageName) {
    if (packageName.equals("com.igg.android.lordsmobile")) {
      return appsFlyerManager.registerImpression();
    } else {
      return Single.just(true);
    }
  }
}