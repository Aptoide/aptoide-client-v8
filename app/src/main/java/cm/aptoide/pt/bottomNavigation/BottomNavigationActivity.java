package cm.aptoide.pt.bottomNavigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.LoginBottomSheetActivity;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.home.AptoideBottomNavigator;
import cm.aptoide.pt.home.more.eskills.EskillsInfoFragment;
import cm.aptoide.pt.home.more.eskills.ListAppsEskillsFragment;
import cm.aptoide.pt.view.NotBottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 12/03/18.
 */

public abstract class BottomNavigationActivity extends LoginBottomSheetActivity
    implements AptoideBottomNavigator {

  protected static final int LAYOUT = R.layout.frame_layout;
  private final String ITEMS_LIST_KEY = "BN_ITEMS";
  protected BottomNavigationView bottomNavigationView;
  @Inject BottomNavigationMapper bottomNavigationMapper;
  @Inject BottomNavigationNavigator bottomNavigationNavigator;
  private PublishSubject<Integer> navigationSubject;
  private Animation animationup;
  private Animation animationdown;

  private Boolean isThemeEnforced;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(LAYOUT);
    navigationSubject = PublishSubject.create();
    bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
    getActivityComponent().inject(this);
    if (savedInstanceState != null) {
      bottomNavigationNavigator.setBottomNavigationItems(
          savedInstanceState.getIntegerArrayList(ITEMS_LIST_KEY));
    }
    bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
      navigationSubject.onNext(item.getItemId());
      return true;
    });
    animationup = AnimationUtils.loadAnimation(this, R.anim.slide_up);
    animationdown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
    isThemeEnforced = false;
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

  @SuppressLint("ResourceType") @Override public void toggleBottomNavigation() {
    Fragment fragment = getFragmentNavigator().getFragment();
    if (fragment instanceof NotBottomNavigationView) {
      if (bottomNavigationView.getVisibility() != View.GONE) {
        bottomNavigationView.startAnimation(animationdown);
        bottomNavigationView.setVisibility(View.GONE);
      }
    } else if (fragment instanceof EskillsInfoFragment
        || ((fragment instanceof AppViewFragment && ((AppViewFragment) fragment).isEskills) ||
        (fragment instanceof ListAppsEskillsFragment)
    )
        && !themeManager.isThemeDark()) {
      forceDarkTheme();
    } else {
      if (isThemeEnforced && !themeManager.isThemeDark()) {
        setDefaultTheme();
      }
      if (bottomNavigationView.getVisibility() != View.VISIBLE) {
        bottomNavigationView.startAnimation(animationup);
        bottomNavigationView.setVisibility(View.VISIBLE);
      }

      getActivityComponent().inject(this);
      bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
        navigationSubject.onNext(item.getItemId());
        return true;
      });
    }
  }

  private void forceDarkTheme() {
    if(isThemeEnforced) return;
    bottomNavigationView.animate()
        .alpha(0)
        .setDuration(200)
        .withEndAction(() -> {
          bottomNavigationView.setBackgroundColor(getResources().getColor(R.color.grey_900));
          bottomNavigationView.setItemIconTintList(ContextCompat.getColorStateList(this, R.drawable.default_nav_item_color_state_dark));
          bottomNavigationView.setItemTextColor(
              ContextCompat.getColorStateList(this, R.drawable.default_nav_item_color_state_dark));
          isThemeEnforced =true;
          bottomNavigationView.animate()
              .alpha(1.0f)
              .setDuration(200);
        });
  }

  private void setDefaultTheme() {
    bottomNavigationView.animate()
        .alpha(0)
        .setDuration(200)
        .withEndAction(() -> {
          bottomNavigationView.setItemIconTintList(
              ContextCompat.getColorStateList(this, R.drawable.default_nav_item_color_state));
          bottomNavigationView.setItemTextColor(
              ContextCompat.getColorStateList(this, R.drawable.default_nav_item_color_state));
          bottomNavigationView.setBackgroundColor(0);
          isThemeEnforced = false;
          bottomNavigationView.animate()
              .alpha(1.0f)
              .setDuration(200);
        });
  }

  @Override public void hideBottomNavigation() {
    if (bottomNavigationView.getVisibility() != View.GONE) {
      bottomNavigationView.setVisibility(View.GONE);
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
