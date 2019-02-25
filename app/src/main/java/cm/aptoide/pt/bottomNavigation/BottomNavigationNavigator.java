package cm.aptoide.pt.bottomNavigation;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.CurationListFragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.home.HomeFragment;
import cm.aptoide.pt.home.apps.AppsFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.analytics.SearchSource;
import cm.aptoide.pt.search.view.SearchResultFragment;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import java.util.ArrayList;
import java.util.Collections;

import static cm.aptoide.pt.bottomNavigation.BottomNavigationMapper.APPS_POSITION;
import static cm.aptoide.pt.bottomNavigation.BottomNavigationMapper.CURATION_POSITION;
import static cm.aptoide.pt.bottomNavigation.BottomNavigationMapper.HOME_POSITION;
import static cm.aptoide.pt.bottomNavigation.BottomNavigationMapper.SEARCH_POSITION;
import static cm.aptoide.pt.bottomNavigation.BottomNavigationMapper.STORES_POSITION;

/**
 * Created by D01 on 02/04/18.
 */

public class BottomNavigationNavigator {

  private final static String EVENT_ACTION =
      "https://ws75.aptoide.com/api/7/getStoreWidgets/store_id=15/context=stores";
  private final FragmentNavigator fragmentNavigator;
  private final BottomNavigationAnalytics bottomNavigationAnalytics;
  private final SearchAnalytics searchAnalytics;
  private final String theme;
  private ArrayList<Integer> bottomNavigationItems;

  public BottomNavigationNavigator(FragmentNavigator fragmentNavigator,
      BottomNavigationAnalytics bottomNavigationAnalytics, SearchAnalytics searchAnalytics,
      String theme) {
    this.bottomNavigationAnalytics = bottomNavigationAnalytics;
    this.searchAnalytics = searchAnalytics;
    this.theme = theme;
    bottomNavigationItems = new ArrayList<>();
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToBottomNavigationItem(int bottomNavigationPosition) {
    switch (bottomNavigationPosition) {
      case HOME_POSITION:
        bottomNavigationAnalytics.sendNavigateToHomeClickEvent();
        navigateToHome();
        break;
      case SEARCH_POSITION:
        bottomNavigationAnalytics.sendNavigateToSearchClickEvent();
        searchAnalytics.searchStart(SearchSource.BOTTOM_NAVIGATION, true);
        SearchResultFragment searchResultFragment = SearchResultFragment.newInstance(true);
        navigateToSearch(searchResultFragment);
        break;
      case STORES_POSITION:
        bottomNavigationAnalytics.sendNavigateToStoresClickEvent();
        navigateToStore();
        break;
      case APPS_POSITION:
        bottomNavigationAnalytics.sendNavigateToAppsClickEvent();
        navigateToApps();
        break;
      case CURATION_POSITION:
        bottomNavigationAnalytics.sendNavigateToCurationClickEvent();
        navigateToCuration();
        break;
    }
  }

  public void navigateToHome() {
    HomeFragment homeFragment = new HomeFragment();
    navigateToSelectedFragment(HOME_POSITION, homeFragment);
  }

  public void navigateToSearch(SearchResultFragment searchResultFragment) {
    navigateToSelectedFragment(SEARCH_POSITION, searchResultFragment);
  }

  public void navigateToStore() {
    MyStoresFragment myStoresFragment =
        MyStoresFragment.newInstance(getStoreEvent(), theme, "stores", StoreContext.home);
    navigateToSelectedFragment(STORES_POSITION, myStoresFragment);
  }

  public void navigateToApps() {
    AppsFragment appsFragment = new AppsFragment();
    navigateToSelectedFragment(APPS_POSITION, appsFragment);
  }

  public void navigateToCuration() {
    CurationListFragment curationListFragment = new CurationListFragment();
    navigateToSelectedFragment(CURATION_POSITION, curationListFragment);
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
      case HOME_POSITION:
        fragment = new HomeFragment();
        break;
      case SEARCH_POSITION:
        fragment = SearchResultFragment.newInstance(true);
        break;
      case STORES_POSITION:
        fragment = MyStoresFragment.newInstance(getStoreEvent(), "", "stores", StoreContext.home);
        break;
      case APPS_POSITION:
        fragment = new AppsFragment();
        break;
      case CURATION_POSITION:
        fragment = new CurationListFragment();
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

  public void setBottomNavigationItems(ArrayList<Integer> savedBottomNavigationItems) {
    bottomNavigationItems = savedBottomNavigationItems;
  }
}
