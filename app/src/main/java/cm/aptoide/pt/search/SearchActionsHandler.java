package cm.aptoide.pt.search;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.pt.search.websocket.SearchAppsWebSocket;

public class SearchActionsHandler
    implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener,
    View.OnFocusChangeListener, View.OnClickListener {

  private final static String SEARCH_WEB_SOCKET = "9000";

  private final SearchAppsWebSocket searchAppsWebSocket;
  private final MenuItem menuItem;
  private final SearchNavigator searchNavigator;
  private final UnableToSearchAction unableToSearchAction;
  private final QueryResultRepository queryResultRepository;

  public SearchActionsHandler(SearchAppsWebSocket searchAppsWebSocket, MenuItem menuItem,
      SearchNavigator searchNavigator, UnableToSearchAction unableToSearchAction,
      QueryResultRepository queryResultRepository) {
    this.searchAppsWebSocket = searchAppsWebSocket;
    this.menuItem = menuItem;
    this.searchNavigator = searchNavigator;
    this.unableToSearchAction = unableToSearchAction;
    this.queryResultRepository = queryResultRepository;
  }

  @Override public boolean onQueryTextSubmit(String query) {
    MenuItemCompat.collapseActionView(menuItem);
    if (query.length() > 1) {
      searchNavigator.navigate(query);
    } else {
      unableToSearchAction.call();
    }
    return true;
  }

  @Override public boolean onQueryTextChange(String s) {
    return false;
  }

  @Override public boolean onSuggestionSelect(int position) {
    return false;
  }

  @Override public boolean onSuggestionClick(int position) {
    final String query = queryResultRepository.getQueryAt(position);
    searchNavigator.navigate(query);
    return true;
  }

  @Override public void onFocusChange(View v, boolean hasFocus) {
    if (!hasFocus) {
      MenuItemCompat.collapseActionView(menuItem);
      searchAppsWebSocket.disconnect();
    }
  }

  @Override public void onClick(View v) {
    searchAppsWebSocket.connect(SEARCH_WEB_SOCKET);
  }
}
