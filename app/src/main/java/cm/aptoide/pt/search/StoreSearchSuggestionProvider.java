package cm.aptoide.pt.search;

import cm.aptoide.pt.R;
import cm.aptoide.pt.search.websocket.SearchSocket;
import cm.aptoide.pt.search.websocket.StoreAutoCompleteWebSocket;

public class StoreSearchSuggestionProvider extends SearchRecentSuggestionsProviderWrapper {

  public StoreSearchSuggestionProvider() {
    
    super(webSocketManager);
  }

  @Override public String getSearchProvider() {
    return getContext().getResources()
        .getString(R.string.store_suggestion_provider_authority);
  }
}
