package cm.aptoide.pt.account;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.Store;
import cm.aptoide.pt.account.view.MyAccountFragment;
import cm.aptoide.pt.account.view.MyAccountNavigator;
import cm.aptoide.pt.account.view.MyAccountPresenter;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.StoreUserAbstraction;
import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.notification.NotificationCenter;
import cm.aptoide.pt.presenter.View;
import java.util.ArrayList;
import java.util.List;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by D01 on 27/02/18.
 */

public class MyAccountPresenterTest {

  private final int NUMBER_OF_NOTIFICATIONS = 3;
  @Mock private MyAccountFragment view;

  @Mock private AptoideAccountManager accountManager;

  @Mock private CrashReport crashReport;

  @Mock private MyAccountNavigator myAccountNavigator;

  @Mock private NotificationCenter notificationCenter;

  @Mock private SharedPreferences sharedPreferences;

  @Mock private NotificationAnalytics analytics;

  @Mock private NavigationTracker navigationTracker;

  @Mock private Account account;

  @Mock private SharedPreferences.Editor editor;

  @Mock private GetStore getStore;

  @Mock private StoreUserAbstraction.Nodes nodes;

  @Mock private GetStoreMeta meta;

  @Mock private cm.aptoide.pt.dataprovider.model.v7.store.Store storeModel;

  @Mock private AptoideNotification aptoideNotification;

  @Mock private Store store;

  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private MyAccountPresenter myAccountPresenter;

  @Before public void setupMyAccountPresenter() {

    MockitoAnnotations.initMocks(this);
    lifecycleEvent = PublishSubject.create();
    myAccountPresenter =
        new MyAccountPresenter(view, accountManager, crashReport, myAccountNavigator,
            notificationCenter, sharedPreferences, navigationTracker, analytics,
            Schedulers.immediate());
    //simulate view lifecycle event
    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void showAndPopulateAccountViewsTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account,
    //When an account is requested
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the account information is shown in the UI
    verify(view).showAccount(account);
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

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    //Then address book related shared preferences should be set to false (reset)
    verify(sharedPreferences).edit();
    verify(editor, times(3)).putBoolean(anyString(), eq(false));
    verify(editor).apply();
    //Then navigate to the HomeView
    verify(myAccountNavigator).navigateToHome();
  }

  @Test public void handleEditStoreBackNavigationTest() {
    //Given an initialized MyAccountPresenter
    //And an user account
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    //When the user navigates back from the edit store screen
    when(myAccountNavigator.editStoreResult(EDIT_STORE_REQUEST_CODE)).thenReturn(
        Observable.just(null));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    InOrder order = Mockito.inOrder(myAccountNavigator, view);
    order.verify(myAccountNavigator)
        .editStoreResult(EDIT_STORE_REQUEST_CODE);

    //Then the account information is shown in the UI
    order.verify(view)
        .showAccount(account);
  }

  @Test public void handleMoreNotificationsClickTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account
    //When the user clicks the "More" Button
    PublishSubject<Void> clickEvent = PublishSubject.create();
    when(view.moreNotificationsClick()).thenReturn(clickEvent);

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    //Then the user navigates to the expanded notifications screen
    verify(myAccountNavigator).navigateToInboxView();
  }

  @Test public void handleEditStoreClickTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account with a store created
    //When the user clicks the "Edit" Button from the store
    PublishSubject<Void> clickEvent = PublishSubject.create();

    when(view.editStoreClick()).thenReturn(clickEvent);
    when(view.getStore()).thenReturn(Observable.just(getStore));
    when(getStore.getNodes()).thenReturn(nodes);
    when(nodes.getMeta()).thenReturn(meta);
    when(meta.getData()).thenReturn(storeModel);

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    //Then the user should navigate to the edit store screen
    verify(myAccountNavigator).navigateToEditStoreView(storeModel, EDIT_STORE_REQUEST_CODE);
  }

  @Test public void handleHeaderVisibilityWithNotificationsTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account with Notifications
    when(notificationCenter.haveNotifications()).thenReturn(Observable.just(true));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the notifications header section should be shown in the UI
    verify(view).showHeader();
  }

  @Test public void handleHeaderVisibilityWithNoNotificationsTest() {
    //Given an initialized MyAccountPresenter
    //And user Account without Notifications
    when(notificationCenter.haveNotifications()).thenReturn(Observable.just(false));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then the notifications header section should NOT be shown in the UI
    verify(view, never()).showHeader();
    verify(view).hideHeader();
  }

  @Test public void handleGetNotificationsTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account with notifications
    when(notificationCenter.haveNotifications()).thenReturn(Observable.just(true));

    List<AptoideNotification> aptoideNotificationList = new ArrayList<>();
    aptoideNotificationList.add(aptoideNotification);
    when(notificationCenter.getInboxNotifications(NUMBER_OF_NOTIFICATIONS)).thenReturn(
        Observable.just(aptoideNotificationList));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //It should show the notifications
    verify(view).showNotifications(aptoideNotificationList);
  }

  @Test public void handleGetNoNotificationsTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account without notifications
    when(notificationCenter.haveNotifications()).thenReturn(Observable.just(false));

    List<AptoideNotification> aptoideNotificationList = new ArrayList<>();
    aptoideNotificationList.add(aptoideNotification);
    when(notificationCenter.getInboxNotifications(NUMBER_OF_NOTIFICATIONS)).thenReturn(
        Observable.just(aptoideNotificationList));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //It should NOT show the notifications
    verify(view, never()).showNotifications(aptoideNotificationList);
  }

  @Test public void handleNotificationClickTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account with Notifications
    //When the user clicks one of them
    when(view.notificationSelection()).thenReturn(Observable.just(aptoideNotification));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then should navigate to the Notification
    verify(myAccountNavigator).navigateToNotification(aptoideNotification);
    //Then send the analytics
    verify(analytics).sendNotificationTouchEvent(anyString());
    //Then register the screen
    verify(navigationTracker).registerScreen(any());
  }

  @Test public void handleUserEditClickTest() {
    PublishSubject<Void> clickEvent = PublishSubject.create();
    //Given an initialized MyAccountPresenter
    //And an user Account
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    //When the user clicks the "Edit" Button from the user
    when(view.editUserProfileClick()).thenReturn(clickEvent);
    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    //Then it should navigate to the edit user profile screen
    verify(myAccountNavigator).navigateToEditProfileView();
  }

  @Test public void handleUserLayoutClickTest() {
    PublishSubject<Void> clickEvent = PublishSubject.create();
    //Given an initialized MyAccountPresenter
    //And an user Account
    when(view.userClick()).thenReturn(clickEvent);
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    //When the user clicks the user info
    when(account.getStore()).thenReturn(store);

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    //Then it should navigate to the user or store screen
    verify(myAccountNavigator).navigateToUserView(anyString(), anyString());
  }

  @Test public void handleStoreLayoutClickTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account with Store
    //When the user clicks the store info
    PublishSubject<Void> clickEvent = PublishSubject.create();
    when(view.storeClick()).thenReturn(clickEvent);
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getStore()).thenReturn(store);

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    //Then it should navigate to the store screen
    verify(myAccountNavigator).navigateToStoreView(anyString(), anyString());
  }

  @Test public void checkIfStoreIsInvalidAndRefreshTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account with Store
    //When the store is not in AccountManager (getId == 0),
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getStore()).thenReturn(store);
    when(storeModel.getId()).thenReturn((long) 0);
    when(view.getStore()).thenReturn(Observable.just(getStore));
    when(getStore.getNodes()).thenReturn(nodes);
    when(nodes.getMeta()).thenReturn(meta);
    when(meta.getData()).thenReturn(storeModel);

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    //Then it should update the view
    verify(view).refreshUI(storeModel);
    //Then it should update the accountManager
    verify(accountManager).updateAccount();
  }

  @Test public void markNotificationsReadTest() {
    //Given an initialized MyAccountPresenter
    //And an user Account with unread Notifications
    //When the user enters the MyAccountView

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    // Then it should set all Notifications as read
    verify(notificationCenter).setAllNotificationsRead();
  }
}
