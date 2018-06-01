package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.view.store.StoreManager;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.appview.PreferencesManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.v2.GenericResponseV2;
import cm.aptoide.pt.download.AppContext;
import cm.aptoide.pt.download.DownloadFactory;
import cm.aptoide.pt.download.InstallType;
import cm.aptoide.pt.download.Origin;
import cm.aptoide.pt.install.Install;
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

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by D01 on 28/05/2018.
 */

public class AppViewManagerTest {

  private final int limit = 18;
  @Mock private InstallManager installManager;
  @Mock private DownloadFactory downloadFactory;
  @Mock private AppCenter appCenter;
  @Mock private ReviewsManager reviewsManager;
  @Mock private AdsManager adsManager;
  @Mock private StoreManager storeManager;
  @Mock private FlagManager flagManager;
  @Mock private StoreUtilsProxy storeUtilsProxy;
  @Mock private AptoideAccountManager aptoideAccountManager;
  @Mock private AppViewConfiguration appViewConfiguration;
  @Mock private InstallAnalytics installAnalytics;
  @Mock private PreferencesManager preferencesManager;
  @Mock private DownloadStateParser downloadStateParser;
  @Mock private AppViewAnalytics appViewAnalytics;
  @Mock private NotificationAnalytics notificationAnalytics;
  @Mock private DetailedApp cachedApp;
  @Mock private MinimalAd minimalAd;
  @Mock private DetailedAppRequestResult detailedAppRequestResult;
  @Mock private ReviewRequestResult reviewRequestResult;
  @Mock private MinimalAdRequestResult minimalAdRequestResult;
  @Mock private AppsList appsList;
  @Mock private SearchAdResult searchAdResult;
  @Mock private Store store;
  @Mock private AppStats stats;
  @Mock private SocialRepository socialRepository;
  @Mock private GenericResponseV2 genericResponseV2;
  @Mock private Download download;
  @Mock private Install install;
  private AppViewManager appViewManager;

  @Before public void setupAppViewManagerTest() {
    MockitoAnnotations.initMocks(this);
    appViewManager =
        new AppViewManager(installManager, downloadFactory, appCenter, reviewsManager, adsManager,
            storeManager, flagManager, storeUtilsProxy, aptoideAccountManager, appViewConfiguration,
            preferencesManager, downloadStateParser, appViewAnalytics, notificationAnalytics,
            installAnalytics, limit, socialRepository);
  }

  @Test public void loadAppViewViewModelTestWithAppIdTest() {
    DetailedApp detailedApp =
        new DetailedApp((long) 1, "any", "any", (long) 1, "any", "any", "any", "any", true, null,
            null, null, null, null, (long) 1, null, null, null, 1, null, null, store, null, stats,
            null, null, null, true, true, null);

    //When the presenter ask for an App and the AppView was initialized with an AppId
    when(appViewConfiguration.getAppId()).thenReturn((long) 1);
    when(appViewConfiguration.getStoreName()).thenReturn("anyString");
    when(appViewConfiguration.getPackageName()).thenReturn("anyString");

    when(appCenter.loadDetailedApp((long) 1, "anyString", "anyString")).thenReturn(
        Single.just(detailedAppRequestResult));
    //And an app is returned with success
    when(detailedAppRequestResult.getDetailedApp()).thenReturn(detailedApp);

    when(store.getId()).thenReturn((long) 1);
    when(storeManager.isSubscribed(anyLong())).thenReturn(Observable.just(true));

    AppViewViewModel appViewViewModel = appViewManager.loadAppViewViewModel()
        .toBlocking()
        .value();

    //Then the correct loadDetailedApp should be called
    verify(appCenter).loadDetailedApp((long) 1, "anyString", "anyString");

    //And a AppViewViewModel should be returned with a not null app, with no loading and no errors
    Assert.assertNotNull(appViewViewModel.getAppId());
    Assert.assertEquals(false, appViewViewModel.isLoading());
    Assert.assertEquals(false, appViewViewModel.hasError());

    //Test if app is cached
    appViewViewModel = appViewManager.loadAppViewViewModel()
        .toBlocking()
        .value();
    Assert.assertNotNull(appViewViewModel.getAppId());
    Assert.assertEquals(false, appViewViewModel.isLoading());
    Assert.assertEquals(false, appViewViewModel.hasError());

    //Since there's a cached app there should not be any interactions with the appCenter
    verifyZeroInteractions(appCenter);
  }

