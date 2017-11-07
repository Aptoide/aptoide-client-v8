package cm.aptoide.pt.search.websocket;

import android.database.Cursor;
import cm.aptoide.pt.BuildConfig;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by pedroribeiro on 23/01/17.
 */

public class SearchWebSocketProvider {

  private static final String WEBSOCKETS_SCHEME = BuildConfig.APTOIDE_WEBSOCKETS_SCHEME;
  private static final String WEBSOCKETS_HOST = BuildConfig.APTOIDE_WEBSOCKETS_HOST;
  private static final String SEARCH_WEBSOCKET_PORT = "9000";
  private static final String STORE_WEBSOCKET_PORT = "9002";
  private final ScheduledExecutorService scheduledExecutorService;
  private final OkHttpClient client;
  private final SearchWebSocketListener listener;

  public SearchWebSocketProvider(BlockingQueue<Cursor> blockingQueue) {
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    client = new OkHttpClient();
    listener = new SearchWebSocketListener(blockingQueue);
  }

  public SearchSocket getSearchAppsSocket() {
    Request request =
        new Request.Builder().url(WEBSOCKETS_SCHEME + WEBSOCKETS_HOST + ":" + SEARCH_WEBSOCKET_PORT)
            .build();
    return new SearchSocket(scheduledExecutorService, request, client, listener);
  }

  public SearchSocket getStoreSearchSocket() {
    Request request =
        new Request.Builder().url(WEBSOCKETS_SCHEME + WEBSOCKETS_HOST + ":" + STORE_WEBSOCKET_PORT)
            .build();
    return new SearchSocket(scheduledExecutorService, request, client, listener);
  }
}
