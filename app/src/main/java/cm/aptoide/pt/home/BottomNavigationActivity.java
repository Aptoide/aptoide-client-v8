package cm.aptoide.pt.home;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import cm.aptoide.pt.R;
import cm.aptoide.pt.navigator.TabNavigatorActivity;
import cm.aptoide.pt.view.NotBottomNavigationView;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 12/03/18.
 */

public abstract class BottomNavigationActivity extends TabNavigatorActivity
    implements AptoideBottomNavigator {

  protected static final int LAYOUT = R.layout.frame_layout;
  private final String ITEMS_LIST_KEY = "BN_ITEMS";
  protected BottomNavigationView bottomNavigationView;
  @Inject BottomNavigationMapper bottomNavigationMapper;
  @Inject BottomNavigationNavigator bottomNavigationNavigator;
  private PublishSubject<Integer> navigationSubject;
  private Animation animationup;
  private Animation animationdown;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(LAYOUT);
    navigationSubject = PublishSubject.create();
    bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
    super.onCreate(savedInstanceState);
    getActivityComponent().inject(this);
    if (savedInstanceState != null) {
      bottomNavigationNavigator.setBottomNavigationItems(
          savedInstanceState.getIntegerArrayList(ITEMS_LIST_KEY));
    }
    BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
      navigationSubject.onNext(item.getItemId());
      return true;
    });
    animationup = AnimationUtils.loadAnimation(this, R.anim.slide_up);
    animationdown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
    toggleBottomNavigation(); //Here because of the SettingsFragment that doesn't extend the BaseFragment
  }

  @Override protected void onDestroy() {
    bottomNavigationMapper = null;
    bottomNavigationNavigator = null;
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
    int bottomNavigationPosition = bottomNavigationMapper.mapToBottomNavigationPosition(menuItemId);
    bottomNavigationNavigator.navigateToBottomNavigationItem(bottomNavigationPosition);
  }

  @Override public void toggleBottomNavigation() {
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
    int bottomNavigationPosition =
        bottomNavigationMapper.mapToBottomNavigationPosition(bottomNavigationItem);
    bottomNavigationView.getMenu()
        .getItem(bottomNavigationPosition)
        .setChecked(true);
  }

  @Override public void onBackPressed() {
    if (getFragmentNavigator().peekLast() == null && bottomNavigationNavigator.canNavigateBack()) {
      bottomNavigationNavigator.navigateBack();
    } else {
      super.onBackPressed();
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putIntegerArrayList(ITEMS_LIST_KEY,
        bottomNavigationNavigator.getBottomNavigationItems());
  }
}