  @Test public void loadAppViewModelTestWithMd5Test() {
    DetailedApp detailedApp =
        new DetailedApp((long) 1, "any", "any", (long) 1, "any", "any", "any", "any", true, null,
            null, null, null, null, (long) 1, "md5", null, null, 1, null, null, store, null, stats,
            null, null, null, true, true, null);

    //When the presenter ask for an App and the AppView was initialized with a Md5
    when(appViewConfiguration.getAppId()).thenReturn((long) -1);
    when(appViewConfiguration.hasMd5()).thenReturn(true);
    when(appViewConfiguration.getMd5()).thenReturn("md5");

    when(appCenter.loadDetailedAppFromMd5("md5")).thenReturn(Single.just(detailedAppRequestResult));
    //And an app is returned with success
    when(detailedAppRequestResult.getDetailedApp()).thenReturn(detailedApp);

    when(store.getId()).thenReturn((long) 1);
    when(storeManager.isSubscribed(anyLong())).thenReturn(Observable.just(true));

    AppViewViewModel appViewViewModel = appViewManager.loadAppViewViewModel()
        .toBlocking()
        .value();

    //Then the correct loadDetailedApp should be called
    verify(appCenter).loadDetailedAppFromMd5("md5");

    //And a AppViewViewModel should be returned with a not null app, with no loading and no errors
    Assert.assertNotNull(appViewViewModel.getAppId());
    Assert.assertEquals(false, appViewViewModel.isLoading());
    Assert.assertEquals(false, appViewViewModel.hasError());

    //Test if app is cached
    when(cachedApp.getMd5()).thenReturn("md5");
    appViewViewModel = appViewManager.loadAppViewViewModel()
        .toBlocking()
        .value();

    Assert.assertNotNull(appViewViewModel.getAppId());
    Assert.assertEquals(false, appViewViewModel.isLoading());
    Assert.assertEquals(false, appViewViewModel.hasError());

    //Since there's a cached app there should not be any interactions with the appCenter
    verifyZeroInteractions(appCenter);
  }

  @Test public void loadAppViewViewModelWithUniqueNameTest() {
    DetailedApp detailedApp =
        new DetailedApp((long) 1, "any", "any", (long) 1, "any", "any", "any", "any", true, null,
            null, null, null, null, (long) 1, "any", null, null, 1, null, null, store, null, stats,
            null, null, null, true, true, "uniqueName");

    //When the presenter ask for an App and the AppView was initialized with a uniqueName
    when(appViewConfiguration.getAppId()).thenReturn((long) -1);
    when(appViewConfiguration.hasMd5()).thenReturn(false);
    when(appViewConfiguration.hasUniqueName()).thenReturn(true);
    when(appViewConfiguration.getUniqueName()).thenReturn("uniqueName");

    when(appCenter.loadDetailedAppFromUniqueName("uniqueName")).thenReturn(
        Single.just(detailedAppRequestResult));
    //And an app is returned with success
    when(detailedAppRequestResult.getDetailedApp()).thenReturn(detailedApp);

    when(store.getId()).thenReturn((long) 1);
    when(storeManager.isSubscribed(anyLong())).thenReturn(Observable.just(true));

    AppViewViewModel appViewViewModel = appViewManager.loadAppViewViewModel()
        .toBlocking()
        .value();

    //Then the correct loadDetailedApp should be called
    verify(appCenter).loadDetailedAppFromUniqueName("uniqueName");

    //And a AppViewViewModel should be returned with a not null app, with no loading and no errors
    Assert.assertNotNull(appViewViewModel.getAppId());
    Assert.assertEquals(false, appViewViewModel.isLoading());
    Assert.assertEquals(false, appViewViewModel.hasError());

    //Test if app is cached
    when(cachedApp.getUniqueName()).thenReturn("uniqueName");
    appViewViewModel = appViewManager.loadAppViewViewModel()
        .toBlocking()
        .value();
    Assert.assertNotNull(appViewViewModel.getAppId());
    Assert.assertEquals(false, appViewViewModel.isLoading());
    Assert.assertEquals(false, appViewViewModel.hasError());

    //Since there's a cached app there should not be any interactions with the appCenter
    verifyZeroInteractions(appCenter);
  }

