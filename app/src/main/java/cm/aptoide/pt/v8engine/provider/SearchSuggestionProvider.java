/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.provider;

import cm.aptoide.pt.search.SearchRecentSuggestionsProviderWrapper;
import cm.aptoide.pt.search.websocket.SearchAppsWebSocket;
import cm.aptoide.pt.search.websocket.WebSocketManager;

/**
 * Refactored by pedroribeiro in 17/01/2017
 */
public class SearchSuggestionProvider extends SearchRecentSuggestionsProviderWrapper {

  @Override public String getSearchProvider() {
    return getContext().getResources()
        .getString(cm.aptoide.pt.R.string.suggested_searchable_authority);
  }

  @Override public WebSocketManager getWebSocket() {
    return new SearchAppsWebSocket();
  }
}
