package cm.aptoide.pt.home;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.home.apps.AppsFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigatorActivity;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.analytics.SearchSource;
import cm.aptoide.pt.search.view.SearchResultFragment;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import cm.aptoide.pt.view.NotBottomNavigationView;
import cm.aptoide.pt.view.settings.NewAccountFragment;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 12/03/18.
 */

public abstract class BottomNavigationActivity extends TabNavigatorActivity
    implements AptoideBottomNavigator {

  protected static final int LAYOUT = R.layout.frame_layout;
  private final static String EVENT_ACTION =
      "https://ws75.aptoide.com/api/7/getStoreWidgets/store_id=15/context=stores";
  protected BottomNavigationView bottomNavigationView;
  @Inject BottomNavigationAnalytics bottomNavigationAnalytics;
  @Inject SearchAnalytics searchAnalytics;
  private PublishSubject<Integer> navigationSubject;
  private Animation animationup;
  private Animation animationdown;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivityComponent().inject(this);

    setContentView(LAYOUT);
    navigationSubject = PublishSubject.create();
    bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
    BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
      navigationSubject.onNext(item.getItemId());
      return true;
    });
    animationup = AnimationUtils.loadAnimation(this, R.anim.slide_up);
    animationdown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
    toogleBottomNavigation(); //Here because of the SettingsFragment that doesn't extend the BaseFragment
  }

  @Override protected void onDestroy() {
    navigationSubject = null;
    bottomNavigationView = null;
    animationdown = null;
    animationup = null;
    super.onDestroy();
  }

  @Override public Observable<Integer> navigationEvent() {
    return navigationSubject;
  }

  @Override public void showFragment(Integer menuItemId) {
    Fragment currentFragment = getFragmentNavigator().getFragment();
    Fragment selectedFragment = null;
    String defaultStoreName = ((AptoideApplication) getApplicationContext()).getDefaultStoreName();
    //Each view from the  BottomNavigation has to implement a scrollToTop method when clicked again (see BottomHomeFragment)
    //Each fragment should implement it's own action bar
    switch (menuItemId) {
      case R.id.action_home:
        bottomNavigationAnalytics.sendNavigateToHomeClickEvent();
        selectedFragment = new BottomHomeFragment();
        break;
      case R.id.action_search:
        bottomNavigationAnalytics.sendNavigateToSearchClickEvent();
        searchAnalytics.searchStart(SearchSource.BOTTOM_NAVIGATION, true);
        selectedFragment = SearchResultFragment.newInstance(defaultStoreName, true);
        break;
      case R.id.action_stores:
        bottomNavigationAnalytics.sendNavigateToStoresClickEvent();
        selectedFragment =
            MyStoresFragment.newInstance(getStoreEvent(), "default", "stores", StoreContext.home);
        break;
      case R.id.action_apps:
        bottomNavigationAnalytics.sendNavigateToAppsClickEvent();
        selectedFragment = new AppsFragment();
        break;
    }
    if (selectedFragment != null) {
      if (currentFragment == null) {
        FragmentNavigator fragmentNavigator = getFragmentNavigator();
        fragmentNavigator.navigateTo(selectedFragment, true);
      } else if (selectedFragment.getClass() != currentFragment.getClass()) {
        FragmentNavigator fragmentNavigator = getFragmentNavigator();
        fragmentNavigator.navigateTo(selectedFragment, true);
      }
    }
  }

  @Override public void toogleBottomNavigation() {
    Fragment fragment = getFragmentNavigator().getFragment();
    if (fragment instanceof NotBottomNavigationView) {
      if (bottomNavigationView.getVisibility() != View.GONE) {
        bottomNavigationView.startAnimation(animationdown);
        bottomNavigationView.setVisibility(View.GONE);
      }
    } else {
      if (bottomNavigationView.getVisibility() != View.VISIBLE) {
        bottomNavigationView.startAnimation(animationup);
        bottomNavigationView.setVisibility(View.VISIBLE);
      }
    }
  }

  @Override public void requestFocus(BottomNavigationItem bottomNavigationItem) {
    BottomNavigationMapper bottomNavigationMapper = new BottomNavigationMapper();
    int bottomNavigationPosition =
        bottomNavigationMapper.mapToBottomNavigationPosition(bottomNavigationItem);
    bottomNavigationView.getMenu()
        .getItem(bottomNavigationPosition)
        .setChecked(true);
  }

  private Event getStoreEvent() {
    Event event = new Event();
    event.setAction(EVENT_ACTION);
    event.setData(null);
    event.setName(Event.Name.myStores);
    event.setType(Event.Type.CLIENT);
    return event;
  }
}
