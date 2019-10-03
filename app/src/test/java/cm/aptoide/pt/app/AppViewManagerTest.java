package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.ads.WalletAdsOfferManager;
import cm.aptoide.pt.ads.data.AptoideNativeAd;
import cm.aptoide.pt.app.migration.AppcMigrationManager;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.dataprovider.ws.v2.GenericResponseV2;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.install.Install;
import cm.aptoide.pt.install.InstallAnalytics;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.notification.AppcPromotionNotificationStringProvider;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.sync.LocalNotificationSyncManager;
import cm.aptoide.pt.promotions.PromotionsManager;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.view.app.AppCenter;
import cm.aptoide.pt.view.app.AppDeveloper;
import cm.aptoide.pt.view.app.AppFlags;
import cm.aptoide.pt.view.app.AppMedia;
import cm.aptoide.pt.view.app.AppRating;
import cm.aptoide.pt.view.app.AppStats;
import cm.aptoide.pt.view.app.AppsList;
import cm.aptoide.pt.view.app.FlagsVote;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by D01 on 28/05/2018.
 */

public class AppViewManagerTest {

  private final int limit = 18;
  @Mock private InstallManager installManager;
  @Mock private AppCenter appCenter;
  @Mock private ReviewsManager reviewsManager;
  @Mock private AdsManager adsManager;
  @Mock private FlagManager flagManager;
  @Mock private StoreUtilsProxy storeUtilsProxy;
  @Mock private AptoideAccountManager aptoideAccountManager;
  @Mock private InstallAnalytics installAnalytics;
  @Mock private AppViewAnalytics appViewAnalytics;
  @Mock private NotificationAnalytics notificationAnalytics;
  @Mock private GenericResponseV2 genericResponseV2;
  @Mock private Download download;
  @Mock private DownloadFactory downloadFactory;
  @Mock private AppCoinsManager appCoinsManager;
  @Mock private MoPubAdsManager moPubAdsManager;
  @Mock private PromotionsManager promotionsManager;
  @Mock private AppcMigrationManager migrationManager;
  @Mock private LocalNotificationSyncManager localNotificationSyncManager;
  @Mock private AppcPromotionNotificationStringProvider appcPromotionNotificationStringProvider;
  @Mock private AppViewModelManager appViewModelManager;
  private DownloadStateParser downloadStateParser;
  private AppViewManager appViewManager;
  private AppStats appStats;

  @Before public void setupAppViewManagerTest() {
    MockitoAnnotations.initMocks(this);
    downloadStateParser = new DownloadStateParser();
    AppRating appRating = new AppRating(1, 1, Collections.emptyList());
    appStats = new AppStats(appRating, appRating, 1, 1);
    appViewManager =
        new AppViewManager(appViewModelManager, installManager, downloadFactory, appCenter,
            reviewsManager, adsManager, flagManager, storeUtilsProxy, aptoideAccountManager,
            moPubAdsManager, downloadStateParser, appViewAnalytics, notificationAnalytics,
            installAnalytics, limit, Schedulers.immediate(), "anyString", appCoinsManager,
            promotionsManager, migrationManager, localNotificationSyncManager,
            appcPromotionNotificationStringProvider);
  }

