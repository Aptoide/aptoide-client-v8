package cm.aptoide.pt.home;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.R;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigatorActivity;
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
    //Each view from the fragment should extend the BottomNavigationView interface so that it can scrollToTop when clicked again (see BottomHomeFragment)
    //Each fragment should implement it's own action bar
    switch (menuItemId) {
      case R.id.action_home:
        selectedFragment = new BottomHomeFragment();
        break;
      case R.id.action_search:
        break;
      case R.id.action_stores:
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
}
