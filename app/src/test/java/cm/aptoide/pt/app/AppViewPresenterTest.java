package cm.aptoide.pt.app;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.app.view.AppCoinsViewModel;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.app.view.AppViewNavigator;
import cm.aptoide.pt.app.view.AppViewPresenter;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.Malware;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.app.AppDeveloper;
import cm.aptoide.pt.view.app.AppFlags;
import cm.aptoide.pt.view.app.AppMedia;
import cm.aptoide.pt.view.app.AppRating;
import cm.aptoide.pt.view.app.DetailedAppRequestResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Completable;
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

  @Mock private AppViewFragment view;
  @Mock private PermissionManager permissionManager;
  @Mock private PermissionService permissionService;
  @Mock private AppViewAnalytics appViewAnalytics;
  @Mock private AccountNavigator accountNavigator;
  @Mock private AppViewNavigator appViewNavigator;
  @Mock private AppViewManager appViewManager;
  @Mock private AptoideAccountManager accountManager;
  @Mock private CrashReport crashReporter;
  @Mock private CampaignAnalytics campaignAnalytics;

  private AppViewPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private AppViewViewModel appViewViewModel;
  private AppViewViewModel errorAppViewViewModel;
  private DownloadAppViewModel downloadAppViewModel;

  @Before public void setupAppViewPresenter() {
    MockitoAnnotations.initMocks(this);
    presenter = new AppViewPresenter(view, accountNavigator, appViewAnalytics, campaignAnalytics,
        appViewNavigator, appViewManager, accountManager, Schedulers.immediate(), crashReporter,
        permissionManager, permissionService);

    lifecycleEvent = PublishSubject.create();

    Malware malware = new Malware();
    malware.setRank(Malware.Rank.CRITICAL);
    List<String> bdsFlags = new ArrayList<>();

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
            AppViewFragment.OpenType.OPEN_ONLY, 0, null, "editorsChoice", "origin", false,
            "marketName", false, false, bdsFlags, "");

    DownloadModel downloadModel =
        new DownloadModel(DownloadModel.Action.INSTALL, 0, DownloadModel.DownloadState.ACTIVE,
            null);

    downloadAppViewModel = new DownloadAppViewModel(downloadModel, new SimilarAppsViewModel(),
        new AppCoinsViewModel());

    errorAppViewViewModel = new AppViewViewModel(DetailedAppRequestResult.Error.GENERIC);

    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
  }

  @Test public void handleLoadDownloadAppViewModel() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //When the app model is requested
    when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(appViewViewModel));

    //When the appCoinsInformation is requested
    when(appViewManager.loadAppCoinsInformation()).thenReturn(Completable.complete());

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(appViewViewModel.getMd5(),
        appViewViewModel.getPackageName(), appViewViewModel.getVersionCode(),
        appViewViewModel.isPaid(), appViewViewModel.getPay())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the loading should be shown
    verify(view).showLoading();
    //Then should set the download information
    verify(view).showDownloadAppModel(downloadAppViewModel, false);
    //Then should set the download ready to download
    verify(view).readyToDownload();
  }

  @Test public void handleLoadAppViewNoError() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(appViewViewModel));

    //When the appCoinsInformation is requested
    when(appViewManager.loadAppCoinsInformation()).thenReturn(Completable.complete());

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(appViewViewModel.getMd5(),
        appViewViewModel.getPackageName(), appViewViewModel.getVersionCode(),
        appViewViewModel.isPaid(), appViewViewModel.getPay())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //then the loading should be shown
    verify(view).showLoading();
    //and the view should populated
    verify(view).showAppView(appViewViewModel);
  }

  @Test public void handleLoadAppViewWithError() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(errorAppViewViewModel));

    //When the appCoinsInformation is requested
    when(appViewManager.loadAppCoinsInformation()).thenReturn(Completable.complete());

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(errorAppViewViewModel.getMd5(),
        errorAppViewViewModel.getPackageName(), errorAppViewViewModel.getVersionCode(),
        errorAppViewViewModel.isPaid(), errorAppViewViewModel.getPay())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //then the loading should be shown
    verify(view).showLoading();
    //the view should not be populated with the app info
    verify(view, never()).showAppView(errorAppViewViewModel);
    //and the error should be handled
    verify(view).handleError(errorAppViewViewModel.getError());
  }

  @Test public void handleOpenAppViewEventsWithEditorsChoice() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewViewModel()).thenReturn(Single.just(appViewViewModel));

    //When the appCoinsInformation is requested
    when(appViewManager.loadAppCoinsInformation()).thenReturn(Completable.complete());

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
            .name(), appViewViewModel.hasBilling(), appViewViewModel.hasAdvertising());
  }

  @Test public void handleOpenAppViewEventsWithEmptyEditorsChoice() {
    Malware malware = new Malware();
    malware.setRank(Malware.Rank.CRITICAL);
    List<String> bdsFlags = new ArrayList<>();

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
            AppViewFragment.OpenType.OPEN_ONLY, 0, null, "", "origin", false, "marketName", false,
            false, bdsFlags, "");

    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewViewModel()).thenReturn(
        Single.just(emptyEditorsChoiceAppViewViewModel));

    //When the appCoinsInformation is requested
    when(appViewManager.loadAppCoinsInformation()).thenReturn(Completable.complete());

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
            .name(), emptyEditorsChoiceAppViewViewModel.hasBilling(),
        emptyEditorsChoiceAppViewViewModel.hasAdvertising());
  }
}
