package cm.aptoide.pt.bottomNavigation;

import cm.aptoide.pt.R;

/**
 * Created by D01 on 16/03/18.
 */

public class BottomNavigationMapper {

  static final int HOME_POSITION = 0;
  static final int SEARCH_POSITION = 1;
  static final int STORES_POSITION = 2;
  static final int APPS_POSITION = 3;
  static final int CURATION_POSITION = 4;

  public BottomNavigationItem mapItemClicked(Integer menuItemId) {
    BottomNavigationItem bottomNavigationItem;
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
      case R.id.action_curation:
        bottomNavigationItem = BottomNavigationItem.CURATION;
        break;
      default:
        throw new IllegalStateException("The selected menuItem is not supported");
    }
    return bottomNavigationItem;
  }

  public int mapToBottomNavigationPosition(BottomNavigationItem bottomNavigationItem) {
    int bottomNavigationPosition;
    switch (bottomNavigationItem) {
      case HOME:
        bottomNavigationPosition = HOME_POSITION;
        break;
      case SEARCH:
        bottomNavigationPosition = SEARCH_POSITION;
        break;
      case STORES:
        bottomNavigationPosition = STORES_POSITION;
        break;
      case APPS:
        bottomNavigationPosition = APPS_POSITION;
        break;
      case CURATION:
        bottomNavigationPosition = CURATION_POSITION;
        break;
      default:
        throw new IllegalStateException("The selected bottomNavigationItem is not supported");
    }
    return bottomNavigationPosition;
  }

  public int mapToBottomNavigationPosition(Integer menuItemId) {
    int bottomNavigationPosition;
    switch (menuItemId) {
      case R.id.action_home:
        bottomNavigationPosition = HOME_POSITION;
        break;
      case R.id.action_search:
        bottomNavigationPosition = SEARCH_POSITION;
        break;
      case R.id.action_stores:
        bottomNavigationPosition = STORES_POSITION;
        break;
      case R.id.action_apps:
        bottomNavigationPosition = APPS_POSITION;
        break;
      case R.id.action_curation:
        bottomNavigationPosition = CURATION_POSITION;
        break;
      default:
        throw new IllegalStateException("The selected menuItem is not supported");
    }
    return bottomNavigationPosition;
  }
}
