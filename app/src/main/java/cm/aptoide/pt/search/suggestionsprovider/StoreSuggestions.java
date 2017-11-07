package cm.aptoide.pt.search.suggestionsprovider;

import android.content.res.Resources;
import android.database.MatrixCursor;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.search.suggestionsprovider.websocket.SearchWebSocket;
import cm.aptoide.pt.search.suggestionsprovider.websocket.SearchWebSocketProvider;
import cm.aptoide.pt.search.suggestionsprovider.websocket.WebSocketSearchRecentSuggestionsProvider;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class StoreSuggestions extends WebSocketSearchRecentSuggestionsProvider {

  private final SearchWebSocketProvider searchWebSocketProvider;
  private final BlockingQueue<MatrixCursor> blockingQueue = new ArrayBlockingQueue<>(1);
  private SearchWebSocket searchSocket;

  public StoreSuggestions() {
    super(CrashReport.getInstance());
    searchWebSocketProvider = new SearchWebSocketProvider();
  }

  @Override public BlockingQueue<MatrixCursor> getBlockingQueue() {
    return blockingQueue;
  }

  @Override public String getSearchProvider(Resources resources) {
    return resources.getString(R.string.store_suggestion_provider_authority);
  }

  @Override public SearchWebSocket getSearchSocket() {
    if (searchSocket == null) {
      searchSocket = searchWebSocketProvider.getStoreSearchSocket(blockingQueue);
    }
    return searchSocket;
  }
}