  @Test public void loadAppViewViewModelDefaultTest() {
    DetailedApp detailedApp =
        new DetailedApp((long) 1, "any", "", (long) 1, "any", "any", "any", "any", true, null, null,
            null, null, null, (long) 1, "any", null, null, 1, null, null, store, null, stats, null,
            null, null, true, true, "uniqueName");

    //When the presenter ask for an App and the AppView was initialized with arguments other than appId, md5 or uniqueName
    when(appViewConfiguration.getAppId()).thenReturn((long) -1);
    when(appViewConfiguration.hasMd5()).thenReturn(false);
    when(appViewConfiguration.hasUniqueName()).thenReturn(false);
    when(appViewConfiguration.getPackageName()).thenReturn("");
    when(appViewConfiguration.getStoreName()).thenReturn("");

    when(appCenter.loadDetailedApp("", "")).thenReturn(Single.just(detailedAppRequestResult));
    //And an app is returned with success
    when(detailedAppRequestResult.getDetailedApp()).thenReturn(detailedApp);

    when(store.getId()).thenReturn((long) 1);
    when(storeManager.isSubscribed(anyLong())).thenReturn(Observable.just(true));

    AppViewViewModel appViewViewModel = appViewManager.loadAppViewViewModel()
        .toBlocking()
        .value();

    //Then the correct loadDetailedApp should be called
    verify(appCenter).loadDetailedApp("", "");
    //And a AppViewViewModel should be returned with a not null app, with no loading and no errors
    Assert.assertNotNull(appViewViewModel.getAppId());
    Assert.assertEquals(false, appViewViewModel.isLoading());
    Assert.assertEquals(false, appViewViewModel.hasError());

    //Test if app is cached
    when(cachedApp.getPackageName()).thenReturn("");
    when(store.getName()).thenReturn("");
    appViewViewModel = appViewManager.loadAppViewViewModel()
        .toBlocking()
        .value();
    Assert.assertNotNull(appViewViewModel.getAppId());
    Assert.assertEquals(false, appViewViewModel.isLoading());
    Assert.assertEquals(false, appViewViewModel.hasError());

    //Since there's a cached app there should not be any interactions with the appCenter
    verifyZeroInteractions(appCenter);
  }

  @Test public void loadAppViewViewModelWithLoadingStateTest() {

    //When the presenter ask for an App
    when(appViewConfiguration.getAppId()).thenReturn((long) 1);
    when(appViewConfiguration.getStoreName()).thenReturn("anyString");
    when(appViewConfiguration.getPackageName()).thenReturn("anyString");

    when(appCenter.loadDetailedApp((long) 1, "anyString", "anyString")).thenReturn(
        Single.just(detailedAppRequestResult));
    //And the result is a state of loading
    when(detailedAppRequestResult.getDetailedApp()).thenReturn(null);
    when(detailedAppRequestResult.isLoading()).thenReturn(true);

    //Then an AppViewViewModel should be returned with a null app, with loading and no errors
    appViewManager.loadAppViewViewModel()
        .map(AppViewViewModel::isLoading)
        .test()
        .assertValue(true);
  }

  @Test public void loadAppViewViewModelWithErrorTest() {

    //When the presenter ask for an App
    when(appViewConfiguration.getAppId()).thenReturn((long) 1);
    when(appViewConfiguration.getStoreName()).thenReturn("anyString");
    when(appViewConfiguration.getPackageName()).thenReturn("anyString");

    when(appCenter.loadDetailedApp((long) 1, "anyString", "anyString")).thenReturn(
        Single.just(detailedAppRequestResult));
    //And the result is an Error
    when(detailedAppRequestResult.getDetailedApp()).thenReturn(null);
    when(detailedAppRequestResult.isLoading()).thenReturn(false);
    when(detailedAppRequestResult.hasError()).thenReturn(true);
    when(detailedAppRequestResult.getError()).thenReturn(DetailedAppRequestResult.Error.NETWORK);

    //Then an AppViewViewModel should be returned with a null app, with no loading and an error
    appViewManager.loadAppViewViewModel()
        .map(AppViewViewModel::getError)
        .test()
        .assertValue(DetailedAppRequestResult.Error.NETWORK);
  }

