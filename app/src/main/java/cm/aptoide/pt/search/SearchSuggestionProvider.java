/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.search;

import cm.aptoide.pt.R;
import cm.aptoide.pt.search.websocket.SearchAppsWebSocket;
import cm.aptoide.pt.search.websocket.WebSocketManager;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProviderWrapper {

  @Override public String getSearchProvider() {
    return getContext().getResources()
        .getString(R.string.search_suggestion_provider_authority);
  }

  @Override public WebSocketManager getWebSocket() {
    return new SearchAppsWebSocket();
  }
}
