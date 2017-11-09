package cm.aptoide.pt.search.suggestionsprovider.websocket;

import android.database.MatrixCursor;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.crashreports.CrashReport;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@Deprecated
public class SearchWebSocketProvider {

  private static final String WEBSOCKETS_SCHEME = BuildConfig.APTOIDE_WEBSOCKETS_SCHEME;
  private static final String WEBSOCKETS_HOST = BuildConfig.APTOIDE_WEBSOCKETS_HOST;
  private static final String STORE_WEBSOCKET_PORT = "9002";
  private static final String SEARCH_WEBSOCKET_PORT = "9000";

  private final ScheduledExecutorService scheduledExecutorService;
  private final OkHttpClient client;

  public SearchWebSocketProvider() {
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    client = new OkHttpClient();
  }

  public SearchWebSocket getSearchAppsSocket(BlockingQueue<MatrixCursor> blockingQueue) {
    final Request request =
        new Request.Builder().url(WEBSOCKETS_SCHEME + WEBSOCKETS_HOST + ":" + SEARCH_WEBSOCKET_PORT)
            .build();
    final SearchWebSocketListener listener =
        new SearchWebSocketListener(blockingQueue, CrashReport.getInstance());
    return new SearchWebSocket(scheduledExecutorService, request, client, listener);
  }

  public SearchWebSocket getStoreSearchSocket(BlockingQueue<MatrixCursor> blockingQueue) {
    Request request =
        new Request.Builder().url(WEBSOCKETS_SCHEME + WEBSOCKETS_HOST + ":" + STORE_WEBSOCKET_PORT)
            .build();
    final SearchWebSocketListener listener =
        new SearchWebSocketListener(blockingQueue, CrashReport.getInstance());
    return new SearchWebSocket(scheduledExecutorService, request, client, listener);
  }
}