  @Test public void loadAppViewViewModelWithDefaultErrorTest() {

    //When the presenter ask for an App
    when(appViewConfiguration.getAppId()).thenReturn((long) 1);
    when(appViewConfiguration.getStoreName()).thenReturn("anyString");
    when(appViewConfiguration.getPackageName()).thenReturn("anyString");

    when(appCenter.loadDetailedApp((long) 1, "anyString", "anyString")).thenReturn(
        Single.just(detailedAppRequestResult));
    //And the result is an unknown error
    when(detailedAppRequestResult.getDetailedApp()).thenReturn(null);
    when(detailedAppRequestResult.isLoading()).thenReturn(false);
    when(detailedAppRequestResult.hasError()).thenReturn(false);

    //Then an AppViewViewModel should be returned with a null app, with no loading and with a generic error
    appViewManager.loadAppViewViewModel()
        .map(AppViewViewModel::getError)
        .test()
        .assertValue(DetailedAppRequestResult.Error.GENERIC);
  }

  @Test public void loadReviewsViewModelTest() {

    //When the presenter ask for a Review
    when(reviewsManager.loadReviews("", "", 3, "")).thenReturn(Single.just(reviewRequestResult));
    when(reviewRequestResult.getReviewList()).thenReturn(Collections.emptyList());
    when(reviewRequestResult.getError()).thenReturn(null);
    when(reviewRequestResult.isLoading()).thenReturn(false);

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

    //When the presenters asks for the Similar Apps bundle
    when(adsManager.loadAd("anyString", keywords)).thenReturn(Single.just(minimalAdRequestResult));
    when(appCenter.loadRecommendedApps(limit, "anyString")).thenReturn(Single.just(appsList));
    //And the result returns an Ad and a list of SimilarApps (empty or not)
    when(minimalAdRequestResult.getMinimalAd()).thenReturn(minimalAd);
    when(minimalAdRequestResult.getError()).thenReturn(null);

    when(appsList.getList()).thenReturn(Collections.emptyList());
    when(appsList.getError()).thenReturn(null);
    when(appsList.isLoading()).thenReturn(false);

    SimilarAppsViewModel similarAppsViewModel =
        appViewManager.loadSimilarApps("anyString", keywords)
            .toBlocking()
            .value();

    //Then a request of recommendedApps and an Ad should be done
    verify(appCenter).loadRecommendedApps(limit, "anyString");
    verify(adsManager).loadAd("anyString", keywords);

    //And a SimilarAppsViewModel should be returned with an Ad, a list of similarApps, no loading and no errors
    Assert.assertEquals(minimalAd, similarAppsViewModel.getAd());
    Assert.assertEquals(Collections.emptyList(), similarAppsViewModel.getRecommendedApps());
    Assert.assertEquals(false, similarAppsViewModel.isLoading());
    Assert.assertEquals(false, similarAppsViewModel.hasError());
  }

  @Test public void loadSimilarAppsTestWithError() {
    List<String> keywords = new ArrayList<>();
    keywords.add("key");

    //When the presenters asks for the Similar Apps bundle
    when(adsManager.loadAd("anyString", keywords)).thenReturn(Single.just(minimalAdRequestResult));
    when(appCenter.loadRecommendedApps(limit, "anyString")).thenReturn(Single.just(appsList));

    //And the result of the Ad request returns no Ad and the result of the Recommended Apps return a list of SimilarApps (empty or not)
    when(minimalAdRequestResult.getMinimalAd()).thenReturn(null);
    when(minimalAdRequestResult.getError()).thenReturn(AppsList.Error.GENERIC);

    when(appsList.getList()).thenReturn(Collections.emptyList());
    when(appsList.getError()).thenReturn(null);
    when(appsList.isLoading()).thenReturn(false);

    SimilarAppsViewModel similarAppsViewModel =
        appViewManager.loadSimilarApps("anyString", keywords)
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
    //Cache App (Test preparation)
    DetailedApp detailedApp =
        new DetailedApp((long) 1, "any", "anyString", (long) 1, "any", "any", "any", "any", true,
            null, null, null, null, null, (long) 1, null, null, null, 1, null, null, store, null,
            stats, null, null, null, true, true, null);
    when(appViewConfiguration.getAppId()).thenReturn((long) 1);
    when(appViewConfiguration.getStoreName()).thenReturn("anyString");
    when(appViewConfiguration.getPackageName()).thenReturn("anyString");

    when(appCenter.loadDetailedApp((long) 1, "anyString", "anyString")).thenReturn(
        Single.just(detailedAppRequestResult));
    when(detailedAppRequestResult.getDetailedApp()).thenReturn(detailedApp);

    when(store.getId()).thenReturn((long) 1);
    when(storeManager.isSubscribed(anyLong())).thenReturn(Observable.just(true));

    appViewManager.loadAppViewViewModel()
        .toBlocking()
        .value();

    //Test loadAdsFromAppView
    //When the presenters asks for an Ad
    when(store.getName()).thenReturn("anyString");
    when(adsManager.loadAds("anyString", "anyString")).thenReturn(Single.just(minimalAd));

    //Then the Ad from the SearchAdResult should be the same as the one in the request
    appViewManager.loadAdsFromAppView()
        .map(SearchAdResult::getAdId)
        .test()
        .assertValue(minimalAd.getAdId());

    //And it should request the adsManager an Ad
    verify(adsManager).loadAds("anyString", "anyString");
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
    Assert.assertEquals(true, appViewManager.showRootInstallWarningPopup());
  }

