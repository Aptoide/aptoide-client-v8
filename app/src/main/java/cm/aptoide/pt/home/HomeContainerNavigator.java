package cm.aptoide.pt.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.view.MoreBundleFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;

public class HomeContainerNavigator {

  private FragmentNavigator fragmentNavigator;
  private String homeTag;
  private String gamesTag;
  private String appsTag;

  public HomeContainerNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void loadMainHomeContent() {
    Fragment fragment = fragmentNavigator.getFragment(homeTag);
    if (fragment != null) {
      fragmentNavigator.navigateToWithoutBackSave(fragment, true);
    } else {
      homeTag = fragmentNavigator.navigateTo(new HomeFragment(), true);
    }
  }

  public void loadGamesHomeContent() {
    Fragment fragment = new MoreBundleFragment();
    Bundle args = new Bundle();
    args.putString(StoreTabGridRecyclerFragment.BundleCons.TITLE, fragmentNavigator.getFragment()
        .getString(R.string.home_chip_games));
    args.putString(StoreTabGridRecyclerFragment.BundleCons.ACTION,
        "https://ws75.aptoide.com/api/7/getStoreWidgets/store_id=15/context=games/widget=apps_list%3A0%262%3Adownloads7d");
    args.putBoolean(StoreTabGridRecyclerFragment.BundleCons.TOOLBAR, false);
    fragment.setArguments(args);

    Fragment gamesFragment = fragmentNavigator.getFragment(gamesTag);
    if (gamesFragment != null) {
      fragmentNavigator.navigateToWithoutBackSave(gamesFragment, true);
    } else {
      gamesTag = fragmentNavigator.navigateTo(fragment, true);
    }
  }

  public void loadAppsHomeContent() {
    Fragment fragment = new MoreBundleFragment();
    Bundle args = new Bundle();
    args.putString(StoreTabGridRecyclerFragment.BundleCons.TITLE, fragmentNavigator.getFragment()
        .getString(R.string.home_chip_apps));
    args.putString(StoreTabGridRecyclerFragment.BundleCons.ACTION,
        "https://ws75.aptoide.com/api/7/getStoreWidgets/store_id=15/context=apps/widget=apps_list%3A0%262%3Apdownloads7d");
    args.putBoolean(StoreTabGridRecyclerFragment.BundleCons.TOOLBAR, false);
    fragment.setArguments(args);

    Fragment appsFragment = fragmentNavigator.getFragment(appsTag);
    if (appsFragment != null) {
      fragmentNavigator.navigateToWithoutBackSave(appsFragment, true);
    } else {
      appsTag = fragmentNavigator.navigateTo(fragment, true);
    }
  }
}
