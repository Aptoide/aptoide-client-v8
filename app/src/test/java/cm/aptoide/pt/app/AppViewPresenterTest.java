package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.view.AppViewNavigator;
import cm.aptoide.pt.app.view.AppViewPresenter;
import cm.aptoide.pt.app.view.NewAppViewFragment;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.app.AppDeveloper;
import cm.aptoide.pt.view.app.AppFlags;
import cm.aptoide.pt.view.app.AppMedia;
import cm.aptoide.pt.view.app.AppRating;
import cm.aptoide.pt.view.app.DetailedAppRequestResult;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by filipegoncalves on 6/27/18.
 */

public class AppViewPresenterTest {

  @Mock private NewAppViewFragment view;
  @Mock private PermissionManager permissionManager;
  @Mock private PermissionService permissionService;
  @Mock private AppViewAnalytics appViewAnalytics;
  @Mock private AccountNavigator accountNavigator;
  @Mock private AppViewNavigator appViewNavigator;
  @Mock private AppViewManager appViewManager;
  @Mock private AptoideAccountManager accountManager;
  @Mock private CrashReport crashReporter;
  @Mock private Account account;

  private AppViewPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private PublishSubject<Account> accountStatusEvent;
  private AppViewViewModel appViewViewModel;
  private AppViewViewModel errorAppViewViewModel;
  private DownloadAppViewModel downloadAppViewModel;
  private SimilarAppsViewModel similarAppsViewModel;

