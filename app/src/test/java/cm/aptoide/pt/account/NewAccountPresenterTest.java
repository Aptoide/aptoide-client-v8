package cm.aptoide.pt.account;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.model.v7.store.StoreUserAbstraction;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.settings.NewAccountFragment;
import cm.aptoide.pt.view.settings.NewAccountNavigator;
import cm.aptoide.pt.view.settings.NewAccountPresenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by franciscocalado on 26/04/18.
 */

public class NewAccountPresenterTest {

  private static final int EDIT_STORE_REQUEST_CODE = 1230;
  @Mock Account account;
  @Mock GetStore getStore;
  @Mock Store store;
  @Mock cm.aptoide.accountmanager.Store amStore;
  @Mock StoreUserAbstraction.Nodes nodes;
  @Mock GetStoreMeta getMeta;
  @Mock SharedPreferences.Editor editor;
  @Mock cm.aptoide.accountmanager.Store accountManagerStore;
  @Mock private NewAccountFragment view;
  @Mock private AptoideAccountManager accountManager;
  @Mock private CrashReport crashReport;
  @Mock private SharedPreferences sharedPreferences;
  @Mock private NewAccountNavigator navigator;
  @Mock private AccountAnalytics analytics;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private NewAccountPresenter newAccountPresenter;

  @Before public void setupNewAccountPresenter() {
    MockitoAnnotations.initMocks(this);
    lifecycleEvent = PublishSubject.create();
    newAccountPresenter =
        new NewAccountPresenter(view, accountManager, crashReport, sharedPreferences,
            Schedulers.immediate(), navigator, analytics);

    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void populateAccountViewsTest() {

    newAccountPresenter.populateAccountViews();
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).showAccount(account);
  }

  @Test public void checkIfStoreIsInvalidAndRefreshTest() {

    newAccountPresenter.checkIfStoreIsInvalidAndRefresh();

    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getStore()).thenReturn(amStore);
    when(amStore.getId()).thenReturn(0L);
    when(account.hasStore()).thenReturn(true);
    when(view.getStore()).thenReturn(Observable.just(getStore));
    when(getStore.getNodes()).thenReturn(nodes);
    when(nodes.getMeta()).thenReturn(getMeta);
    when(getMeta.getData()).thenReturn(store);

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).refreshUI(store);
    verify(accountManager).updateAccount();
  }

  @Test public void handleLoginClickTest() {
    when(view.loginClick()).thenReturn(Observable.just(null));

    newAccountPresenter.handleLoginClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(navigator).navigateToLoginView(AccountAnalytics.AccountOrigins.MY_ACCOUNT);
  }

  @Test public void handleLogoutClickTest() {
    when(view.signOutClick()).thenReturn(Observable.just(null));
    when(accountManager.logout()).thenReturn(Completable.complete());
    when(sharedPreferences.edit()).thenReturn(editor);
    when(editor.putBoolean(anyString(), eq(false))).thenReturn(editor);

    newAccountPresenter.handleLogOutClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(sharedPreferences).edit();
    verify(editor, times(3)).putBoolean(anyString(), eq(false));
    verify(editor).apply();
    verify(view).showLoginAccountDisplayable();
  }

  @Test public void handleCreateStoreClick() {
    when(view.createStoreClick()).thenReturn(Observable.just(null));

    newAccountPresenter.handleCreateStoreClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(navigator).navigateToCreateStore();
  }

  @Test public void handleFindFriendsClickTest() {
    when(view.findFriendsClick()).thenReturn(Observable.just(null));

    newAccountPresenter.handleFindFriendsClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(analytics).sendFollowFriendsClickEvent();
    verify(navigator).navigateToFindFriends();
  }

  @Test public void handleStoreEditClickTest() {
    when(view.editStoreClick()).thenReturn(Observable.just(null));
    when(view.getStore()).thenReturn(Observable.just(getStore));
    when(getStore.getNodes()).thenReturn(nodes);
    when(nodes.getMeta()).thenReturn(getMeta);
    when(getMeta.getData()).thenReturn(store);

    newAccountPresenter.handleStoreEditClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(navigator).navigateToEditStoreView(store, EDIT_STORE_REQUEST_CODE);
  }

  @Test public void handleStoreEditResultTest() {
    when(navigator.editStoreResult(EDIT_STORE_REQUEST_CODE)).thenReturn(Observable.just(null));
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));

    newAccountPresenter.handleStoreEditResult();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    InOrder order = Mockito.inOrder(navigator, view);
    order.verify(navigator)
        .editStoreResult(EDIT_STORE_REQUEST_CODE);
    order.verify(view)
        .showAccount(account);
  }

  @Test public void handleStoreDisplayableClickTest() {
    newAccountPresenter.handleStoreDisplayableClick();

    when(view.storeClick()).thenReturn(Observable.just(null));
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getStore()).thenReturn(accountManagerStore);
    when(accountManagerStore.getName()).thenReturn("name");
    when(accountManagerStore.getTheme()).thenReturn("theme");

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(navigator).navigateToStoreView("name", "theme");
  }

  @Test public void handleProfileEditClickTest() {
    newAccountPresenter.handleProfileEditClick();

    when(view.editUserProfileClick()).thenReturn(Observable.just(null));
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(navigator).navigateToEditProfileView();
  }

  @Test public void handleProfileDisplayableClickTest() {
    newAccountPresenter.handleProfileDisplayableClick();

    when(view.userClick()).thenReturn(Observable.just(null));
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getId()).thenReturn("id");
    when(account.getStore()).thenReturn(accountManagerStore);
    when(accountManagerStore.getTheme()).thenReturn("theme");

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(navigator).navigateToUserView("id", "theme");
  }

  @Test public void handleSettingsClickedTest() {
    newAccountPresenter.handleSettingsClicked();

    when(view.settingsClicked()).thenReturn(Observable.just(null));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(navigator).navigateToSettings();
  }

  @Test public void handleNotificationHistoryClickedTest() {
    newAccountPresenter.handleNotificationHistoryClicked();

    when(view.notificationsClicked()).thenReturn(Observable.just(null));

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(navigator).navigateToNotificationHistory();
  }
}
