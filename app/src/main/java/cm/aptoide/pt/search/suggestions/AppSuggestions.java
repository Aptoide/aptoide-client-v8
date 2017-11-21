/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.search.suggestions;

import android.database.MatrixCursor;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.search.suggestions.websocket.SearchWebSocket;
import cm.aptoide.pt.search.suggestions.websocket.SearchWebSocketProvider;
import cm.aptoide.pt.search.suggestions.websocket.WebSocketSearchRecentSuggestionsProvider;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Deprecated
public class AppSuggestions extends WebSocketSearchRecentSuggestionsProvider {


  private SearchWebSocketProvider searchWebSocketProvider = new SearchWebSocketProvider();
  private BlockingQueue<MatrixCursor> blockingQueue = new ArrayBlockingQueue<>(1);

  public AppSuggestions() {
    super(CrashReport.getInstance());
  }

  public SearchWebSocket getSearchWebSocket() {
    if (searchWebSocketProvider == null) {
      searchWebSocketProvider = new SearchWebSocketProvider();
      blockingQueue = new ArrayBlockingQueue<>(1);
    }
    blockingQueue.clear();
    return searchWebSocketProvider.getSearchAppsSocket(blockingQueue);
  }

  public BlockingQueue<MatrixCursor> getBlockingQueue() {
    return blockingQueue;
  }

  public String getAuthority() {
    return getContext().getResources()
        .getString(R.string.search_suggestion_provider_authority);
  }

}