  @Test public void loadAppViewModelTest() {
    List<String> bdsFlags = new ArrayList<>();
    Malware malware = new Malware();
    malware.setRank(Malware.Rank.CRITICAL);
    AppModel appModel =
        new AppModel(11, "aptoide", new cm.aptoide.pt.dataprovider.model.v7.store.Store(), "", true,
            malware, new AppFlags("", Collections.emptyList()), Collections.<String>emptyList(),
            Collections.<String>emptyList(), Collections.<String>emptyList(), 121312312,
            "md5dajskdjas", "mypath", "myAltPath", 12311, "9.0.0", "cm.aptoide.pt", 12311,
            100210312, new AppRating(0, 100, Collections.emptyList()), 1231231,
            new AppRating(0, 100, Collections.emptyList()),
            new AppDeveloper("Felipao", "felipao@aptoide.com", "privacy", "website"), "graphic",
            "icon", new AppMedia("description", Collections.<String>emptyList(), "news",
            Collections.emptyList(), Collections.emptyList()), "modified", "app added", null, null,
            "weburls", false, false, "paid path", "no", true, "aptoide",
            AppViewFragment.OpenType.OPEN_ONLY, 0, null, "editorsChoice", "origin", false,
            "marketName", false, false, bdsFlags, "", "", false, null, null);
    AppViewModel exampleAppViewModel = new AppViewModel(appModel, null, null, null);

    appViewManager =
        new AppViewManager(appViewModelManager, installManager, downloadFactory, appCenter,
            reviewsManager, adsManager, flagManager, storeUtilsProxy, aptoideAccountManager,
            moPubAdsManager, downloadStateParser, appViewAnalytics, notificationAnalytics,
            installAnalytics, limit, Schedulers.immediate(), "anyString", appCoinsManager,
            promotionsManager, migrationManager, localNotificationSyncManager,
            appcPromotionNotificationStringProvider);

    when(appViewModelManager.getAppViewModel()).thenReturn(Single.just(exampleAppViewModel));

    AppViewModel appViewModel = appViewManager.getAppViewModel()
        .toBlocking()
        .value();

    //Then the correct AppViewModelManager method should be called
    verify(appViewModelManager).getAppViewModel();

    //And a AppViewModel should be the same
    Assert.assertNotNull(appViewModel);
    Assert.assertEquals(appViewModel, exampleAppViewModel);
  }

  @Test public void loadReviewsViewModelTest() {
    ReviewRequestResult reviewRequestResult = new ReviewRequestResult(Collections.emptyList());
    //When the presenter ask for a Review and a result is returned
    when(reviewsManager.loadReviews("", "", 3, "")).thenReturn(Single.just(reviewRequestResult));

    ReviewsViewModel reviewsViewModel = appViewManager.loadReviewsViewModel("", "", "")
        .toBlocking()
        .value();

    //Then a ReviewsViewModel should be returned with a not null app, with no loading and no errors
    Assert.assertEquals(Collections.emptyList(), reviewsViewModel.getReviewsList());
    Assert.assertEquals(false, reviewsViewModel.isLoading());
    Assert.assertNotNull(reviewsViewModel);
  }

  @Test public void loadSimilarAppsTest() {
    List<String> keywords = new ArrayList<>();
    keywords.add("key");

    MinimalAd minimalAd =
        new MinimalAd("anyString", (long) 1, "", "", "", (long) 1, (long) 1, "", "", "", "", 1, 1,
            (long) 1);
    MinimalAdRequestResult minimalAdRequestResult = new MinimalAdRequestResult(minimalAd);
    AppsList appsList = new AppsList(Collections.emptyList(), false, 0);

    //When the presenters asks for the Similar Apps bundle
    //And the result returns an Ad and a list of SimilarApps (empty or not)
    when(adsManager.loadAd("anyString", keywords)).thenReturn(Single.just(minimalAdRequestResult));
    when(appCenter.loadRecommendedApps(limit, "anyString")).thenReturn(Single.just(appsList));
    SimilarAppsViewModel similarAppsViewModel =
        appViewManager.loadAptoideSimilarAppsViewModel("anyString", keywords)
            .toBlocking()
            .value();

    //Then a request of recommendedApps and an Ad should be done
    verify(appCenter).loadRecommendedApps(limit, "anyString");
    verify(adsManager).loadAd("anyString", keywords);

    //And a SimilarAppsViewModel should be returned with an Ad, a list of similarApps, no loading and no errors
    Assert.assertEquals(new AptoideNativeAd(minimalAd), similarAppsViewModel.getAd());
    Assert.assertEquals(Collections.emptyList(), similarAppsViewModel.getRecommendedApps());
    Assert.assertEquals(false, similarAppsViewModel.isLoading());
    Assert.assertEquals(false, similarAppsViewModel.hasError());
  }

