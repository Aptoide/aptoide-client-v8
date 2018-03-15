package cm.aptoide.pt.navigation;

import cm.aptoide.pt.home.BottomNavigationActivity;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import cm.aptoide.pt.store.view.my.MyStoresPresenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by D01 on 14/03/18.
 */

public class MyStoresPresenterTest {

  private static final Integer MENU_ITEM_ID_TEST = 3;
  @Mock private MyStoresFragment view;
  @Mock private BottomNavigationActivity bottomNavigationActivity;
  private MyStoresPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycle;
  private PublishSubject<Integer> navigationEvent;

  @Before public void setupMyStoresPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycle = PublishSubject.create();
    navigationEvent = PublishSubject.create();
    presenter = new MyStoresPresenter(view, bottomNavigationActivity);

    when(view.getLifecycle()).thenReturn(lifecycle);
    when(bottomNavigationActivity.navigationEvent()).thenReturn(navigationEvent);
  }

  @Test public void scrollToTopTest() {
    //Given an initialised HomePresenter
    presenter.present();
    //And Bottom navigation is visible to the user
    lifecycle.onNext(View.LifecycleEvent.CREATE);
    //When the user clicks a menu item
    navigationEvent.onNext(MENU_ITEM_ID_TEST);
    //Then it should scroll to the top
    verify(view).scrollToTop();
  }
}
