package cm.aptoide.pt.home;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.View;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HomeContainerPresenterTest {

  @Mock private HomeContainerFragment view;
  @Mock private CrashReport crashReporter;
  @Mock private HomeNavigator homeNavigator;
  @Mock private Home home;
  @Mock private AptoideAccountManager aptoideAccountManager;
  @Mock private Account account;
  @Mock private HomeAnalytics homeAnalytics;
  @Mock private HomeContainerNavigator homeContainerNavigator;
  @Mock private ChipManager chipManager;

  private HomeContainerPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;

  @Before public void setupHomePresenter() {
    MockitoAnnotations.initMocks(this);
    lifecycleEvent = PublishSubject.create();

    presenter = new HomeContainerPresenter(view, Schedulers.immediate(), aptoideAccountManager,
        homeContainerNavigator, homeNavigator, homeAnalytics, home, chipManager);
    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
    when(view.toolbarUserClick()).thenReturn(Observable.just(null));
    when(aptoideAccountManager.accountStatus()).thenReturn(Observable.just(account));
  }

  @Test public void loadLoggedInUserImageUserTest() {
    //When the user is logged in
    when(account.getAvatar()).thenReturn("A string");
    when(account.isLoggedIn()).thenReturn(true);
    //Given an initialised HomePresenter
    presenter.loadUserImage();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And AccountManager returns an account
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
    //Then it should show the image
    verify(view).showAvatar();
  }

  @Test public void handleUserImageClickTest() {
    //Given an initialised HomePresenter
    presenter.handleUserImageClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then it should navigate to the Settings Fragment
    verify(homeNavigator).navigateToMyAccount();
  }

  @Test public void hasPromotionAppsAndDialog_checkForPromotionAppsTest() {
    HomePromotionsWrapper homePromotionsWrapper =
        new HomePromotionsWrapper("", "", true, 2, 20f, true, "");
    when(home.hasPromotionApps()).thenReturn(Single.just(homePromotionsWrapper));

    presenter.checkForPromotionApps();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).showPromotionsHomeIcon(homePromotionsWrapper);
    verify(homeAnalytics).sendPromotionsImpressionEvent();
    verify(homeAnalytics).sendPromotionsDialogImpressionEvent();
    verify(home).setPromotionsDialogShown();
    verify(view).showPromotionsHomeDialog(homePromotionsWrapper);
  }

  @Test public void hasPromotionAppsNoDialog_CheckForPromotionAppsTest() {
    HomePromotionsWrapper homePromotionsWrapper =
        new HomePromotionsWrapper("", "", true, 2, 20f, false, "");
    when(home.hasPromotionApps()).thenReturn(Single.just(homePromotionsWrapper));

    presenter.checkForPromotionApps();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).showPromotionsHomeIcon(homePromotionsWrapper);
    verify(homeAnalytics).sendPromotionsImpressionEvent();
  }

  @Test public void handleClickOnPromotionsDialogContinueTest() {
    when(view.promotionsHomeDialogClicked()).thenReturn(Observable.just("navigate"));

    presenter.handleClickOnPromotionsDialogContinue();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(homeAnalytics).sendPromotionsDialogNavigateEvent();
    verify(view).dismissPromotionsDialog();
    verify(homeNavigator).navigateToPromotions();
  }

  @Test public void handleClickOnPromotionsDialogCancelTest() {
    when(view.promotionsHomeDialogClicked()).thenReturn(Observable.just("cancel"));

    presenter.handleClickOnPromotionsDialogCancel();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(homeAnalytics).sendPromotionsDialogDismissEvent();
    verify(view).dismissPromotionsDialog();
  }

  @Test public void handleLoggedInAcceptTermsAndConditionsTest() {
    when(account.isLoggedIn()).thenReturn(true);
    when(account.acceptedPrivacyPolicy()).thenReturn(false);
    when(account.acceptedTermsAndConditions()).thenReturn(true);

    presenter.handleLoggedInAcceptTermsAndConditions();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).showTermsAndConditionsDialog();
  }

  @Test public void handleTermsAndConditionsContinueClickedTest() {
    when(view.gdprDialogClicked()).thenReturn(Observable.just("continue"));
    when(aptoideAccountManager.updateTermsAndConditions()).thenReturn(Completable.complete());
    presenter.handleTermsAndConditionsContinueClicked();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(aptoideAccountManager).updateTermsAndConditions();
  }

  @Test public void handleTermsAndConditionsLogOutClickedTest() {
    when(view.gdprDialogClicked()).thenReturn(Observable.just("logout"));
    when(aptoideAccountManager.logout()).thenReturn(Completable.complete());

    presenter.handleTermsAndConditionsLogOutClicked();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(aptoideAccountManager).logout();
  }

  @Test public void handleClickOnTermsAndConditionsTest() {
    when(view.gdprDialogClicked()).thenReturn(Observable.just("terms"));

    presenter.handleClickOnTermsAndConditions();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(homeNavigator).navigateToTermsAndConditions();
  }

  @Test public void handleClickOnPrivacyPolicyTest() {
    when(view.gdprDialogClicked()).thenReturn(Observable.just("privacy"));
    presenter.handleClickOnPrivacyPolicy();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(homeNavigator).navigateToPrivacyPolicy();
  }

  @Test public void gamesChipChecked_loadHomeMainContentTest() {
    when(view.isChipChecked()).thenReturn(Observable.just(HomeContainerFragment.ChipsEvents.GAMES));
    presenter.loadMainHomeContent();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(homeContainerNavigator).loadGamesHomeContent();
  }

  @Test public void noChipsChecked_loadHomeMainContentTest() {
    when(view.isChipChecked()).thenReturn(Observable.just(HomeContainerFragment.ChipsEvents.HOME));
    presenter.loadMainHomeContent();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(homeContainerNavigator).loadMainHomeContent();
  }

  @Test public void appsChipChecked_loadHomeMainContentTest() {
    when(view.isChipChecked()).thenReturn(Observable.just(HomeContainerFragment.ChipsEvents.APPS));
    presenter.loadMainHomeContent();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(homeContainerNavigator).loadAppsHomeContent();
  }

  @Test public void gamesChipIsChecked_handleClickOnGamesChipTest() {
    when(view.gamesChipClicked()).thenReturn(Observable.just(false));
    presenter.handleClickOnGamesChip();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(homeAnalytics).sendChipInteractEvent(ChipManager.Chip.GAMES.getName());
    verify(homeAnalytics).sendChipHomeInteractEvent(ChipManager.Chip.GAMES.getName());

    verify(homeContainerNavigator).loadMainHomeContent();
  }

  @Test public void gamesChipIsNotChecked_handleClickOnGamesChipTest() {
    when(view.gamesChipClicked()).thenReturn(Observable.just(true));
    presenter.handleClickOnGamesChip();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(homeAnalytics).sendChipInteractEvent(ChipManager.Chip.GAMES.getName());
    verify(homeAnalytics).sendChipHomeInteractEvent(ChipManager.Chip.GAMES.getName());

    verify(homeContainerNavigator).loadGamesHomeContent();
  }

  @Test public void appsChipIsChecked_handleClickOnAppsChipTest() {
    when(view.appsChipClicked()).thenReturn(Observable.just(false));
    presenter.handleClickOnAppsChip();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(homeAnalytics).sendChipInteractEvent(ChipManager.Chip.APPS.getName());
    verify(homeAnalytics).sendChipHomeInteractEvent(ChipManager.Chip.APPS.getName());

    verify(homeContainerNavigator).loadMainHomeContent();
  }

  @Test public void appsChipIsNotChecked_handleClickOnAppsChipTest() {
    when(view.appsChipClicked()).thenReturn(Observable.just(true));
    presenter.handleClickOnAppsChip();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(homeAnalytics).sendChipInteractEvent(ChipManager.Chip.APPS.getName());
    verify(homeAnalytics).sendChipHomeInteractEvent(ChipManager.Chip.APPS.getName());

    verify(homeContainerNavigator).loadAppsHomeContent();
  }
}

