package cm.aptoide.pt.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.view.MoreBundleFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;

public class HomeContainerNavigator {

  private FragmentNavigator childFragmentNavigator;
  private String homeTag;
  private String gamesTag;
  private String appsTag;

  public HomeContainerNavigator(FragmentNavigator childFragmentNavigator) {
    this.childFragmentNavigator = childFragmentNavigator;
  }

  public void loadMainHomeContent() {
    Fragment fragment = childFragmentNavigator.getFragment(homeTag);
    if (fragment != null) {
      childFragmentNavigator.navigateToWithoutBackSave(fragment, true);
    } else {
      homeTag = childFragmentNavigator.navigateTo(new HomeFragment(), true);
    }
  }

  public void loadGamesHomeContent() {
    Fragment fragment = new MoreBundleFragment();
    Bundle args = new Bundle();
    args.putString(StoreTabGridRecyclerFragment.BundleCons.TITLE,
        childFragmentNavigator.getFragment()
            .getString(R.string.home_chip_games));
    args.putString(StoreTabGridRecyclerFragment.BundleCons.ACTION,
        "https://ws75.aptoide.com/api/7/getStoreWidgets/store_id=15/context=games/widget=apps_list%3A0%262%3Adownloads7d");
    args.putBoolean(StoreTabGridRecyclerFragment.BundleCons.TOOLBAR, false);
    fragment.setArguments(args);

    Fragment gamesFragment = childFragmentNavigator.getFragment(gamesTag);
    if (gamesFragment != null) {
      childFragmentNavigator.navigateToWithoutBackSave(gamesFragment, true);
    } else {
      gamesTag = childFragmentNavigator.navigateTo(fragment, true);
    }
  }

  public void loadAppsHomeContent() {
    Fragment fragment = new MoreBundleFragment();
    Bundle args = new Bundle();
    args.putString(StoreTabGridRecyclerFragment.BundleCons.TITLE,
        childFragmentNavigator.getFragment()
            .getString(R.string.home_chip_apps));
    args.putString(StoreTabGridRecyclerFragment.BundleCons.ACTION,
        "https://ws75.aptoide.com/api/7/getStoreWidgets/store_id=15/context=apps/widget=apps_list%3A0%262%3Apdownloads7d");
    args.putBoolean(StoreTabGridRecyclerFragment.BundleCons.TOOLBAR, false);
    fragment.setArguments(args);

    Fragment appsFragment = childFragmentNavigator.getFragment(appsTag);
    if (appsFragment != null) {
      childFragmentNavigator.navigateToWithoutBackSave(appsFragment, true);
    } else {
      appsTag = childFragmentNavigator.navigateTo(fragment, true);
    }
  }
}
