package cm.aptoide.pt.search;

import cm.aptoide.pt.R;
import cm.aptoide.pt.search.websocket.StoreAutoCompleteWebSocket;
import cm.aptoide.pt.search.websocket.WebSocketManager;

public class StoreSearchSuggestionProvider extends SearchRecentSuggestionsProviderWrapper {

  @Override public String getSearchProvider() {
    return getContext().getResources()
        .getString(R.string.store_suggestion_provider_authority);
  }

  @Override public WebSocketManager getWebSocket() {
    return new StoreAutoCompleteWebSocket();
  }
}
