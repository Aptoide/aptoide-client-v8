/*
 * Copyright (c) 2016.
 * Modified by pedroribeiro on 19/01/2017
 */

package cm.aptoide.pt.search.view;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import cm.aptoide.pt.R;
import cm.aptoide.pt.search.SearchActionsHandler;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.websocket.SearchAppsWebSocket;
import cm.aptoide.pt.utils.design.ShowMessage;

/**
 * Created by neuro on 01-06-2016.
 */
public class SearchBuilder {

  private final MenuItem menuItem;
  private final Context applicationContext;
  private final SearchNavigator searchNavigator;
  private final SearchManager searchManager;
  private final String lastQuery;

  public SearchBuilder(MenuItem menuItem, Context context, SearchNavigator searchNavigator) {
    this(menuItem, context, searchNavigator, null);
  }

  public SearchBuilder(MenuItem menuItem, Context context, SearchNavigator searchNavigator,
      @Nullable String lastQuery) {
    this.applicationContext = context.getApplicationContext();
    this.searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
    this.menuItem = menuItem;
    this.searchNavigator = searchNavigator;
    this.lastQuery = lastQuery;
  }

  public void validateAndAttachSearch() {
    validateProperties();

    final SearchView searchView = (SearchView) menuItem.getActionView();
    setSearchableInfo(searchView, searchManager);

    final SearchActionsHandler actionsHandler = getSearchActionsHandler(searchView);
    searchView.setOnQueryTextListener(actionsHandler);
    searchView.setOnSuggestionListener(actionsHandler);
    searchView.setOnQueryTextFocusChangeListener(actionsHandler);
    searchView.setOnSearchClickListener(actionsHandler);
    searchView.setQueryRefinementEnabled(true);
  }

  @NonNull private SearchActionsHandler getSearchActionsHandler(SearchView searchView) {
    final UnableToSearchAction unableToSearchAction =
        () -> ShowMessage.asToast(applicationContext, R.string.search_minimum_chars);

    final QueryResultRepository queryResultRepository = (int pos) -> {
      Cursor item = (Cursor) searchView.getSuggestionsAdapter()
          .getItem(pos);
      return item.getString(1);
    };

    return new SearchActionsHandler(new SearchAppsWebSocket(), menuItem, searchNavigator,
        unableToSearchAction, queryResultRepository, lastQuery);
  }

  private void setSearchableInfo(SearchView searchView, SearchManager searchManager) {
    ComponentName componentName =
        new ComponentName(applicationContext.getApplicationContext(), SearchActivity.class);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
  }

  private void validateProperties() {
    if (searchManager == null) {
      throw new NullPointerException("SearchManager service to create search is null");
    }

    if (menuItem == null) {
      throw new NullPointerException("MenuItem to create search is null");
    }

    if (applicationContext == null) {
      throw new NullPointerException("Context to create search is null");
    }

    if (searchNavigator == null) {
      throw new NullPointerException("FragmentNavigator to create search is null");
    }
  }
}
