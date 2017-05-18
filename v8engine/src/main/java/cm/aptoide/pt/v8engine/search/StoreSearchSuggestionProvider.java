package cm.aptoide.pt.v8engine.search;

import cm.aptoide.pt.v8engine.search.websocket.StoreAutoCompleteWebSocket;
import cm.aptoide.pt.v8engine.search.websocket.WebSocketManager;

/**
 * Created by pedroribeiro on 30/01/17.
 */

public class StoreSearchSuggestionProvider extends SearchRecentSuggestionsProviderWrapper {

  @Override public String getSearchProvider() {
    return "cm.aptoide.pt.v8engine.provider.StoreSearchSuggestionProvider";
  }

  @Override public WebSocketManager getWebSocket() {
    return new StoreAutoCompleteWebSocket();
  }
}
