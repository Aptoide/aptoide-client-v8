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
import cm.aptoide.pt.promotions.Promotion;
import cm.aptoide.pt.promotions.PromotionsNavigator;
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
  @Mock private PromotionsNavigator promotionsNavigator;

  private AppViewPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private AppModel appModel;
  private AppModel errorAppModel;
  private DownloadAppViewModel downloadAppViewModel;

  @Before public void setupAppViewPresenter() {
    MockitoAnnotations.initMocks(this);
    presenter = new AppViewPresenter(view, accountNavigator, appViewAnalytics, campaignAnalytics,
        appViewNavigator, appViewManager, accountManager, Schedulers.immediate(), crashReporter,
        permissionManager, permissionService, promotionsNavigator);

    lifecycleEvent = PublishSubject.create();

    Malware malware = new Malware();
    malware.setRank(Malware.Rank.CRITICAL);
    List<String> bdsFlags = new ArrayList<>();

    appModel =
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
            "marketName", false, false, bdsFlags, "", "");

    DownloadModel downloadModel =
        new DownloadModel(DownloadModel.Action.INSTALL, 0, DownloadModel.DownloadState.ACTIVE,
            null);

    downloadAppViewModel = new DownloadAppViewModel(downloadModel, new SimilarAppsViewModel(),
        new SimilarAppsViewModel(), new AppCoinsViewModel());

    errorAppModel = new AppModel(DetailedAppRequestResult.Error.GENERIC);

    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
  }

  @Test public void handleLoadDownloadAppViewModel() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //When the app model is requested
    when(appViewManager.loadAppViewModel()).thenReturn(Single.just(appModel));

    //When the appCoinsInformation is requested
    when(appViewManager.loadAppCoinsInformation()).thenReturn(Completable.complete());

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(appModel.getMd5(), appModel.getPackageName(),
        appModel.getVersionCode(), appModel.isPaid(), appModel.getPay(), appModel.getSignature(),
        appModel.getStore()
            .getId(), appModel.hasAdvertising() || appModel.hasBilling())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the loading should be shown
    verify(view).showLoading();
    //Then should set the download information
    verify(view).showDownloadAppModel(appViewModel.getDownloadModel(),
        appViewModel.getAppCoinsViewModel());
    //Then should set the download ready to download
    verify(view).readyToDownload();
  }

  @Test public void handleLoadAppViewNoError() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewModel()).thenReturn(Single.just(appModel));

    //When the appCoinsInformation is requested
    when(appViewManager.loadAppCoinsInformation()).thenReturn(Completable.complete());

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(appModel.getMd5(), appModel.getPackageName(),
        appModel.getVersionCode(), appModel.isPaid(), appModel.getPay(), appModel.getSignature(),
        appModel.getStore()
            .getId(), appModel.hasAdvertising() || appModel.hasBilling())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //then the loading should be shown
    verify(view).showLoading();
    //and the view should populated
    verify(view).showAppView(appModel);
  }

  @Test public void handleLoadAppViewWithError() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewModel()).thenReturn(Single.just(errorAppModel));

    //When the appCoinsInformation is requested
    when(appViewManager.loadAppCoinsInformation()).thenReturn(Completable.complete());

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(errorAppModel.getMd5(),
        errorAppModel.getPackageName(), errorAppModel.getVersionCode(), errorAppModel.isPaid(),
        errorAppModel.getPay(), errorAppModel.getSignature(), errorAppModel.getStore()
            .getId(), errorAppModel.hasAdvertising() || errorAppModel.hasBilling())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //then the loading should be shown
    verify(view).showLoading();
    //the view should not be populated with the app info
    verify(view, never()).showAppView(errorAppModel);
    //and the error should be handled
    verify(view).handleError(errorAppModel.getError());
  }

  @Test public void handleOpenAppViewEventsWithEditorsChoice() {
    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewModel()).thenReturn(Single.just(appModel));

    //When the appCoinsInformation is requested
    when(appViewManager.loadAppCoinsInformation()).thenReturn(Completable.complete());

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(appModel.getMd5(), appModel.getPackageName(),
        appModel.getVersionCode(), appModel.isPaid(), appModel.getPay(), appModel.getSignature(),
        appModel.getStore()
            .getId(), appModel.hasAdvertising() || appModel.hasBilling())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then editors choice click event should not be sent
    verify(appViewManager).sendEditorsChoiceClickEvent(appModel.getPackageName(),
        appModel.getEditorsChoice());
    //and app view opened from event should be sent
    verify(appViewManager).sendAppViewOpenedFromEvent(appModel.getPackageName(),
        appModel.getDeveloper()
            .getName(), appModel.getMalware()
            .getRank()
            .name(), appModel.hasBilling(), appModel.hasAdvertising());
  }

  @Test public void handleOpenAppViewEventsWithEmptyEditorsChoice() {
    Malware malware = new Malware();
    malware.setRank(Malware.Rank.CRITICAL);
    List<String> bdsFlags = new ArrayList<>();

    AppModel emptyEditorsChoiceAppModel =
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
            AppViewFragment.OpenType.OPEN_ONLY, 0, null, "", "origin", false, "marketName", false,
            false, bdsFlags, "", "");

    //Given an initialized presenter
    presenter.handleFirstLoad();
    //when the app model is requested
    when(appViewManager.loadAppViewModel()).thenReturn(Single.just(emptyEditorsChoiceAppModel));

    //When the appCoinsInformation is requested
    when(appViewManager.loadAppCoinsInformation()).thenReturn(Completable.complete());

    //when the download model is requested
    when(appViewManager.loadDownloadAppViewModel(emptyEditorsChoiceAppModel.getMd5(),
        emptyEditorsChoiceAppModel.getPackageName(), emptyEditorsChoiceAppModel.getVersionCode(),
        emptyEditorsChoiceAppModel.isPaid(), emptyEditorsChoiceAppModel.getPay(),
        appModel.getSignature(), appModel.getStore()
            .getId(), appModel.hasAdvertising() || appModel.hasBilling())).thenReturn(
        Observable.just(downloadAppViewModel));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then editors choice click event should not be sent
    verify(appViewManager, never()).sendEditorsChoiceClickEvent(
        emptyEditorsChoiceAppModel.getPackageName(), emptyEditorsChoiceAppModel.getEditorsChoice());
    //and app view opened from event should be sent
    verify(appViewManager).sendAppViewOpenedFromEvent(emptyEditorsChoiceAppModel.getPackageName(),
        emptyEditorsChoiceAppModel.getDeveloper()
            .getName(), emptyEditorsChoiceAppModel.getMalware()
            .getRank()
            .name(), emptyEditorsChoiceAppModel.hasBilling(),
        emptyEditorsChoiceAppModel.hasAdvertising());
  }

  @Test public void handleAppcPromotionTest() {
    Malware malware = new Malware();
    malware.setRank(Malware.Rank.CRITICAL);
    List<String> bdsFlags = new ArrayList<>();
    AppModel emptyEditorsChoiceAppModel =
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
            AppViewFragment.OpenType.OPEN_ONLY, 0, null, "", "origin", false, "marketName", true,
            false, bdsFlags, "", "");
    Promotion promotion = new Promotion(false, 10f, "cm.aptoide.pt", "install_prom",
        Collections.singletonList(Promotion.ClaimAction.INSTALL));
    PromotionViewModel promotionViewModel = new PromotionViewModel();
    promotionViewModel.setPromotions(Collections.singletonList(promotion));
    when(view.isAppViewReadyToDownload()).thenReturn(Observable.just(null));
    when(appViewManager.loadAppViewModel()).thenReturn(Single.just(emptyEditorsChoiceAppModel));
    when(appViewManager.loadPromotionViewModel()).thenReturn(Observable.just(promotionViewModel));
    when(appViewManager.isAppcPromotionImpressionSent()).thenReturn(false);
    when(appViewManager.getClaimablePromotion(promotionViewModel.getPromotions(),
        Promotion.ClaimAction.INSTALL)).thenReturn(promotion);

    presenter.handleAppcPromotion();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).showAppcWalletPromotionView(promotion, promotionViewModel.getWalletApp(),
        Promotion.ClaimAction.INSTALL, null);
    verify(appViewAnalytics).sendPromotionImpression(promotion.getPromotionId());
    verify(appViewManager).setAppcPromotionImpressionSent();
  }
}
