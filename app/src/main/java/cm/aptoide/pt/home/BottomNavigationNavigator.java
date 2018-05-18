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
  private final int homePosition = 0;
  private final int searchPosition = 1;
  private final int storesPosition = 2;
  private final int appsPosition = 3;
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
      case homePosition:
        bottomNavigationAnalytics.sendNavigateToHomeClickEvent();
        navigateToHome();
        break;
      case searchPosition:
        bottomNavigationAnalytics.sendNavigateToSearchClickEvent();
        searchAnalytics.searchStart(SearchSource.BOTTOM_NAVIGATION, true);
        SearchResultFragment searchResultFragment =
            SearchResultFragment.newInstance(defaultStoreName, true);
        navigateToSearch(searchResultFragment);
        break;
      case storesPosition:
        bottomNavigationAnalytics.sendNavigateToStoresClickEvent();
        navigateToStore();
        break;
      case appsPosition:
        bottomNavigationAnalytics.sendNavigateToAppsClickEvent();
        navigateToApps();
        break;
    }
  }

  public void navigateToHome() {
    HomeFragment homeFragment = new HomeFragment();
    navigateToSelectedFragment(homePosition, homeFragment);
  }

  public void navigateToSearch(SearchResultFragment searchResultFragment) {
    navigateToSelectedFragment(searchPosition, searchResultFragment);
  }

  public void navigateToStore() {
    MyStoresFragment myStoresFragment =
        MyStoresFragment.newInstance(getStoreEvent(), "default", "stores", StoreContext.home);
    navigateToSelectedFragment(storesPosition, myStoresFragment);
  }

  public void navigateToApps() {
    AppsFragment appsFragment = new AppsFragment();
    navigateToSelectedFragment(appsPosition, appsFragment);
  }

  public void navigateBack() {
    int bottomNavigationPosition = bottomNavigationItems.get(1);
    bottomNavigationItems.remove(0);
    navigateBackToBottomNavigationItem(bottomNavigationPosition);
  }

  public boolean canNavigateBack() {
    return bottomNavigationItems.size() > 1;
  }

  private void updateBottomNavigationItemsList(int newItem) {
    int newItemPosition = bottomNavigationItems.indexOf(newItem);
    if (newItemPosition != -1) {
      bottomNavigationItems.remove(newItemPosition);
    }
    bottomNavigationItems.add(newItem);
  }

  private void navigateToSelectedFragment(int newItem, Fragment fragment) {
    Fragment currentFragment = fragmentNavigator.getFragment();
    if (currentFragment == null || currentFragment.getClass() != fragment.getClass()) {
      updateBottomNavigationItemsList(newItem);
      Collections.rotate(bottomNavigationItems, 1);
      fragmentNavigator.navigateToCleaningBackStack(fragment, true);
    }
  }

  private void navigateBackToBottomNavigationItem(int bottomNavigationPosition) {
    Fragment fragment = null;
    switch (bottomNavigationPosition) {
      case homePosition:
        fragment = new HomeFragment();
        break;
      case searchPosition:
        fragment = SearchResultFragment.newInstance(defaultStoreName, true);
        break;
      case storesPosition:
        fragment =
            MyStoresFragment.newInstance(getStoreEvent(), "default", "stores", StoreContext.home);
        break;
      case appsPosition:
        fragment = new AppsFragment();
        break;
    }
    if (fragment != null) {
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