  @Test public void loadSimilarAppsTestWithError() {
    List<String> keywords = new ArrayList<>();
    keywords.add("key");

    MinimalAdRequestResult minimalAdRequestResult =
        new MinimalAdRequestResult(AppsList.Error.GENERIC);
    AppsList appsList = new AppsList(Collections.emptyList(), false, 0);

    //When the presenters asks for the Similar Apps bundle
    //And the result of the Ad request returns no Ad and the result of the Recommended Apps return a list of SimilarApps (empty or not)
    when(adsManager.loadAd("anyString", keywords)).thenReturn(Single.just(minimalAdRequestResult));
    when(appCenter.loadRecommendedApps(limit, "anyString")).thenReturn(Single.just(appsList));

    SimilarAppsViewModel similarAppsViewModel =
        appViewManager.loadAptoideSimilarAppsViewModel("anyString", keywords)
            .toBlocking()
            .value();

    //Then a request of recommendedApps and an Ad should be done
    verify(appCenter).loadRecommendedApps(limit, "anyString");
    verify(adsManager).loadAd("anyString", keywords);

    //And a SimilarAppsViewModel should be returned with no Ad, a list of similarApps, no loading, no error in recommendedApps, a error in of an Ad, and saying that there was an error
    Assert.assertNull(similarAppsViewModel.getAd());
    Assert.assertEquals(Collections.emptyList(), similarAppsViewModel.getRecommendedApps());
    Assert.assertEquals(false, similarAppsViewModel.isLoading());
    Assert.assertEquals(true, similarAppsViewModel.hasError());
    Assert.assertEquals(true, similarAppsViewModel.hasAdError());
    Assert.assertEquals(false, similarAppsViewModel.hasRecommendedAppsError());
  }

  @Test public void loadAdsFromAppViewTest() {
    List<String> bdsFlags = new ArrayList<>();
    Malware malware = new Malware();
    malware.setRank(Malware.Rank.CRITICAL);
    //Cache App (Test preparation)
    AppModel appModel =
        new AppModel(11, "aptoide", new cm.aptoide.pt.dataprovider.model.v7.store.Store(), "", true,
            malware, new AppFlags("", Collections.emptyList()), Collections.<String>emptyList(),
            Collections.<String>emptyList(), Collections.<String>emptyList(), 121312312,
            "md5dajskdjas", "mypath", "myAltPath", 12311, "9.0.0", "cm.aptoide.pt", 12311,
            100210312, new AppRating(0, 100, Collections.emptyList()), 1231231,
            new AppRating(0, 100, Collections.emptyList()),
            new AppDeveloper("Felipao", "felipao@aptoide.com", "privacy", "website"), "graphic",
            "icon", new AppMedia("description", Collections.<String>emptyList(), "news",
            Collections.emptyList(), Collections.emptyList()), "modified", "app added", null, null,
            "weburls", false, false, "paid path", "no", true, "aptoide",
            AppViewFragment.OpenType.OPEN_ONLY, 0, null, "editorsChoice", "origin", false,
            "marketName", false, false, bdsFlags, "", "", false, null, null);

    MinimalAd minimalAd =
        new MinimalAd("anyString", (long) 1, "", "", "", (long) 1, (long) 1, "", "", "", "", 1, 1,
            (long) 1);

    appViewManager =
        new AppViewManager(appViewModelManager, installManager, downloadFactory, appCenter,
            reviewsManager, adsManager, flagManager, storeUtilsProxy, aptoideAccountManager,
            moPubAdsManager, downloadStateParser, appViewAnalytics, notificationAnalytics,
            installAnalytics, limit, Schedulers.immediate(), "anyString", appCoinsManager,
            promotionsManager, migrationManager, localNotificationSyncManager,
            appcPromotionNotificationStringProvider);

    when(appViewModelManager.getAppModel()).thenReturn(Single.just(appModel));
    //Test loadAdsFromAppView
    //When the presenters asks for an Ad
    when(adsManager.loadAds("cm.aptoide.pt", null)).thenReturn(Single.just(minimalAd));

    //Then the Ad from the SearchAdResult should be the same as the one in the request
    appViewManager.loadAdsFromAppView()
        .map(SearchAdResult::getAdId)
        .test()
        .assertValue(minimalAd.getAdId());

    //And it should request the adsManager an Ad
    verify(adsManager).loadAds("cm.aptoide.pt", null);
  }

