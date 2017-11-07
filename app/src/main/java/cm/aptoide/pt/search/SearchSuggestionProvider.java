/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.search;

import android.database.Cursor;
import cm.aptoide.pt.R;
import cm.aptoide.pt.search.websocket.SearchSocket;
import cm.aptoide.pt.search.websocket.SearchWebSocketProvider;
import java.util.concurrent.ArrayBlockingQueue;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProviderWrapper {

  public SearchSuggestionProvider() {
    final ArrayBlockingQueue<Cursor> queue = new ArrayBlockingQueue<>(3);
    final SearchWebSocketProvider searchWebSocketProvider = new SearchWebSocketProvider(queue);
    super(searchWebSocketProvider);
  }

  @Override public String getSearchProvider() {
    return getContext().getResources()
        .getString(R.string.search_suggestion_provider_authority);
  }

}
