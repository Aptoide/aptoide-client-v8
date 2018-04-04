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
  private final AptoideBottomNavigator aptoideBottomNavigator;
  private final String defaultStoreName;
  private final BottomNavigationAnalytics bottomNavigationAnalytics;
  private final SearchAnalytics searchAnalytics;
  private ArrayList<Integer> bottomNavigationItens;

  public BottomNavigationNavigator(AptoideBottomNavigator aptoideBottomNavigator,
      FragmentNavigator fragmentNavigator, String defaultStoreName,
      BottomNavigationAnalytics bottomNavigationAnalytics, SearchAnalytics searchAnalytics) {
    this.aptoideBottomNavigator = aptoideBottomNavigator;
    this.defaultStoreName = defaultStoreName;
    this.bottomNavigationAnalytics = bottomNavigationAnalytics;
    this.searchAnalytics = searchAnalytics;
    bottomNavigationItens = new ArrayList<>();
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
    int bottomNavigationPosition = bottomNavigationItens.get(1);
    bottomNavigationItens.remove(0);
    navigateBackToBottomNavigationItem(bottomNavigationPosition);
  }

  public boolean canNavigateBack() {
    return bottomNavigationItens.size() > 1;
  }

  private void checkAndReplaceItem(int newItem) {
    for (int i = 0; i < bottomNavigationItens.size(); i++) {
      if (newItem == bottomNavigationItens.get(i)) {
        bottomNavigationItens.remove(i);
      }
    }
    bottomNavigationItens.add(newItem);
  }

  private void navigateToSelectedFragment(int newItem, Fragment fragment) {
    Fragment currentFragment = fragmentNavigator.getFragment();
    if (currentFragment == null || currentFragment.getClass() != fragment.getClass()) {
      checkAndReplaceItem(newItem);
      Collections.rotate(bottomNavigationItens, 1);
      fragmentNavigator.navigateToCleaningBackStack(fragment, true);
      requestFocus(newItem);
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
      requestFocus(position);
    }
  }

  private void requestFocus(int position) {
    aptoideBottomNavigator.setFocusOnBottomNavigationItem(position);
  }

  private Event getStoreEvent() {
    Event event = new Event();
    event.setAction(EVENT_ACTION);
    event.setData(null);
    event.setName(Event.Name.myStores);
    event.setType(Event.Type.CLIENT);
    return event;
  }

  public ArrayList<Integer> getBottomNavigationItens() {
    return bottomNavigationItens;
  }

  public void setBottomNavigationItens(ArrayList<Integer> savedBottomNavigationItens) {
    bottomNavigationItens = savedBottomNavigationItens;
  }
}
