package cm.aptoide.pt.navigation;

import android.support.annotation.NonNull;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.ads.data.ApplicationAd;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.home.ActionBundle;
import cm.aptoide.pt.home.ActionItem;
import cm.aptoide.pt.home.AdBundle;
import cm.aptoide.pt.home.AdClick;
import cm.aptoide.pt.home.AdHomeEvent;
import cm.aptoide.pt.home.AdMapper;
import cm.aptoide.pt.home.AdsTagWrapper;
import cm.aptoide.pt.home.AppHomeEvent;
import cm.aptoide.pt.home.FakeBundleDataSource;
import cm.aptoide.pt.home.Home;
import cm.aptoide.pt.home.HomeAnalytics;
import cm.aptoide.pt.home.HomeBundle;
import cm.aptoide.pt.home.HomeBundlesModel;
import cm.aptoide.pt.home.HomeEvent;
import cm.aptoide.pt.home.HomeFragment;
import cm.aptoide.pt.home.HomeNavigator;
import cm.aptoide.pt.home.HomePresenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.app.Application;
import java.util.Collections;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class HomePresenterTest {

  @Mock private HomeFragment view;
  @Mock private CrashReport crashReporter;
  @Mock private HomeNavigator homeNavigator;
  @Mock private Home home;
  @Mock private AptoideAccountManager aptoideAccountManager;
  @Mock private Account account;
  @Mock private HomeAnalytics homeAnalytics;

  private HomePresenter presenter;
  private HomeBundlesModel bundlesModel;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private PublishSubject<AppHomeEvent> appClickEvent;
  private PublishSubject<AdHomeEvent> adClickEvent;
  private PublishSubject<HomeEvent> moreClickEvent;
  private PublishSubject<Object> bottomReachedEvent;
  private PublishSubject<Void> pullToRefreshEvent;
  private PublishSubject<Void> retryClickedEvent;
  private HomeBundle localTopAppsBundle;
  private Application aptoide;
  private PublishSubject<Void> imageClickEvent;
  private PublishSubject<Account> accountStatusEvent;
  private PublishSubject<HomeEvent> bundleScrolledEvent;
  private PublishSubject<HomeEvent> knowMoreEvent;
  private PublishSubject<HomeEvent> dismissEvent;
  private PublishSubject<HomeEvent> visibleBundleEvent;

  @Before public void setupHomePresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();
    appClickEvent = PublishSubject.create();
    adClickEvent = PublishSubject.create();
    moreClickEvent = PublishSubject.create();
    bottomReachedEvent = PublishSubject.create();
    pullToRefreshEvent = PublishSubject.create();
    retryClickedEvent = PublishSubject.create();
    imageClickEvent = PublishSubject.create();
    bundleScrolledEvent = PublishSubject.create();
    accountStatusEvent = PublishSubject.create();
    knowMoreEvent = PublishSubject.create();
    dismissEvent = PublishSubject.create();
    visibleBundleEvent = PublishSubject.create();

    presenter = new HomePresenter(view, home, Schedulers.immediate(), crashReporter, homeNavigator,
        new AdMapper(), aptoideAccountManager, homeAnalytics);
    aptoide =
        new Application("Aptoide", "http://via.placeholder.com/350x150", 0, 1000, "cm.aptoide.pt",
            300, "", false);
    FakeBundleDataSource fakeBundleDataSource = new FakeBundleDataSource();
    bundlesModel = new HomeBundlesModel(fakeBundleDataSource.getFakeBundles(), false, 0);
    localTopAppsBundle = bundlesModel.getList()
        .get(1);

    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
    when(view.appClicked()).thenReturn(appClickEvent);
    when(view.recommendedAppClicked()).thenReturn(appClickEvent);
    when(view.adClicked()).thenReturn(adClickEvent);
    when(view.moreClicked()).thenReturn(moreClickEvent);
    when(view.reachesBottom()).thenReturn(bottomReachedEvent);
    when(view.refreshes()).thenReturn(pullToRefreshEvent);
    when(view.retryClicked()).thenReturn(retryClickedEvent);
    when(view.imageClick()).thenReturn(imageClickEvent);
    when(view.bundleScrolled()).thenReturn(bundleScrolledEvent);
    when(aptoideAccountManager.accountStatus()).thenReturn(accountStatusEvent);
    when(view.infoBundleKnowMoreClicked()).thenReturn(knowMoreEvent);
    when(view.dismissBundleClicked()).thenReturn(dismissEvent);
    when(view.visibleBundles()).thenReturn(visibleBundleEvent);
  }

  @Test public void loadAllBundlesFromRepositoryAndLoadIntoView() {
    //Given an initialised HomePresenter
    presenter.onCreateLoadBundles();
    //When the user clicks the Home menu item
    //And loading of bundlesModel are requested
    when(home.loadHomeBundles()).thenReturn(Single.just(bundlesModel));
    when(home.shouldLoadNativeAd()).thenReturn(Single.just(false));
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the progress indicator should be shown
    verify(view).showLoading();
    //Then the home should be displayed
    verify(view).showBundles(bundlesModel.getList());
    //Then the progress indicator should be hidden
    verify(view).hideLoading();
  }

  @Test public void errorLoadingBundles_ShowsError() {
    //Given an initialised HomePresenter
    presenter.onCreateLoadBundles();
    //When the loading of bundlesModel is requested
    //And an unexpected error occured
    when(home.loadHomeBundles()).thenReturn(
        Single.just(new HomeBundlesModel(HomeBundlesModel.Error.GENERIC)));
    when(home.shouldLoadNativeAd()).thenReturn(Single.just(false));
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the generic error message should be shown in the UI
    verify(view).showGenericError();
  }

  @Test public void errorLoadingBundles_ShowsNetworkError() {
    //Given an initialised HomePresenter
    presenter.onCreateLoadBundles();
    //When the loading of bundlesModel is requested
    //And an unexpected error occured
    when(home.loadHomeBundles()).thenReturn(
        Single.just(new HomeBundlesModel(HomeBundlesModel.Error.NETWORK)));
    when(home.shouldLoadNativeAd()).thenReturn(Single.just(false));
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the generic error message should be shown in the UI
    verify(view).showNetworkError();
  }

  @Test public void appClicked_NavigateToAppView() {
    //Given an initialised HomePresenter
    presenter.handleAppClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When an app is clicked
    appClickEvent.onNext(new AppHomeEvent(aptoide, 1, localTopAppsBundle, 3, HomeEvent.Type.APP));
    //then it should navigate to the App's detail View
    verify(homeNavigator).navigateToAppView(aptoide.getAppId(), aptoide.getPackageName(),
        aptoide.getTag());
  }

  @Test public void adClicked_NavigateToAppView() {

    AdHomeEvent event = createAdHomeEvent();

    //Given an initialised HomePresenter
    presenter.handleAdClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When an app is clicked
    adClickEvent.onNext(event);
    //then it should navigate to the App's detail View
    verify(homeAnalytics).sendAdClickEvent(anyInt(), anyString(), anyInt(), anyString(),
        eq(HomeEvent.Type.AD), eq(ApplicationAd.Network.SERVER));
    verify(homeNavigator).navigateToAppView(any());
  }

  @Test public void recommendsClicked_NavigateToAppView() {
    //Given an initialised HomePresenter
    presenter.handleRecommendedAppClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When an app is clicked
    appClickEvent.onNext(
        new AppHomeEvent(aptoide, 3, localTopAppsBundle, 0, HomeEvent.Type.SOCIAL_CLICK));
    //then it should navigate to the App's detail View
    verify(homeNavigator).navigateToRecommendsAppView(aptoide.getAppId(), aptoide.getPackageName(),
        aptoide.getTag(), HomeEvent.Type.SOCIAL_CLICK);
  }

  @Test public void moreClicked_NavigateToActionView() {
    HomeEvent click = new HomeEvent(localTopAppsBundle, 0, HomeEvent.Type.MORE);
    //Given an initialised HomePresenter
    presenter.handleMoreClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When more in a bundle is clicked
    moreClickEvent.onNext(click);
    //Then it should send a more clicked analytics event
    verify(homeAnalytics).sendTapOnMoreInteractEvent(0, localTopAppsBundle.getTag(),
        localTopAppsBundle.getContent()
            .size());
    //Then it should navigate with the specific action behaviour
    verify(homeNavigator).navigateWithAction(click);
  }

  @Test public void bottomReached_ShowNextBundles() {
    //Given an initialised presenter with already loaded bundlesModel into the UI before
    presenter.handleBottomReached();
    when(home.loadNextHomeBundles()).thenReturn(Single.just(bundlesModel));
    when(home.hasMore()).thenReturn(true);
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When scrolling to the end of the view is reached
    //And there are more bundlesModel available to load
    bottomReachedEvent.onNext(new Object());
    //Then it should show the load more progress indicator
    verify(view).showLoadMore();
    //Then it should request the next bundlesModel to the bundlesModel repository
    verify(home).loadNextHomeBundles();
    //Then it should send a endless scroll analytics event
    verify(homeAnalytics).sendLoadMoreInteractEvent();
    //Then it should hide the load more progress indicator
    verify(view).hideShowMore();
    //Then it should show the view again with old bundlesModel and added bundlesModel, retaining list position
    verify(view).showMoreHomeBundles(bundlesModel.getList());
  }

  @Test public void bottomReached_NoMoreBundlesAvailableToShow() {
    //Given an initialised presenter with already loaded bundlesModel into the UI before
    presenter.handleBottomReached();
    when(home.loadNextHomeBundles()).thenReturn(Single.just(bundlesModel));
    when(home.hasMore()).thenReturn(false);
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When scrolling to the end of the view is reached
    //And there are no more bundlesModel available to load
    bottomReachedEvent.onNext(new Object());
    //Then it should do nothing
    verify(view, never()).showLoadMore();
    verify(home, never()).loadNextHomeBundles();
    verify(view, never()).hideShowMore();
    verify(view, never()).showMoreHomeBundles(bundlesModel.getList());
  }

  @Test public void pullToRefresh_GetFreshBundles() {
    //Given an initialised presenter with already loaded bundlesModel into the UI before
    presenter.handlePullToRefresh();
    when(home.loadFreshHomeBundles()).thenReturn(Single.just(bundlesModel));
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When pull to refresh is done
    pullToRefreshEvent.onNext(null);
    //Then a pull refresh analytics event should be sent
    verify(homeAnalytics).sendPullRefreshInteractEvent();
    //Then the progress indicator should be hidden
    verify(view).hideRefresh();
  }

  @Test public void retryClicked_LoadNextBundles() {
    //Given an initialised presenter with already loaded bundlesModel into the UI before
    presenter.handleRetryClick();
    when(home.loadNextHomeBundles()).thenReturn(Single.just(bundlesModel));
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When pull to refresh is done
    retryClickedEvent.onNext(null);
    //Then bundles should be shown
    verify(view).showMoreHomeBundles(bundlesModel.getList());
    //Then it should hide the load more indicator (if exists)
    verify(view).hideShowMore();
    //Then it should hide the loading indicator
    verify(view).hideLoading();
  }

  @Test public void loadLoggedInUserImageUserTest() {
    //When the user is logged in
    when(account.getAvatar()).thenReturn("A string");
    when(account.isLoggedIn()).thenReturn(true);
    //Given an initialised HomePresenter
    presenter.loadUserImage();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And AccountManager returns an account
    accountStatusEvent.onNext(account);
    //Then it should show the image
    verify(view).setUserImage("A string");
    verify(view).showAvatar();
  }

  @Test public void loadNotLoggedInUserImageUserTest() {
    //When the user is logged in
    when(account.isLoggedIn()).thenReturn(false);
    //Given an initialised HomePresenter
    presenter.loadUserImage();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And AccountManager returns an account
    accountStatusEvent.onNext(account);
    //Then it should show the image
    verify(view).showAvatar();
  }

  @Test public void handleUserImageClick() {
    //Given an initialised HomePresenter
    presenter.handleUserImageClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When an user clicks the profile image
    imageClickEvent.onNext(null);
    //Then it should navigate to the Settings Fragment
    verify(homeNavigator).navigateToMyAccount();
  }

  @Test public void onBundleScrolledRight_SendScrollEvent() {
    //Given an initialised HomePresenter
    presenter.handleBundleScrolledRight();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When an user scrolls a bundle with items to the right
    bundleScrolledEvent.onNext(new HomeEvent(localTopAppsBundle, 2, HomeEvent.Type.SCROLL_RIGHT));
    //Then a scroll right analytics event should be sent
    verify(homeAnalytics).sendScrollRightInteractEvent(2, localTopAppsBundle.getTag(),
        localTopAppsBundle.getContent()
            .size());
  }

  @Test public void onAppCoinsKnowMoreClick_NavigateToAppCoinsInformationFragment() {
    //Given an initialised HomePresenter
    presenter.handleKnowMoreClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When an user clicks on the KNOW MORE button in the AppCoins onboarding card
    knowMoreEvent.onNext(new HomeEvent(getFakeActionBundle(), 4, HomeEvent.Type.KNOW_MORE));
    //Then it should navigate to the AppCoins wallet information view.
    verify(homeNavigator).navigateToAppCoinsInformationView();
  }

  @Test public void onDismissBundleClick_RemoveBundleFromCacheAndView() {
    //Given an initialised HomePresenter
    presenter.handleDismissClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And a bundle position to be dismissed
    int bundlePositionToBeRemoved = 4;
    ActionBundle bundle = getFakeActionBundle();
    when(home.remove(bundle)).thenReturn(Completable.complete());
    //When an user clicks on the dismiss button in the information bundle
    dismissEvent.onNext(new HomeEvent(bundle, bundlePositionToBeRemoved, HomeEvent.Type.KNOW_MORE));
    //Then it should remove the bundle from the cache and view.
    verify(home).remove(bundle);
    verify(view).hideBundle(bundlePositionToBeRemoved);
  }

  @Test public void onActionBundleSeen_SendImpression() {
    //Given an initialised HomePresenter
    presenter.handleActionBundlesImpression();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And an action bundle
    ActionBundle bundle = getFakeActionBundle();
    HomeEvent event = new HomeEvent(bundle, 1, HomeEvent.Type.KNOW_MORE);
    when(home.actionBundleImpression(bundle)).thenReturn(Completable.complete());
    //When the bundle is visible to the user
    visibleBundleEvent.onNext(event);
    //Then bundle should be marked as read (impression)
    verify(home).actionBundleImpression(bundle);
  }

  @NonNull private ActionBundle getFakeActionBundle() {
    return new ActionBundle("title", HomeBundle.BundleType.INFO_BUNDLE, null, "tag",
        new ActionItem("1", "type", "title", "message", "icon", "url"));
  }

  private AdHomeEvent createAdHomeEvent() {
    GetAdsResponse.Data data = new GetAdsResponse.Data();
    data.setId(0);
    data.setName("name");
    data.setRepo("repo");
    data.setPackageName("packageName");
    data.setMd5sum("md5sum");
    data.setSize(1);
    data.setVercode(2);
    data.setVername("verName");
    data.setIcon("icon");
    data.setDownloads(3);
    data.setStars(4);
    data.setDescription("description");
    data.setAdded(new Date());
    data.setModified(new Date());
    data.setUpdated(new Date());

    GetAdsResponse.Info info = new GetAdsResponse.Info();
    info.setAdId(0);
    info.setAdType("adType");
    info.setCpcUrl("cpcUrl");
    info.setCpdUrl("cpdUrl");
    info.setCpiUrl("cpiUrl");

    GetAdsResponse.Partner partner = new GetAdsResponse.Partner();
    GetAdsResponse.Partner.Data partnerData = new GetAdsResponse.Partner.Data();
    GetAdsResponse.Partner.Info partnerInfo = new GetAdsResponse.Partner.Info();
    partner.setData(partnerData);
    partner.setInfo(partnerInfo);

    GetAdsResponse.Partner tracker = new GetAdsResponse.Partner();
    tracker.setData(partnerData);
    tracker.setInfo(partnerInfo);

    GetAdsResponse.Ad ad = new GetAdsResponse.Ad();
    ad.setData(data);
    ad.setInfo(info);
    ad.setPartner(partner);
    ad.setTracker(tracker);
    AdClick adClick = new AdClick(ad, "tag");

    AdBundle adBundle =
        new AdBundle("title", new AdsTagWrapper(Collections.emptyList(), "tag2"), new Event(),
            "tag3");

    return new AdHomeEvent(adClick, 1, adBundle, 1, HomeEvent.Type.AD);
  }
}