  @Test public void flagApkTestSuccess() {
    FlagsVote.VoteType voteType = FlagsVote.VoteType.GOOD;
    //When the presenter asks to flag an Apk
    when(flagManager.flagApk("anyString", "anyString", voteType.name()
        .toLowerCase())).thenReturn(Single.just(genericResponseV2));
    //And the response from the request has no problems
    when(genericResponseV2.isOk()).thenReturn(true);
    when(genericResponseV2.hasErrors()).thenReturn(false);

    //Then it should inform the presenter that everything went fine
    appViewManager.flagApk("anyString", "anyString", FlagsVote.VoteType.GOOD)
        .test()
        .assertValue(true);

    //And the flagManager method to flag the apk should be called
    verify(flagManager).flagApk("anyString", "anyString", voteType.name()
        .toLowerCase());
  }

  @Test public void flagApkTestFail() {
    FlagsVote.VoteType voteType = FlagsVote.VoteType.GOOD;

    //When the presenter asks to flag an Apk
    when(flagManager.flagApk("anyString", "anyString", voteType.name()
        .toLowerCase())).thenReturn(Single.just(genericResponseV2));
    //And the response from the request has errors
    when(genericResponseV2.isOk()).thenReturn(false);
    when(genericResponseV2.hasErrors()).thenReturn(true);

    //Then it should inform the presenter that something went wrong
    appViewManager.flagApk("anyString", "anyString", FlagsVote.VoteType.GOOD)
        .test()
        .assertValue(false);

    //And the flagManager method to flag the apk should be called
    verify(flagManager).flagApk("anyString", "anyString", voteType.name()
        .toLowerCase());
  }

  @Test public void subscribeStoreTest() {
    //When the presenter asks to subscribe a store
    appViewManager.subscribeStore("anyString")
        .test()
        .assertCompleted();

    //Then a method from storeUtilsProxy to subscribe a store should be called
    verify(storeUtilsProxy).subscribeStore("anyString", null, null, aptoideAccountManager);
  }

  @Test public void showRootInstallWarningPopupTest() {
    //When the presenter asks to if it should show a warning popup, and it's supposed to show
    when(installManager.showWarning()).thenReturn(true);
    //Then it should return true
    Assert.assertEquals(true, appViewManager.shouldShowRootInstallWarningPopup());
  }

  @Test public void saveRootInstallWarningTest() {
    //When the presenter asks to save a rootInstallWaring
    appViewManager.allowRootInstall(true);
    //Then a method from the installManager should be called
    verify(installManager).rootInstallAllowed(true);
  }

