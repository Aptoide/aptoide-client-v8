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
  @Mock private Account account;
  @Mock private GetStore getStore;
  @Mock private Store store;
  @Mock private cm.aptoide.accountmanager.Store amStore;
  @Mock private StoreUserAbstraction.Nodes nodes;
  @Mock private GetStoreMeta getMeta;
  @Mock private SharedPreferences.Editor editor;
  @Mock private cm.aptoide.accountmanager.Store accountManagerStore;
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
    //Given an initialized MyAccountPresenter
    //And a user Account,
    //When an account is requested
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));

    newAccountPresenter.populateAccountViews();

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the account information is shown in the UI
    verify(view).showAccount(account);
  }

  @Test public void checkIfStoreIsInvalidAndRefreshTest() {
    //Given an initialized MyAccountPresenter
    //And a user Account with Store
    //When the store is not in AccountManager (getId == 0),
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getStore()).thenReturn(amStore);
    when(amStore.getId()).thenReturn(0L);
    when(account.hasStore()).thenReturn(true);
    when(view.getStore()).thenReturn(Observable.just(getStore));
    when(getStore.getNodes()).thenReturn(nodes);
    when(nodes.getMeta()).thenReturn(getMeta);
    when(getMeta.getData()).thenReturn(store);

    newAccountPresenter.checkIfStoreIsInvalidAndRefresh();


    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should update the view
    verify(view).refreshUI(store);
    //Then it should update the accountManager
    verify(accountManager).updateAccount();
  }

  @Test public void handleLoginClickTest() {
    //Given an initialized MyAccountPresenter
    //When a user clicks the login button
    when(view.loginClick()).thenReturn(Observable.just(null));

    newAccountPresenter.handleLoginClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should navigate the user to the login view
    verify(navigator).navigateToLoginView(AccountAnalytics.AccountOrigins.MY_ACCOUNT);
  }

  @Test public void handleLogoutClickTest() {
    //Given an initialized MyAccountPresenter
    //When a user clicks the logout button
    when(view.signOutClick()).thenReturn(Observable.just(null));
    when(accountManager.logout()).thenReturn(Completable.complete());
    when(sharedPreferences.edit()).thenReturn(editor);
    when(editor.putBoolean(anyString(), eq(false))).thenReturn(editor);

    newAccountPresenter.handleLogOutClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then address book related shared preferences should be set to false (reset)
    verify(sharedPreferences).edit();
    verify(editor, times(3)).putBoolean(anyString(), eq(false));
    verify(editor).apply();
    //Then show the login displayables
    verify(view).showLoginAccountDisplayable();
  }

  @Test public void handleCreateStoreClick() {
    //Given an initialized MyAccountPresenter
    //When a user clicks the create store button
    when(view.createStoreClick()).thenReturn(Observable.just(null));

    newAccountPresenter.handleCreateStoreClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the user should navigate to the create store view
    verify(navigator).navigateToCreateStore();
  }

  @Test public void handleFindFriendsClickTest() {
    //Given an initialized MyAccountPresenter
    //When a user clicks the find friends button
    when(view.findFriendsClick()).thenReturn(Observable.just(null));

    newAccountPresenter.handleFindFriendsClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(analytics).sendFollowFriendsClickEvent();
    //Then the user should navigate to the find friends view
    verify(navigator).navigateToFindFriends();
  }

  @Test public void handleStoreEditClickTest() {
    //Given an initialized MyAccountPresenter
    //And a user Account with a store created
    //When the user clicks the "Edit" Button from the store
    when(view.editStoreClick()).thenReturn(Observable.just(null));
    when(view.getStore()).thenReturn(Observable.just(getStore));
    when(getStore.getNodes()).thenReturn(nodes);
    when(nodes.getMeta()).thenReturn(getMeta);
    when(getMeta.getData()).thenReturn(store);

    newAccountPresenter.handleStoreEditClick();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the user should navigate to the edit store view
    verify(navigator).navigateToEditStoreView(store, EDIT_STORE_REQUEST_CODE);
  }

  @Test public void handleStoreEditResultTest() {
    //Given an initialized MyAccountPresenter
    //And an user account
    //When the user navigates back from the edit store screen
    when(navigator.editStoreResult(EDIT_STORE_REQUEST_CODE)).thenReturn(Observable.just(null));
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));

    newAccountPresenter.handleStoreEditResult();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    InOrder order = Mockito.inOrder(navigator, view);
    order.verify(navigator)
        .editStoreResult(EDIT_STORE_REQUEST_CODE);

    //Then the account information is shown in the UI
    order.verify(view)
        .showAccount(account);
  }

  @Test public void handleStoreDisplayableClickTest() {
    //Given an initialized MyAccountPresenter
    //When a user clicks the the store view
    when(view.storeClick()).thenReturn(Observable.just(null));
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getStore()).thenReturn(accountManagerStore);
    when(accountManagerStore.getName()).thenReturn("name");
    when(accountManagerStore.getTheme()).thenReturn("theme");

    newAccountPresenter.handleStoreDisplayableClick();

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the user should navigate to his store view
    verify(navigator).navigateToStoreView("name", "theme");
  }

  @Test public void handleProfileEditClickTest() {
    //Given an initialized MyAccountPresenter
    //When a user clicks the edit profile button
    when(view.editUserProfileClick()).thenReturn(Observable.just(null));
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));

    newAccountPresenter.handleProfileEditClick();

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the user should navigate to his edit profile view
    verify(navigator).navigateToEditProfileView();
  }

  @Test public void handleProfileDisplayableClickTest() {
    //Given an initialized MyAccountPresenter
    //When a user clicks the profile view
    when(view.userClick()).thenReturn(Observable.just(null));
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getId()).thenReturn("id");
    when(account.getStore()).thenReturn(accountManagerStore);
    when(accountManagerStore.getTheme()).thenReturn("theme");

    newAccountPresenter.handleProfileDisplayableClick();

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the user should navigate to his profile view
    verify(navigator).navigateToUserView("id", "theme");
  }

  @Test public void handleSettingsClickedTest() {
    //Given an initialized MyAccountPresenter
    //When a user clicks the settings button
    when(view.settingsClicked()).thenReturn(Observable.just(null));

    newAccountPresenter.handleSettingsClicked();

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the user should navigate to the settings view
    verify(navigator).navigateToSettings();
  }

  @Test public void handleNotificationHistoryClickedTest() {
    //Given an initialized MyAccountPresenter
    //When a user clicks the notifications button
    when(view.notificationsClicked()).thenReturn(Observable.just(null));

    newAccountPresenter.handleNotificationHistoryClicked();

    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the user should navigate to the notification center view
    verify(navigator).navigateToNotificationHistory();
  }
}
