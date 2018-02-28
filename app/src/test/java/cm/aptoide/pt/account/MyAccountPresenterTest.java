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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Completable;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

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
    //Given an initialized MyAccountPresenter, and an user Account,
    //Populate the view with the information of the Account(Nickname or email and with store if the user has one)
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    verify(view).showAccount(account);
  }

  @Test public void handleSignOutButtonClickTest() {
    //Given an initialized MyAccountPresenter, and an user Account,
    //When the user clicks the SignOut Button, the user should be logged out (changing the manager preferences) and navigate to the HomeView
    PublishSubject<Void> clickEvent = PublishSubject.create();
    when(view.signOutClick()).thenReturn(clickEvent);
    when(accountManager.logout()).thenReturn(Completable.complete());
    when(sharedPreferences.edit()).thenReturn(editor);
    when(editor.putBoolean(anyString(), eq(false))).thenReturn(editor);

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    verify(sharedPreferences).edit();
    verify(editor, times(3)).putBoolean(anyString(), eq(false));
    verify(editor).apply();
    verify(myAccountNavigator).navigateToHome();
  }

  @Test public void handleEditStoreBackNavigationTest() {
    //Given an initialized MyAccountPresenter,
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).showAccount(account);
  }

  @Test public void handleMoreNotificationsClickTest() {
    //Given an initialized MyAccountPresenter, an user Account,
    //When the user clicks the "More" Button, the user should navigate to InboxView
    PublishSubject<Void> clickEvent = PublishSubject.create();
    when(view.moreNotificationsClick()).thenReturn(clickEvent);

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    verify(myAccountNavigator).navigateToInboxView();
  }

  @Test public void handleEditStoreClickTest() {
    //Given an initialized MyAccountPresenter, and an user Account with a store created,
    //When the user clicks the "Edit" Button from the store, the user should navigate to the ManageStoreView
    PublishSubject<Void> clickEvent = PublishSubject.create();

    when(view.editStoreClick()).thenReturn(clickEvent);
    when(view.getStore()).thenReturn(Observable.just(getStore));
    when(getStore.getNodes()).thenReturn(nodes);
    when(nodes.getMeta()).thenReturn(meta);
    when(meta.getData()).thenReturn(storeModel);

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    verify(myAccountNavigator).navigateToEditStoreView(storeModel,
        MyAccountPresenter.EDIT_STORE_REQUEST_CODE);
  }

  @Test public void handleHeaderVisibilityWithNotificationsTest() {
    //Given an initialized MyAccountPresenter, an user Account, and with Notifications,
    //The view should display the Header with the "More" Button
    when(notificationCenter.haveNotifications()).thenReturn(Observable.just(true));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).showHeader();
  }

  @Test public void handleHeaderVisibilityWithNoNotificationsTest() {
    //Given an initialized MyAccountPresenter, an user Account, and with no Notifications,
    //The view should hide the Header with the "More" Button
    when(notificationCenter.haveNotifications()).thenReturn(Observable.just(false));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view, never()).showHeader();
    verify(view).hideHeader();
  }

  @Test public void handleGetNotificationsTest() {
    //Given an initialized MyAccountPresenter and an user Account, with or without Notifications,
    //It should update the adapter with the Notification List
    List<AptoideNotification> aptoideNotificationList = new ArrayList<>();
    aptoideNotificationList.add(aptoideNotification);
    when(notificationCenter.getInboxNotifications(NUMBER_OF_NOTIFICATIONS)).thenReturn(
        Observable.just(aptoideNotificationList));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).updateAdapter(aptoideNotificationList);
  }

  @Test public void handleNotificationClickTest() {
    //Given an initialized MyAccountPresenter, an user Account, with Notification,
    //When the user clicks one of them, it should navigate to the Notification, send the analytics and register the screen
    when(view.notificationSelection()).thenReturn(Observable.just(aptoideNotification));

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(myAccountNavigator).navigateToNotification(aptoideNotification);
    verify(analytics).sendNotificationTouchEvent(anyString());
    verify(navigationTracker).registerScreen(any());
  }

  @Test public void handleUserEditClickTest() {
    //Given an initialized MyAccountPresenter, and an user Account,
    //When the user clicks the "Edit" Button from the user, it should navigate to the ManageUserView
    PublishSubject<Void> clickEvent = PublishSubject.create();
    when(view.editUserProfileClick()).thenReturn(clickEvent);
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    verify(myAccountNavigator).navigateToEditProfileView();
  }

  @Test public void handleUserLayoutClickTest() {
    //Given an initialized MyAccountPresenter, and an user Account,
    //When the user clicks the Layout from the user, it should navigate to the UserView of the StoreFragment
    PublishSubject<Void> clickEvent = PublishSubject.create();
    when(view.userLayoutClick()).thenReturn(clickEvent);
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getStore()).thenReturn(store);

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    verify(myAccountNavigator).navigateToUserView(anyString(), anyString());
  }

  @Test public void handleStoreLayoutClickTest() {
    //Given an initialized MyAccountPresenter, and an user Account with Store,
    //When the user clicks the Layout from the store, it should navigate to the StoreView of the StoreFragment
    PublishSubject<Void> clickEvent = PublishSubject.create();
    when(view.storeLayoutClick()).thenReturn(clickEvent);
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getStore()).thenReturn(store);

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    clickEvent.onNext(null);
    verify(myAccountNavigator).navigateToStoreView(anyString(), anyString());
  }

  @Test public void checkIfStoreIsInvalidAndRefreshTest() {
    //Given an initialized MyAccountPresenter, and an user Account with Store,
    //If the store is not in AccountManager (getId == 0),
    //then it should update the view and accountManager
    when(accountManager.accountStatus()).thenReturn(Observable.just(account));
    when(account.getStore()).thenReturn(store);
    when(storeModel.getId()).thenReturn((long) 0);
    when(view.getStore()).thenReturn(Observable.just(getStore));
    when(getStore.getNodes()).thenReturn(nodes);
    when(nodes.getMeta()).thenReturn(meta);
    when(meta.getData()).thenReturn(storeModel);

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(view).refreshUI(storeModel);
    verify(accountManager).updateAccount();
  }

  @Test public void markNotificationsReadTest() {
    //Given an initialized MyAccountPresenter, an user Account with unread Notifications,
    //When the user enters the MyAccountView, it should set all Notifications as read

    myAccountPresenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);

    verify(notificationCenter).setAllNotificationsRead();
  }
}
