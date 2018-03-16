package cm.aptoide.pt.home;

import cm.aptoide.pt.R;

/**
 * Created by D01 on 16/03/18.
 */

public class BottomNavigationMapper {

  public BottomNavigationItem mapItemClicked(Integer menuItemId) {
    switch (menuItemId) {
      case R.id.action_home:
        return BottomNavigationItem.HOME;
      case R.id.action_search:
        return BottomNavigationItem.SEARCH;
      case R.id.action_stores:
        return BottomNavigationItem.STORES;
      case R.id.action_apps:
        return BottomNavigationItem.APPS;
    }
    return null;
  }
}
