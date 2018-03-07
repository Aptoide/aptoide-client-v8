package cm.aptoide.pt.navigation;

import cm.aptoide.pt.home.BottomNavPresenter;
import cm.aptoide.pt.home.BottomNavigationFragmentView;
import cm.aptoide.pt.presenter.View;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class BottomNavigationPresenterTest {

  private static final int MENU_ITEM_ID_TEST = 2;
  @Mock private BottomNavigationFragmentView view;

  private BottomNavPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private PublishSubject<Integer> navigationEvent;

  @Before public void setupBottomNavigationPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();
    navigationEvent = PublishSubject.create();

    presenter = new BottomNavPresenter(view);

    //simulate view lifecycle event
    when(view.getLifecycle()).thenReturn(lifecycleEvent);
    when(view.navigationEvent()).thenReturn(navigationEvent);
  }

  @Test public void onNavigationRequestedNavigateToView() {
    //Given an initialised BottomNavPresenter
    presenter.present();
    //And Bottom navigation is visible to the user
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //When the user clicks a menu item
    navigationEvent.onNext(MENU_ITEM_ID_TEST);
    //Then that menu item becomes focused
    //And the respective view is shown to the user
    verify(view).showFragment(MENU_ITEM_ID_TEST);
  }
}