  @Test public void downloadAppTest() {
    List<String> bdsFlags = new ArrayList<>();
    Malware malware = new Malware();
    malware.setRank(Malware.Rank.CRITICAL);
    //Cache App (Test preparation)
    AppModel appModel =
        new AppModel(11, "aptoide", new cm.aptoide.pt.dataprovider.model.v7.store.Store(), "", true,
            malware, new AppFlags("", Collections.emptyList()), Collections.<String>emptyList(),
            Collections.<String>emptyList(), Collections.<String>emptyList(), 121312312,
            "md5dajskdjas", "mypath", "myAltPath", 12311, "9.0.0", "cm.aptoide.pt", 12311,
            100210312, new AppRating(0, 100, Collections.emptyList()), 1231231,
            new AppRating(0, 100, Collections.emptyList()),
            new AppDeveloper("Felipao", "felipao@aptoide.com", "privacy", "website"), "graphic",
            "icon", new AppMedia("description", Collections.<String>emptyList(), "news",
            Collections.emptyList(), Collections.emptyList()), "modified", "app added", null, null,
            "weburls", false, false, "paid path", "no", true, "aptoide",
            AppViewFragment.OpenType.OPEN_ONLY, 0, null, "editorsChoice", "origin", false,
            "marketName", false, false, bdsFlags, "", "", false, null, null);

    appViewManager =
        new AppViewManager(appViewModelManager, installManager, downloadFactory, appCenter,
            reviewsManager, adsManager, flagManager, storeUtilsProxy, aptoideAccountManager,
            moPubAdsManager, downloadStateParser, appViewAnalytics, notificationAnalytics,
            installAnalytics, limit, Schedulers.immediate(), "anyString", appCoinsManager,
            promotionsManager, migrationManager, localNotificationSyncManager,
            appcPromotionNotificationStringProvider);

    when(appViewModelManager.getAppModel()).thenReturn(Single.just(appModel));

    when(moPubAdsManager.shouldHaveInterstitialAds()).thenReturn(Single.just(true));
    when(moPubAdsManager.shouldShowAds()).thenReturn(Single.just(true));
    when(moPubAdsManager.getAdsVisibilityStatus()).thenReturn(
        Single.just(WalletAdsOfferManager.OfferResponseStatus.ADS_SHOW));

    appViewManager.getAppModel()
        .subscribe();

    //DownloadApp Test

    //When the presenter asks to download an App
    int action = downloadStateParser.parseDownloadAction(DownloadModel.Action.INSTALL);
    when(downloadFactory.create(action, appModel.getAppName(), appModel.getPackageName(),
        appModel.getMd5(), appModel.getIcon(), appModel.getVersionName(), appModel.getVersionCode(),
        appModel.getPath(), appModel.getPathAlt(), appModel.getObb(), false, appModel.getSize(),
        null, null)).thenReturn(download);
    when(installManager.install(download)).thenReturn(Completable.complete());
    when(notificationAnalytics.getCampaignId("packageName", (long) 1)).thenReturn(2);
    when(notificationAnalytics.getAbTestingGroup("packageName", (long) 1)).thenReturn("aString");
    when(download.getPackageName()).thenReturn("packageName");
    when(download.getVersionCode()).thenReturn(1);
    when(download.getAction()).thenReturn(3);

    //Then the AppViewManager should return a Complete when the download starts
    appViewManager.downloadApp(DownloadModel.Action.INSTALL, 2, "aString", null)
        .test()
        .assertCompleted();

    //And it should ask the installManager to start the download
    verify(installManager).install(download);
    //And it should set the necessary analytics
    verify(appViewAnalytics).setupDownloadEvents(download, 0, null, DownloadModel.Action.INSTALL,
        AnalyticsManager.Action.CLICK, "aString", null,
        WalletAdsOfferManager.OfferResponseStatus.ADS_SHOW);
    verify(installAnalytics).installStarted("packageName", 1, AnalyticsManager.Action.INSTALL,
        AppContext.APPVIEW, downloadStateParser.getOrigin(download.getAction()), 0, null, false,
        false, false);
  }

