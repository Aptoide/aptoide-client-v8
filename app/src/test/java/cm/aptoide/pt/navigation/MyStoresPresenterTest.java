package cm.aptoide.pt.navigation;

import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.R;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import cm.aptoide.pt.store.view.my.MyStoresNavigator;
import cm.aptoide.pt.store.view.my.MyStoresPresenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by D01 on 14/03/18.
 */

public class MyStoresPresenterTest {

  private static final Integer MENU_ITEM_ID_TEST = R.id.action_stores;
  @Mock private MyStoresFragment view;
  @Mock private AptoideAccountManager aptoideAccountManager;
  @Mock private MyStoresNavigator myStoresNavigator;
  @Mock private Account account;

  private MyStoresPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private PublishSubject<Integer> navigationEvent;
  private PublishSubject<Void> imageClickEvent;
  private PublishSubject<Account> accountStatusEvent;

  @Before public void setupMyStoresPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();
    navigationEvent = PublishSubject.create();
    imageClickEvent = PublishSubject.create();
    accountStatusEvent = PublishSubject.create();
    presenter = new MyStoresPresenter(view, Schedulers.immediate(), aptoideAccountManager,
        myStoresNavigator);

    when(view.getLifecycle()).thenReturn(lifecycleEvent);
    when(view.imageClick()).thenReturn(imageClickEvent);
    when(myStoresNavigator.bottomNavigationEvent()).thenReturn(navigationEvent);
    when(aptoideAccountManager.accountStatus()).thenReturn(accountStatusEvent);
  }

  @Test public void scrollToTopTest() {
    //Given an initialised MyStoresPresenter
    presenter.present();
    //And Bottom navigation is visible to the user
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When the user clicks a menu item
    navigationEvent.onNext(MENU_ITEM_ID_TEST);
    //Then it should scroll to the top
    verify(view).scrollToTop();
  }

  @Test public void loadUserImageUserLoggedInTest() {
    //When the user is logged in
    when(account.getAvatar()).thenReturn("A string");
    when(account.isLoggedIn()).thenReturn(true);
    //Given an initialised MyStoresPresenter
    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And AccountManager returns an account
    accountStatusEvent.onNext(account);
    //Then it should show the image
    verify(view).setUserImage("A string");
    verify(view).showAvatar();
  }

  @Test public void loadUserImageUserNotLoggedInTest() {
    //When the user is logged in
    when(account.isLoggedIn()).thenReturn(false);
    //Given an initialised MyStoresPresenter
    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And AccountManager returns an account
    accountStatusEvent.onNext(account);
    //Then it should show the image
    verify(view).showAvatar();
  }

  @Test public void handeUserImageClick() {
    //Given an initialised MyStoresPresenter
    presenter.present();
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When an user clicks the profile image
    imageClickEvent.onNext(null);
    //Then it should navigate to the Settings Fragment
    verify(myStoresNavigator).navigateToMyAccount();
  }
}
