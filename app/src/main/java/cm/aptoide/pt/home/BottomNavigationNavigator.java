package cm.aptoide.pt.home;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.home.apps.AppsFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.analytics.SearchSource;
import cm.aptoide.pt.search.view.SearchResultFragment;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by D01 on 02/04/18.
 */

public class BottomNavigationNavigator {

  private final static String EVENT_ACTION =
      "https://ws75.aptoide.com/api/7/getStoreWidgets/store_id=15/context=stores";
  private final FragmentNavigator fragmentNavigator;
  private final String defaultStoreName;
  private final BottomNavigationAnalytics bottomNavigationAnalytics;
  private final SearchAnalytics searchAnalytics;
  private ArrayList<Integer> bottomNavigationItems;

  public BottomNavigationNavigator(FragmentNavigator fragmentNavigator, String defaultStoreName,
      BottomNavigationAnalytics bottomNavigationAnalytics, SearchAnalytics searchAnalytics) {
    this.defaultStoreName = defaultStoreName;
    this.bottomNavigationAnalytics = bottomNavigationAnalytics;
    this.searchAnalytics = searchAnalytics;
    bottomNavigationItems = new ArrayList<>();
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToBottomNavigationItem(int bottomNavigationPosition) {
    switch (bottomNavigationPosition) {
      case 0:
        bottomNavigationAnalytics.sendNavigateToHomeClickEvent();
        BottomHomeFragment bottomHomeFragment = new BottomHomeFragment();
        navigateToHome(bottomHomeFragment);
        break;
      case 1:
        bottomNavigationAnalytics.sendNavigateToSearchClickEvent();
        searchAnalytics.searchStart(SearchSource.BOTTOM_NAVIGATION, true);
        SearchResultFragment searchResultFragment =
            SearchResultFragment.newInstance(defaultStoreName, true);
        navigateToSearch(searchResultFragment);
        break;
      case 2:
        bottomNavigationAnalytics.sendNavigateToStoresClickEvent();
        MyStoresFragment myStoresFragment =
            MyStoresFragment.newInstance(getStoreEvent(), "default", "stores", StoreContext.home);
        navigateToStore(myStoresFragment);
        break;
      case 3:
        bottomNavigationAnalytics.sendNavigateToAppsClickEvent();
        AppsFragment appsFragment = new AppsFragment();
        navigateToApps(appsFragment);
        break;
    }
  }

  public void navigateToHome(BottomHomeFragment bottomHomeFragment) {
    navigateToSelectedFragment(0, bottomHomeFragment);
  }

  public void navigateToSearch(SearchResultFragment searchResultFragment) {
    navigateToSelectedFragment(1, searchResultFragment);
  }

  public void navigateToStore(MyStoresFragment myStoresFragment) {
    navigateToSelectedFragment(2, myStoresFragment);
  }

  public void navigateToApps(AppsFragment appsFragment) {
    navigateToSelectedFragment(3, appsFragment);
  }

  public void navigateBack() {
    int bottomNavigationPosition = bottomNavigationItems.get(1);
    bottomNavigationItems.remove(0);
    navigateBackToBottomNavigationItem(bottomNavigationPosition);
  }

  public boolean canNavigateBack() {
    return bottomNavigationItems.size() > 1;
  }

  private void checkAndReplaceItem(int newItem) {
    for (int i = 0; i < bottomNavigationItems.size(); i++) {
      if (newItem == bottomNavigationItems.get(i)) {
        bottomNavigationItems.remove(i);
      }
    }
    bottomNavigationItems.add(newItem);
  }

  private void navigateToSelectedFragment(int newItem, Fragment fragment) {
    Fragment currentFragment = fragmentNavigator.getFragment();
    if (currentFragment == null || currentFragment.getClass() != fragment.getClass()) {
      checkAndReplaceItem(newItem);
      Collections.rotate(bottomNavigationItems, 1);
      fragmentNavigator.navigateToCleaningBackStack(fragment, true);
    }
  }

  private void navigateBackToBottomNavigationItem(int bottomNavigationPosition) {
    Fragment fragment = null;
    int position = -1;
    switch (bottomNavigationPosition) {
      case 0:
        fragment = new BottomHomeFragment();
        position = 0;
        break;
      case 1:
        fragment = SearchResultFragment.newInstance(defaultStoreName, true);
        position = 1;
        break;
      case 2:
        fragment =
            MyStoresFragment.newInstance(getStoreEvent(), "default", "stores", StoreContext.home);
        position = 2;
        break;
      case 3:
        fragment = new AppsFragment();
        position = 3;
        break;
    }
    if (fragment != null && position != -1) {
      fragmentNavigator.navigateToCleaningBackStack(fragment, true);
    }
  }

  private Event getStoreEvent() {
    Event event = new Event();
    event.setAction(EVENT_ACTION);
    event.setData(null);
    event.setName(Event.Name.myStores);
    event.setType(Event.Type.CLIENT);
    return event;
  }

  public ArrayList<Integer> getBottomNavigationItems() {
    return bottomNavigationItems;
  }

  public void setBottomNavigationItems(ArrayList<Integer> savedBottomNavigationItens) {
    bottomNavigationItems = savedBottomNavigationItens;
  }
}
