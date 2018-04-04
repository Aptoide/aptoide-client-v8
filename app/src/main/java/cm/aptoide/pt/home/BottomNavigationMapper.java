package cm.aptoide.pt.home;

import cm.aptoide.pt.R;

/**
 * Created by D01 on 16/03/18.
 */

public class BottomNavigationMapper {

  public BottomNavigationItem mapItemClicked(Integer menuItemId) {
    BottomNavigationItem bottomNavigationItem = null;
    switch (menuItemId) {
      case R.id.action_home:
        bottomNavigationItem = BottomNavigationItem.HOME;
        break;
      case R.id.action_search:
        bottomNavigationItem = BottomNavigationItem.SEARCH;
        break;
      case R.id.action_stores:
        bottomNavigationItem = BottomNavigationItem.STORES;
        break;
      case R.id.action_apps:
        bottomNavigationItem = BottomNavigationItem.APPS;
        break;
    }
    if (bottomNavigationItem == null) {
      throw new IllegalStateException("The selected menuItem is not supported");
    }
    return bottomNavigationItem;
  }

  public int mapToBottomNavigationPosition(BottomNavigationItem bottomNavigationItem) {
    int bottomNavigationPosition = -1;
    switch (bottomNavigationItem) {
      case HOME:
        bottomNavigationPosition = 0;
        break;
      case SEARCH:
        bottomNavigationPosition = 1;
        break;
      case STORES:
        bottomNavigationPosition = 2;
        break;
      case APPS:
        bottomNavigationPosition = 3;
        break;
    }
    if (bottomNavigationPosition == -1) {
      throw new IllegalStateException("The selected bottomNavigationItem is not supported");
    }
    return bottomNavigationPosition;
  }

  public int mapToBottomNavigationPosition(Integer menuItemId) {
    int bottomNavigationPosition = -1;
    switch (menuItemId) {
      case R.id.action_home:
        bottomNavigationPosition = 0;
        break;
      case R.id.action_search:
        bottomNavigationPosition = 1;
        break;
      case R.id.action_stores:
        bottomNavigationPosition = 2;
        break;
      case R.id.action_apps:
        bottomNavigationPosition = 3;
        break;
    }
    if (bottomNavigationPosition == -1) {
      throw new IllegalStateException("The selected menuItem is not supported");
    }
    return bottomNavigationPosition;
  }
}
