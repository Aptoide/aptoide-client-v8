package cm.aptoide.pt.account;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.Store;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreMeta;
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

import static cm.aptoide.pt.account.view.MyAccountPresenter.EDIT_STORE_REQUEST_CODE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by franciscocalado on 02/04/18.
 */

public class NewAccountPresenterTest {

  @Mock private NewAccountFragment view;

  @Mock private AptoideAccountManager accountManager;

  @Mock private CrashReport crashReport;

  @Mock private NewAccountNavigator newAccountNavigator;

  @Mock private SharedPreferences sharedPreferences;

  @Mock private AccountAnalytics analytics;

  @Mock private Account account;

  @Mock private SharedPreferences.Editor editor;

  @Mock private GetStore getStore;

  @Mock private StoreUserAbstraction.Nodes nodes;

  @Mock private GetStoreMeta meta;

  @Mock private cm.aptoide.pt.dataprovider.model.v7.store.Store storeModel;

  @Mock private Store store;

  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private NewAccountPresenter newAccountPresenter;

  @Before public void setupNewAccountPresenter() {

    MockitoAnnotations.initMocks(this);
    lifecycleEvent = PublishSubject.create();
    newAccountPresenter =
        new NewAccountPresenter(view, accountManager, crashReport, sharedPreferences,
            Schedulers.immediate(), newAccountNavigator, analytics);
    //simulate view lifecycle event
    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void showAndPopulateAccountViewsTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account,
    //When an account is requested
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));

    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the account information is shown in the UI
    verify(view).showAccount(account);
  }

  @Test public void checkIfStoreIsInvalidAndRefreshTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account with Store
    //When the store is not in AccountManager (getId == 0),
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getStore()).thenReturn(store);
    when(storeModel.getId()).thenReturn((long) 0);
    when(account.hasStore()).thenReturn(true);
    when(view.getStore()).thenReturn(Observable.just(getStore));
    when(getStore.getNodes()).thenReturn(nodes);
    when(nodes.getMeta()).thenReturn(meta);
    when(meta.getData()).thenReturn(storeModel);

    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should update the view
    verify(view).refreshUI(storeModel);
    //Then it should update the accountManager
    verify(accountManager).updateAccount();
  }

  @Test public void handleLoginClickTest() {
    PublishSubject<Void> clickEvent = PublishSubject.create();
    //Given an initialized MyAccountPresenter
    //And an user Account

    //When the user clicks the login Button
    when(view.loginClick()).thenReturn(clickEvent);
    //Then the user should navigate to the login screen
    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);

    verify(newAccountNavigator).navigateToLoginView(AccountAnalytics.AccountOrigins.MY_ACCOUNT);
  }

  @Test public void handleSignOutButtonClickTest() {
    PublishSubject<Void> clickEvent = PublishSubject.create();
    //Given an initialized MyAccountPresenter
    //And an user Account

    //When the user clicks the SignOut Button
    when(view.signOutClick()).thenReturn(clickEvent);
    //Then the user should be logged out
    when(accountManager.logout()).thenReturn(Completable.complete());
    when(sharedPreferences.edit()).thenReturn(editor);
    when(editor.putBoolean(anyString(), eq(false))).thenReturn(editor);

    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    //Then address book related shared preferences should be set to false (reset)
    verify(sharedPreferences).edit();
    verify(editor, times(3)).putBoolean(anyString(), eq(false));
    verify(editor).apply();
    //Then navigate to the HomeView
    verify(view).showLoginAccountDisplayable();
  }

  @Test public void handleCreateStoreClickTest() {
    PublishSubject<Void> clickEvent = PublishSubject.create();
    //Given an initialized MyAccountPresenter
    //And an user Account

    //When the user clicks the create store button
    when(view.createStoreClick()).thenReturn(clickEvent);
    //Then the user should navigate to the create store screen
    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);

    verify(newAccountNavigator).navigateToCreateStore();
  }

  @Test public void handleFindFriendsClickTest() {
    PublishSubject<Void> clickEvent = PublishSubject.create();
    //Given an initialized MyAccountPresenter
    //And an user Account

    //When the user clicks the find friends button
    when(view.findFriendsClick()).thenReturn(clickEvent);
    //Then the user should navigate to the find friends screen
    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);

    verify(newAccountNavigator).navigateToFindFriends();
  }

  @Test public void handleStoreEditClickTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account with a store created
    //When the user clicks the "Edit" Button from the store
    PublishSubject<Void> clickEvent = PublishSubject.create();

    when(view.editStoreClick()).thenReturn(clickEvent);
    when(view.getStore()).thenReturn(Observable.just(getStore));
    when(getStore.getNodes()).thenReturn(nodes);
    when(nodes.getMeta()).thenReturn(meta);
    when(meta.getData()).thenReturn(storeModel);

    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    //Then the user should navigate to the edit store screen
    verify(newAccountNavigator).navigateToEditStoreView(storeModel, EDIT_STORE_REQUEST_CODE);
  }

  @Test public void handleEditStoreBackNavigationTest() {
    //Given an initialized MyAccountPresenter
    //And an user account
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    //When the user navigates back from the edit store screen
    when(newAccountNavigator.editStoreResult(EDIT_STORE_REQUEST_CODE)).thenReturn(
        Observable.just(null));

    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    InOrder order = Mockito.inOrder(newAccountNavigator, view);
    order.verify(newAccountNavigator)
        .editStoreResult(EDIT_STORE_REQUEST_CODE);

    //Then the account information is shown in the UI
    order.verify(view)
        .showAccount(account);
  }

  @Test public void handleStoreDisplayableClickTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account with Store
    //When the user clicks the store info
    PublishSubject<Void> clickEvent = PublishSubject.create();
    when(view.storeClick()).thenReturn(clickEvent);
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getStore()).thenReturn(store);

    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    //Then it should navigate to the store screen
    verify(newAccountNavigator).navigateToStoreView(anyString(), anyString());
  }

  @Test public void handleProfileEditClickTest() {
    PublishSubject<Void> clickEvent = PublishSubject.create();
    //Given an initialized MyAccountPresenter
    //And an user Account
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    //When the user clicks the "Edit" Button from the user
    when(view.editUserProfileClick()).thenReturn(clickEvent);
    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    //Then it should navigate to the edit user profile screen
    verify(newAccountNavigator).navigateToEditProfileView();
  }

  @Test public void handleProfileDisplayableClickTest() {
    PublishSubject<Void> clickEvent = PublishSubject.create();
    //Given an initialized MyAccountPresenter
    //And an user Account
    when(view.userClick()).thenReturn(clickEvent);
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    //When the user clicks the user info
    when(account.getStore()).thenReturn(store);

    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    //Then it should navigate to the user or store screen
    verify(newAccountNavigator).navigateToUserView(anyString(), anyString());
  }

  @Test public void handleSettingsClickedTest() {
    PublishSubject<Void> clickEvent = PublishSubject.create();
    //Given an initialized MyAccountPresenter
    //And an user Account

    //When the user clicks the login Button
    when(view.settingsClicked()).thenReturn(clickEvent);
    //Then the user should navigate to the login screen
    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);

    verify(newAccountNavigator).navigateToSettings();
  }

  @Test public void handleNotificationHistoryClickedTest() {
    PublishSubject<Void> clickEvent = PublishSubject.create();
    //Given an initialized MyAccountPresenter
    //And an user Account

    //When the user clicks the login Button
    when(view.notificationsClicked()).thenReturn(clickEvent);
    //Then the user should navigate to the login screen
    newAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);

    verify(newAccountNavigator).navigateToNotificationHistory();
  }
}