  @Test public void saveRootInstallWarningTest() {
    //When the presenter asks to save a rootInstallWaring
    appViewManager.saveRootInstallWarning(true);
    //Then a method from the installManager should be called
    verify(installManager).rootInstallAllowed(true);
  }

  @Test public void downloadAppTest() {
    //Cache App (Test preparation)
    DetailedApp detailedApp =
        new DetailedApp((long) 1, "any", "packageName", (long) 1, "any", "any", "any", "any", true,
            null, null, null, null, null, (long) 1, null, null, null, 1, null, null, store, null,
            stats, null, null, null, true, true, null);
    when(appViewConfiguration.getAppId()).thenReturn((long) 1);
    when(appViewConfiguration.getStoreName()).thenReturn("anyString");
    when(appViewConfiguration.getPackageName()).thenReturn("packageName");

    when(appCenter.loadDetailedApp((long) 1, "anyString", "packageName")).thenReturn(
        Single.just(detailedAppRequestResult));
    when(detailedAppRequestResult.getDetailedApp()).thenReturn(detailedApp);

    when(store.getId()).thenReturn((long) 1);
    when(storeManager.isSubscribed(anyLong())).thenReturn(Observable.just(true));

    appViewManager.loadAppViewViewModel()
        .toBlocking()
        .value();

    //DownloadApp Test

    //When the presenter asks to download an App
    when(downloadStateParser.parseDownloadAction(DownloadAppViewModel.Action.INSTALL)).thenReturn(
        1);
    when(downloadFactory.create(detailedApp, 1)).thenReturn(download);
    when(installManager.install(download)).thenReturn(Completable.complete());
    when(notificationAnalytics.getCampaignId("packageName", (long) 1)).thenReturn(2);
    when(notificationAnalytics.getAbTestingGroup("packageName", (long) 1)).thenReturn("aString");
    when(download.getPackageName()).thenReturn("packageName");
    when(download.getVersionCode()).thenReturn(1);
    when(download.getAction()).thenReturn(3);
    when(downloadStateParser.getInstallType(3)).thenReturn(InstallType.INSTALL);
    when(downloadStateParser.getOrigin(3)).thenReturn(Origin.INSTALL);

    //Then the AppViewManager should return a Complete when the download starts
    appViewManager.downloadApp(DownloadAppViewModel.Action.INSTALL, "packageName", 1)
        .test()
        .assertCompleted();

    //And it should set the install clicks if not logged in the preferences to show pop-up in the 2nd and 4th time
    verify(preferencesManager).setNotLoggedInInstallClicks();
    //And it should ask the installManager to start the download
    verify(installManager).install(download);
    //And it should set the necessary analytics
    verify(appViewAnalytics).setupDownloadEvents(download, 2, "aString",
        AnalyticsManager.Action.CLICK);
    verify(installAnalytics).installStarted("packageName", 1, InstallType.INSTALL,
        AnalyticsManager.Action.INSTALL, AppContext.APPVIEW,
        downloadStateParser.getOrigin(download.getAction()), 2, "aString");
  }

