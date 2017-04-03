/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.v8engine.search;

import cm.aptoide.pt.v8engine.websocket.SearchAppsWebSocket;
import cm.aptoide.pt.v8engine.websocket.WebSocketManager;

/**
 * Refactored by pedroribeiro in 17/01/2017
 */
public class SearchSuggestionProvider extends SearchRecentSuggestionsProviderWrapper {

  @Override public String getSearchProvider() {
    return "cm.aptoide.pt.v8engine.provider.SearchSuggestionProvider";
  }

  @Override public WebSocketManager getWebSocket() {
    return new SearchAppsWebSocket();
  }
}