  @Before public void setupAppViewPresenter() {
    MockitoAnnotations.initMocks(this);
    presenter = new AppViewPresenter(view, accountNavigator, appViewAnalytics, appViewNavigator,
        appViewManager, accountManager, Schedulers.immediate(), crashReporter, permissionManager,
        permissionService);

    lifecycleEvent = PublishSubject.create();
    accountStatusEvent = PublishSubject.create();

    Malware malware = new Malware();
    malware.setRank(Malware.Rank.CRITICAL);

    appViewViewModel =
        new AppViewViewModel(11, "aptoide", new cm.aptoide.pt.dataprovider.model.v7.store.Store(),
            "", true, malware, new AppFlags("", Collections.emptyList()),
            Collections.<String>emptyList(), Collections.<String>emptyList(),
            Collections.<String>emptyList(), 121312312, "md5dajskdjas", "mypath", "myAltPath",
            12311, "9.0.0", "cm.aptoide.pt", 12311, 100210312,
            new AppRating(0, 100, Collections.emptyList()), 1231231,
            new AppRating(0, 100, Collections.emptyList()),
            new AppDeveloper("Felipao", "felipao@aptoide.com", "privacy", "website"), "graphic",
            "icon", new AppMedia("description", Collections.<String>emptyList(), "news",
            Collections.emptyList(), Collections.emptyList()), "modified", "app added", null, null,
            "weburls", false, false, "paid path", "no", true, "aptoide",
            NewAppViewFragment.OpenType.OPEN_ONLY, 0, null, "editorsChoice", "origin", false,
            "marketName");

    downloadAppViewModel = new DownloadAppViewModel(DownloadAppViewModel.Action.INSTALL, 0,
        DownloadAppViewModel.DownloadState.ACTIVE, null);

    errorAppViewViewModel = new AppViewViewModel(DetailedAppRequestResult.Error.GENERIC);
    // similarAppsViewModel = new SimilarAppsViewModel(
    //new MinimalAd("", 0, "", "", "", 123, 111, "", "aptoide", "www.icones.pt",
    //  "no description available", 1231, 5, Long.valueOf(0)), );

    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void handleLoadDownloadAppViewModel() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //When the app model is requested
    when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(appViewViewModel));
    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(appViewViewModel.getMd5(),
        appViewViewModel.getPackageName(), appViewViewModel.getVersionCode(),
        appViewViewModel.isPaid(), appViewViewModel.getPay())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the loading should be shown
    verify(view).showLoading();
    //Then should set the download information
    verify(view).showDownloadAppModel(downloadAppViewModel);
    //Then should set the download ready to download
    verify(view).readyToDownload();
  }

  @Test public void handleLoadAppViewNoError() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(appViewViewModel));

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(appViewViewModel.getMd5(),
        appViewViewModel.getPackageName(), appViewViewModel.getVersionCode(),
        appViewViewModel.isPaid(), appViewViewModel.getPay())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //then the loading should be shown
    verify(view).showLoading();
    //and the view should populated
    verify(view).populateAppDetails(appViewViewModel);
  }

  @Test public void handleLoadAppViewWithError() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(errorAppViewViewModel));

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(errorAppViewViewModel.getMd5(),
        errorAppViewViewModel.getPackageName(), errorAppViewViewModel.getVersionCode(),
        errorAppViewViewModel.isPaid(), errorAppViewViewModel.getPay())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //then the loading should be shown
    verify(view).showLoading();
    //the view should not be populated with the app info
    verify(view, never()).populateAppDetails(errorAppViewViewModel);
    //and the error should be handled
    verify(view).handleError(errorAppViewViewModel.getError());
  }

  @Test public void handleOpenAppViewEventsWithEditorsChoice() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(appViewViewModel));

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(appViewViewModel.getMd5(),
        appViewViewModel.getPackageName(), appViewViewModel.getVersionCode(),
        appViewViewModel.isPaid(), appViewViewModel.getPay())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then editors choice click event should not be sent
    verify(appViewManager).sendEditorsChoiceClickEvent(appViewViewModel.getPackageName(),
        appViewViewModel.getEditorsChoice());
    //and app view opened from event should be sent
    verify(appViewManager).sendAppViewOpenedFromEvent(appViewViewModel.getPackageName(),
        appViewViewModel.getDeveloper()
            .getName(), appViewViewModel.getMalware()
            .getRank()
            .name(), appViewViewModel.getAppc());
  }

  @Test public void handleOpenAppViewEventsWithEmptyEditorsChoice() {
    Malware malware = new Malware();
    malware.setRank(Malware.Rank.CRITICAL);

    AppViewViewModel emptyEditorsChoiceAppViewViewModel =
        new AppViewViewModel(11, "aptoide", new cm.aptoide.pt.dataprovider.model.v7.store.Store(),
            "", true, malware, new AppFlags("", Collections.emptyList()),
            Collections.<String>emptyList(), Collections.<String>emptyList(),
            Collections.<String>emptyList(), 121312312, "md5dajskdjas", "mypath", "myAltPath",
            12311, "9.0.0", "cm.aptoide.pt", 12311, 100210312,
            new AppRating(0, 100, Collections.emptyList()), 1231231,
            new AppRating(0, 100, Collections.emptyList()),
            new AppDeveloper("Felipao", "felipao@aptoide.com", "privacy", "website"), "graphic",
            "icon", new AppMedia("description", Collections.<String>emptyList(), "news",
            Collections.emptyList(), Collections.emptyList()), "modified", "app added", null, null,
            "weburls", false, false, "paid path", "no", true, "aptoide",
            NewAppViewFragment.OpenType.OPEN_ONLY, 0, null, "", "origin", false, "marketName");

    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewViewModel()).thenReturn(
        Single.just(emptyEditorsChoiceAppViewViewModel));

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(emptyEditorsChoiceAppViewViewModel.getMd5(),
        emptyEditorsChoiceAppViewViewModel.getPackageName(),
        emptyEditorsChoiceAppViewViewModel.getVersionCode(),
        emptyEditorsChoiceAppViewViewModel.isPaid(),
        emptyEditorsChoiceAppViewViewModel.getPay())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then editors choice click event should not be sent
    verify(appViewManager, never()).sendEditorsChoiceClickEvent(
        emptyEditorsChoiceAppViewViewModel.getPackageName(),
        emptyEditorsChoiceAppViewViewModel.getEditorsChoice());
    //and app view opened from event should be sent
    verify(appViewManager).sendAppViewOpenedFromEvent(
        emptyEditorsChoiceAppViewViewModel.getPackageName(),
        emptyEditorsChoiceAppViewViewModel.getDeveloper()
            .getName(), emptyEditorsChoiceAppViewViewModel.getMalware()
            .getRank()
            .name(), emptyEditorsChoiceAppViewViewModel.getAppc());
  }

  /**

   @Test public void handleFirstAppLoadOpenTest() {
   //Given an initialized presenter
   presenter.handleFirstLoad();
   //When the app model is requested
   when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(appViewViewModel));
   //when the download model is requested
   when(appViewManager.loadDownloadAppViewModel(appViewViewModel.getMd5(),
   appViewViewModel.getPackageName(), appViewViewModel.getVersionCode(),
   appViewViewModel.isPaid(), appViewViewModel.getPay())).thenReturn(
   Observable.just(downloadAppViewModel));
   //when the loadSimilar apps is called
   //when(appViewManager.loadSimilarApps(appViewViewModel.getPackageName(),
   //appViewViewModel.getMedia()
   //  .getKeywords())).thenReturn(Single.just());
   //Then the loading should be shown
   verify(view).showLoading();
   //Then should set the download information
   verify(view).showDownloadAppModel(downloadAppViewModel);
   //Then should set the download ready to download
   verify(view).readyToDownload();
   //Then should show all the app details
   verify(view).populateAppDetails(appViewViewModel);
   //Then should send editors choice event
   verify(appViewManager).sendEditorsChoiceClickEvent(appViewViewModel.getPackageName(),
   appViewViewModel.getEditorsChoice());
   //Then should send the open app view event
   verify(appViewManager).sendAppViewOpenedFromEvent(appViewViewModel.getPackageName(),
   appViewViewModel.getDeveloper()
   .getName(), appViewViewModel.getMalware()
   .getRank()
   .name(), appViewViewModel.getAppc());
   //then the view should recover its scroll state
   verify(view).recoverScrollViewState();
   //then update the suggested apps
   //verify(appViewManager).loadSimilarApps();
   //then
   }

   @Test public void handleFirstAppLoadOpenAndInstallTest() {
   //Given an initialized presenter
   presenter.handleFirstLoad();
   //When the app model is requested
   when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(appViewViewModel));
   //when the download model is requested
   when(appViewManager.loadDownloadAppViewModel(appViewViewModel.getMd5(),
   appViewViewModel.getPackageName(), appViewViewModel.getVersionCode(),
   appViewViewModel.isPaid(), appViewViewModel.getPay())).thenReturn(
   Observable.just(downloadAppViewModel));
   //Then the loading should be shown
   verify(view).showLoading();
   //Then should set the download information
   verify(view).showDownloadAppModel(downloadAppViewModel);
   //Then should set the download ready to download
   verify(view).readyToDownload();
   //Then should show all the app details
   verify(view).populateAppDetails(appViewViewModel);
   //Then should send editors choice event
   verify(appViewManager).sendEditorsChoiceClickEvent(appViewViewModel.getPackageName(),
   appViewViewModel.getEditorsChoice());
   //Then should send the open app view event
   verify(appViewManager).sendAppViewOpenedFromEvent(appViewViewModel.getPackageName(),
   appViewViewModel.getDeveloper()
   .getName(), appViewViewModel.getMalware()
   .getRank()
   .name(), appViewViewModel.getAppc());
   //todo handle open and install open type
   //then the view should recover its scroll state
   verify(view).recoverScrollViewState();
   }

   @Test public void handleFirstAppLoadOpenWithInstallPopupTest() {
   //Given an initialized presenter
   presenter.handleFirstLoad();
   //When the app model is requested
   when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(appViewViewModel));
   //when the download model is requested
   when(appViewManager.loadDownloadAppViewModel(appViewViewModel.getMd5(),
   appViewViewModel.getPackageName(), appViewViewModel.getVersionCode(),
   appViewViewModel.isPaid(), appViewViewModel.getPay())).thenReturn(
   Observable.just(downloadAppViewModel));
   //Then the loading should be shown
   verify(view).showLoading();
   //Then should set the download information
   verify(view).showDownloadAppModel(downloadAppViewModel);
   //Then should set the download ready to download
   verify(view).readyToDownload();
   //Then should show all the app details
   verify(view).populateAppDetails(appViewViewModel);
   //Then should send editors choice event
   verify(appViewManager).sendEditorsChoiceClickEvent(appViewViewModel.getPackageName(),
   appViewViewModel.getEditorsChoice());
   //Then should send the open app view event
   verify(appViewManager).sendAppViewOpenedFromEvent(appViewViewModel.getPackageName(),
   appViewViewModel.getDeveloper()
   .getName(), appViewViewModel.getMalware()
   .getRank()
   .name(), appViewViewModel.getAppc());

   //todo handle install popup open type
   //then the view should recover its scroll state
   verify(view).recoverScrollViewState();
   }

   @Test public void handleFirstApkfyInstallPopupTest() {
   //Given an initialized presenter
   presenter.handleFirstLoad();
   //When the app model is requested
   when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(appViewViewModel));
   //when the download model is requested
   when(appViewManager.loadDownloadAppViewModel(appViewViewModel.getMd5(),
   appViewViewModel.getPackageName(), appViewViewModel.getVersionCode(),
   appViewViewModel.isPaid(), appViewViewModel.getPay())).thenReturn(
   Observable.just(downloadAppViewModel));
   //Then the loading should be shown
   verify(view).showLoading();
   //Then should set the download information
   verify(view).showDownloadAppModel(downloadAppViewModel);
   //Then should set the download ready to download
   verify(view).readyToDownload();
   //Then should show all the app details
   verify(view).populateAppDetails(appViewViewModel);
   //Then should send editors choice event
   verify(appViewManager).sendEditorsChoiceClickEvent(appViewViewModel.getPackageName(),
   appViewViewModel.getEditorsChoice());
   //Then should send the open app view event
   verify(appViewManager).sendAppViewOpenedFromEvent(appViewViewModel.getPackageName(),
   appViewViewModel.getDeveloper()
   .getName(), appViewViewModel.getMalware()
   .getRank()
   .name(), appViewViewModel.getAppc());
   //todo handle install popup APKFY
   //then the view should recover its scroll state
   verify(view).recoverScrollViewState();
   }

   **/
  //todo create error case, create no editors choice case, create all open types cases
}
