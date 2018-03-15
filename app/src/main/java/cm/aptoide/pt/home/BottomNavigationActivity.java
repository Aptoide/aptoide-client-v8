package cm.aptoide.pt.home;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigatorActivity;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 12/03/18.
 */

public abstract class BottomNavigationActivity extends TabNavigatorActivity
    implements AptoideBottomNavigator {

  protected static final int LAYOUT = R.layout.frame_layout;
  protected BottomNavigationView bottomNavigationView;
  private PublishSubject<Integer> navigationSubject;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(LAYOUT);
    navigationSubject = PublishSubject.create();
    bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
    BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
      navigationSubject.onNext(item.getItemId());
      return true;
    });
  }

  @Override public Observable<Integer> navigationEvent() {
    return navigationSubject;
  }

  @Override public void showFragment(Integer menuItemId) {
    Fragment currentFragment = getFragmentNavigator().getFragment();
    Fragment selectedFragment = null;
    //Each view from the fragment should extend the BottomNavigationFragment interface so that it can scrollToTop when clicked again (see BottomHomeFragment)
    //Each fragment should implement it's own action bar
    switch (menuItemId) {
      case R.id.action_home:
        selectedFragment = new BottomHomeFragment();
        break;
      case R.id.action_search:
        break;
      case R.id.action_stores:
        selectedFragment =
            MyStoresFragment.newInstance(getStoreEvent(), null, "stores", StoreContext.home);
        break;
      case R.id.action_apps:
        selectedFragment = new BottomHomeFragment();
        break;
    }
    if (selectedFragment != null) {
      if (selectedFragment.getClass() != currentFragment.getClass()) {
        FragmentNavigator fragmentChildNavigator = getFragmentNavigator();
        fragmentChildNavigator.navigateTo(selectedFragment, true);
      }
    }
  }

  private Event getStoreEvent() {
    Event event = new Event();
    event.setAction("https://ws75.aptoide.com/api/7/getStoreWidgets/store_id=15/context=stores");
    event.setData(null);
    event.setName(Event.Name.myStores);
    event.setType(Event.Type.CLIENT);
    return event;
  }
}