  @Test public void loadDownloadAppViewModelTest() {
    Install install =
        new Install(2, Install.InstallationStatus.DOWNLOADING, Install.InstallationType.INSTALL,
            false, 1, "md5", "packageName", 1, "", "", "");

    //When the presenter asks for the downloadModel
    when(installManager.getInstall("md5", "packageName", 1)).thenReturn(Observable.just(install));
    when(migrationManager.isMigrationApp("packageName", "", 1, 2, false)).thenReturn(
        Observable.just(false));

    DownloadModel downloadModel =
        appViewManager.loadDownloadModel("md5", "packageName", 1, false, null, "", 2, false)
            .toBlocking()
            .first();

    ////Then it should ask the installManager to start the install
    verify(installManager).getInstall("md5", "packageName", 1);

    //And it should return a DownloadViewModel with the correct progress, action and download state
    Assert.assertEquals(2, downloadModel.getProgress());
    Assert.assertEquals(DownloadModel.Action.INSTALL, downloadModel.getAction());
    Assert.assertEquals(DownloadModel.DownloadState.ACTIVE, downloadModel.getDownloadState());
  }

  @Test public void pauseDownloadTest() {
    //When the presenter wants to pause the download
    //Then the appViewManager should return a Complete when the request is done
    appViewManager.pauseDownload("md5")
        .test()
        .assertCompleted();
    //And it should ask the installManager to stop the installation
    verify(installManager).stopInstallation("md5");
  }

  @Test public void resumeDownloadTest() {
    //When the presenter asks to resume a download
    when(installManager.getDownload("md5")).thenReturn(Single.just(download));
    when(installManager.install(download)).thenReturn(Completable.complete());

    when(notificationAnalytics.getCampaignId("packageName", (long) 1)).thenReturn(2);
    when(notificationAnalytics.getAbTestingGroup("packageName", (long) 1)).thenReturn("aString");
    when(download.getPackageName()).thenReturn("packageName");
    when(download.getVersionCode()).thenReturn(1);
    when(download.getAction()).thenReturn(3);
    when(moPubAdsManager.shouldHaveInterstitialAds()).thenReturn(Single.just(true));
    when(moPubAdsManager.shouldShowAds()).thenReturn(Single.just(true));
    when(moPubAdsManager.getAdsVisibilityStatus()).thenReturn(
        Single.just(WalletAdsOfferManager.OfferResponseStatus.ADS_SHOW));

    //Then the appViewManager should return a Complete when the request is done
    appViewManager.resumeDownload("md5", 1, DownloadModel.Action.INSTALL)
        .test()
        .assertCompleted();

    //And it should ask the installManager for the current download and to start the installation
    verify(installManager).getDownload("md5");
    verify(installManager).install(download);
    //And it should set the necessary analytics
    verify(appViewAnalytics).setupDownloadEvents(download, 2, "aString",
        DownloadModel.Action.INSTALL, AnalyticsManager.Action.CLICK, null, null,
        WalletAdsOfferManager.OfferResponseStatus.ADS_SHOW);
    verify(installAnalytics).installStarted("packageName", 1, AnalyticsManager.Action.INSTALL,
        AppContext.APPVIEW, downloadStateParser.getOrigin(download.getAction()), 2, "aString",
        false, false, false);
  }

  @Test public void cancelDownloadTest() {
    //When the presents asks to cancel a download
    //Then it should return a Complete when the request is done
    appViewManager.cancelDownload("md5", "packageName", 1)
        .test()
        .assertCompleted();
    //And it should ask the installManager to remove the file
    verify(installManager).removeInstallationFile("md5", "packageName", 1);
  }

  @Test public void setAndGetSearchAdResultTest() {
    SearchAdResult searchAdResult = new SearchAdResult();
    //When the presenter asks for the SearchAdResult after setting it, it should return the same SearchAdResult
    appViewManager.setSearchAdResult(searchAdResult);
    Assert.assertEquals(searchAdResult, appViewManager.getSearchAdResult());
  }

  @Test public void handleAdsLogicTest() {
    SearchAdResult searchAdResult = new SearchAdResult();
    //When the presenter aks the AppViewManager to handle the Ads logic
    appViewManager.handleAdsLogic(searchAdResult);
    //It should delegate that to the adsManager
    verify(adsManager).handleAdsLogic(searchAdResult);
  }
}
