package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.app.view.AppCoinsViewModel;
import cm.aptoide.pt.app.view.donations.Donation;
import cm.aptoide.pt.appview.PreferencesManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.promotions.PromotionApp;
import cm.aptoide.pt.promotions.PromotionsManager;
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

import static cm.aptoide.pt.ads.WalletAdsOfferManager.OfferResponseStatus.ADS_HIDE;
import static cm.aptoide.pt.ads.WalletAdsOfferManager.OfferResponseStatus.NO_ADS;

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
  private PreferencesManager preferencesManager;
  private DownloadStateParser downloadStateParser;
  private AppViewAnalytics appViewAnalytics;
  private NotificationAnalytics notificationAnalytics;
  private DetailedApp cachedApp;
  private SearchAdResult searchAdResult;
  private SocialRepository socialRepository;
  private String marketName;
  private boolean isFirstLoad;
  private AppCoinsManager appCoinsManager;
  private AppCoinsViewModel cachedAppCoinsViewModel;
  private SimilarAppsViewModel cachedSimilarAppsViewModel;
  private SimilarAppsViewModel cachedAppcSimilarAppsViewModel;
  private String promotionId;

  public AppViewManager(InstallManager installManager, DownloadFactory downloadFactory,
      AppCenter appCenter, ReviewsManager reviewsManager, AdsManager adsManager,
      StoreManager storeManager, FlagManager flagManager, StoreUtilsProxy storeUtilsProxy,
      AptoideAccountManager aptoideAccountManager, AppViewConfiguration appViewConfiguration,
      MoPubAdsManager moPubAdsManager, PreferencesManager preferencesManager,
      DownloadStateParser downloadStateParser, AppViewAnalytics appViewAnalytics,
      NotificationAnalytics notificationAnalytics, InstallAnalytics installAnalytics, int limit,
      SocialRepository socialRepository, String marketName, AppCoinsManager appCoinsManager,
      PromotionsManager promotionsManager, String promotionId,
      InstalledRepository installedRepository) {
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
    this.preferencesManager = preferencesManager;
    this.downloadStateParser = downloadStateParser;
    this.appViewAnalytics = appViewAnalytics;
    this.notificationAnalytics = notificationAnalytics;
    this.installAnalytics = installAnalytics;
    this.socialRepository = socialRepository;
    this.limit = limit;
    this.marketName = marketName;
    this.appCoinsManager = appCoinsManager;
    this.promotionsManager = promotionsManager;
    this.promotionId = promotionId;
    this.installedRepository = installedRepository;
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
    return moPubAdsManager.recordInterstitialAdImpression();
  }

  public Single<Boolean> recordInterstitialClick() {
    return moPubAdsManager.recordInterstitialAdClick();
  }

  public Observable<DownloadAppViewModel> loadDownloadAppViewModel(String md5, String packageName,
      int versionCode, boolean paidApp, GetAppMeta.Pay pay) {
    return loadDownloadModel(md5, packageName, versionCode, paidApp, pay).map(
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
            appViewConfiguration.getCampaignUrl()));
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

  public Completable downloadApp(DownloadModel.Action downloadAction, long appId,
      String trustedValue, String editorsChoicePosition) {
    increaseInstallClick();
    return Observable.just(
        downloadFactory.create(downloadStateParser.parseDownloadAction(downloadAction),
            cachedApp.getName(), cachedApp.getPackageName(), cachedApp.getMd5(),
            cachedApp.getIcon(), cachedApp.getVersionName(), cachedApp.getVersionCode(),
            cachedApp.getPath(), cachedApp.getPathAlt(), cachedApp.getObb()))
        .flatMapSingle(download -> moPubAdsManager.shouldHaveInterstitialAds()
            .flatMap(hasAds -> {
              if (hasAds) {
                return moPubAdsManager.shouldShowAds()
                    .doOnSuccess(showAds -> setupDownloadEvents(download, downloadAction, appId,
                        trustedValue, editorsChoicePosition,
                        showAds ? WalletAdsOfferManager.OfferResponseStatus.ADS_SHOW : ADS_HIDE));
              } else {
                setupDownloadEvents(download, downloadAction, appId, trustedValue,
                    editorsChoicePosition, NO_ADS);
                return Single.just(false);
              }
            })
            .map(__ -> download))
        .flatMapCompletable(download -> installManager.install(download))
        .toCompletable();
  }

  public Completable downloadApp(WalletPromotionViewModel promotionViewApp) {
    return Observable.just(downloadFactory.create(downloadStateParser.parseDownloadAction(
        promotionViewApp.getDownloadModel()
            .getAction()), promotionViewApp.getAppName(), promotionViewApp.getPackageName(),
        promotionViewApp.getMd5sum(), promotionViewApp.getIcon(), promotionViewApp.getVersionName(),
        promotionViewApp.getVersionCode(), promotionViewApp.getPath(),
        promotionViewApp.getPathAlt(), promotionViewApp.getObb()))
        .flatMapSingle(download -> moPubAdsManager.shouldHaveInterstitialAds()
            .flatMap(hasAds -> {
              if (hasAds) {
                return moPubAdsManager.shouldShowAds()
                    .doOnSuccess(showAds -> setupDownloadEvents(download,
                        promotionViewApp.getDownloadModel()
                            .getAction(), promotionViewApp.getId(),
                        showAds ? WalletAdsOfferManager.OfferResponseStatus.ADS_SHOW : ADS_HIDE));
              } else {
                setupDownloadEvents(download, promotionViewApp.getDownloadModel()
                    .getAction(), promotionViewApp.getId(), NO_ADS);
                return Single.just(false);
              }
            })
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
        downloadStateParser.getOrigin(download.getAction()), campaignId, abTestGroup);
  }

  public Observable<DownloadModel> loadDownloadModel(String md5, String packageName,
      int versionCode, boolean paidApp, GetAppMeta.Pay pay) {
    return installManager.getInstall(md5, packageName, versionCode)
        .map(install -> new DownloadModel(
            downloadStateParser.parseDownloadType(install.getType(), paidApp,
                pay != null && pay.isPaid()), install.getProgress(),
            downloadStateParser.parseDownloadState(install.getState()), pay));
  }

  public Completable pauseDownload(String md5) {
    return Completable.fromAction(() -> installManager.stopInstallation(md5));
  }

  public Completable resumeDownload(String md5, long appId) {
    return installManager.getDownload(md5)
        .flatMap(download -> moPubAdsManager.shouldHaveInterstitialAds()
            .flatMap(hasAds -> {
              if (hasAds) {
                return moPubAdsManager.shouldShowAds()
                    .doOnSuccess(showAds -> setupDownloadEvents(download, appId,
                        showAds ? WalletAdsOfferManager.OfferResponseStatus.ADS_SHOW : ADS_HIDE));
              } else {
                setupDownloadEvents(download, appId,
                    WalletAdsOfferManager.OfferResponseStatus.NO_ADS);
                return Single.just(false);
              }
            })
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

  public boolean shouldShowRecommendsPreviewDialog() {
    return preferencesManager.shouldShowInstallRecommendsPreviewDialog() && !isAppcApp();
  }

  private boolean isAppcApp() {
    return cachedAppCoinsViewModel != null && (cachedAppCoinsViewModel.hasAdvertising()
        || cachedAppCoinsViewModel.hasBilling());
  }

  public boolean canShowNotLoggedInDialog() {
    return preferencesManager.canShowNotLoggedInDialog() && !isAppcApp();
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
                    sendAdsWalletPromotionEvent();
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

  private void sendAdsWalletPromotionEvent() {
    appViewAnalytics.sendAdsWalletPromotionEvent();
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

  public Observable<WalletPromotionViewModel> loadWalletPromotionViewModel(boolean hasAppc) {
    if (!hasAppc) {
      return Observable.just(new WalletPromotionViewModel(false));
    } else {
      return promotionsManager.getPromotionApps(promotionId)
          .map(this::mapToWalletPromotion)
          .flatMapObservable(viewModel -> installManager.getInstall(viewModel.getMd5sum(),
              viewModel.getPackageName(), viewModel.getVersionCode())
              .flatMap(install -> installedRepository.getInstalled(cachedApp.getPackageName())
                  .map(installed -> new PromotionInstallDependency(install, installed != null)))
              .map(installDependency -> new WalletPromotionViewModel(mapToDownloadModel(
                  installDependency.getInstall()
                      .getType(), installDependency.getInstall()
                      .getProgress(), installDependency.getInstall()
                      .getState()), viewModel.getAppName(), viewModel.getIcon(), viewModel.getId(),
                  viewModel.getPackageName(), viewModel.getMd5sum(), viewModel.getVersionCode(),
                  viewModel.getVersionName(), viewModel.getPath(), viewModel.getPathAlt(),
                  viewModel.getObb(), viewModel.getAppcValue(), installDependency.getInstall()
                  .getState()
                  .equals(Install.InstallationStatus.INSTALLED), viewModel.shouldShowOffer(),
                  installDependency.isAppInstalled())));
    }
  }

  private WalletPromotionViewModel mapToWalletPromotion(List<PromotionApp> apps) {
    if (apps == null || apps.isEmpty()) {
      return new WalletPromotionViewModel(false);
    } else {
      PromotionApp app = apps.get(0);
      if (app.getPackageName()
          .equals("com.appcoins.wallet")) {
        if (app.isClaimed()) {
          return new WalletPromotionViewModel(false);
        } else {
          return new WalletPromotionViewModel(null, app.getName(), app.getAppIcon(), app.getAppId(),
              app.getPackageName(), app.getMd5(), app.getVersionCode(), app.getVersionName(),
              app.getDownloadPath(), app.getAlternativePath(), app.getObb(),
              Math.round(app.getAppcValue()), false, true, false);
        }
      } else {
        return new WalletPromotionViewModel(false);
      }
    }
  }

  private DownloadModel mapToDownloadModel(Install.InstallationType type, int progress,
      Install.InstallationStatus state) {
    return new DownloadModel(downloadStateParser.parseDownloadType(type, false, false), progress,
        downloadStateParser.parseDownloadState(state), null);
  }
}