  @Test public void loadDownloadAppViewModelTest() {
    //When the presenter asks for the downloadAppViewModel
    when(installManager.getInstall("md5", "packageName", 1)).thenReturn(Observable.just(install));
    when(install.getType()).thenReturn(Install.InstallationType.INSTALL);
    when(install.getProgress()).thenReturn(2);
    when(install.getState()).thenReturn(Install.InstallationStatus.INSTALLING);
    when(downloadStateParser.parseDownloadType(Install.InstallationType.INSTALL)).thenReturn(
        DownloadAppViewModel.Action.INSTALL);
    when(downloadStateParser.parseDownloadState(Install.InstallationStatus.INSTALLING)).thenReturn(
        DownloadAppViewModel.DownloadState.ACTIVE);

    DownloadAppViewModel downloadAppViewModel =
        appViewManager.loadDownloadAppViewModel("md5", "packageName", 1)
            .toBlocking()
            .first();

    //Then it should ask the installManager to start the install
    verify(installManager).getInstall("md5", "packageName", 1);

    //And it should return a DownloadViewModel with the correct progress, action and download state
    Assert.assertEquals(2, downloadAppViewModel.getProgress());
    Assert.assertEquals(DownloadAppViewModel.Action.INSTALL, downloadAppViewModel.getAction());
    Assert.assertEquals(DownloadAppViewModel.DownloadState.ACTIVE,
        downloadAppViewModel.getDownloadState());
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
    when(downloadStateParser.getInstallType(3)).thenReturn(InstallType.INSTALL);
    when(downloadStateParser.getOrigin(3)).thenReturn(Origin.INSTALL);

    //Then the appViewManager should return a Complete when the request is done
    appViewManager.resumeDownload("md5", "packageName", 1)
        .test()
        .assertCompleted();

    //And it should ask the installManager for the current download and to start the installation
    verify(installManager).getDownload("md5");
    verify(installManager).install(download);
    //And it should set the necessary analytics
    verify(appViewAnalytics).setupDownloadEvents(download, 2, "aString",
        AnalyticsManager.Action.CLICK);
    verify(installAnalytics).installStarted("packageName", 1, InstallType.INSTALL,
        AnalyticsManager.Action.INSTALL, AppContext.APPVIEW,
        downloadStateParser.getOrigin(download.getAction()), 2, "aString");
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
    //When the presenter asks for the SearchAdResult after setting it, it should return the same SearchAdResult
    appViewManager.setSearchAdResult(searchAdResult);
    Assert.assertEquals(searchAdResult, appViewManager.getSearchAdResult());
  }

  @Test public void handleAdsLogicTest() {
    //When the presenter aks the AppViewManager to handle the Ads logic
    appViewManager.handleAdsLogic(searchAdResult);
    //It should delegate that to the adsManager
    verify(adsManager).handleAdsLogic(searchAdResult);
  }

  @Test public void shouldShowInstallRecommendsPreviewDialog() {
    //When the presenter asks if it should show the recommends preview dialog
    appViewManager.shouldShowRecommendsPreviewDialog();
    //Then the AppViewManager should ask the preferencesManager
    verify(preferencesManager).shouldShowInstallRecommendsPreviewDialog();
  }

  @Test public void canShowNotLoggedInDialogTest() {
    //When the presenter asks if it should show the not logged in dialog
    appViewManager.canShowNotLoggedInDialog();
    //Then the AppViewManager should ask the preferencesManager
    verify(preferencesManager).canShowNotLoggedInDialog();
  }

  @Test public void shareOnTimelineTest() {
    //When the presenter asks the AppViewManager to share on timeline, then it should inform when it's completed
    appViewManager.shareOnTimeline("packageName", (long) 1, "shareType")
        .test()
        .assertCompleted();
    //And should delegate the action to the social repository
    verify(socialRepository).share("packageName", (long) 1, "shareType");
  }

  @Test public void shareOnTimelineAsyncTest() {
    //When the presenter asks the AppViewManager to share on timeline, then it should inform when it's completed
    appViewManager.shareOnTimelineAsync("packageName", (long) 1)
        .test()
        .assertCompleted();
    //And should delegate the action to the social repository
    verify(socialRepository).asyncShare("packageName", (long) 1, "app");
  }

  @Test public void dontShowLoggedInInstallRecommendsPreviewDialogTest() {
    //When the presenter asks to not show the recommends preview dialog, then it should inform when it's completed
    appViewManager.dontShowLoggedInInstallRecommendsPreviewDialog()
        .test()
        .assertCompleted();
    //And should delegate the action to the preferencesManager
    verify(preferencesManager).setShouldShowInstallRecommendsPreviewDialog(false);
  }
}
