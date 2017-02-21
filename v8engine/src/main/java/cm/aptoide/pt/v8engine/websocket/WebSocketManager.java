package cm.aptoide.pt.v8engine.websocket;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import cm.aptoide.pt.v8engine.BuildConfig;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by pedroribeiro on 16/01/17.
 */

public class WebSocketManager extends WebSocketListener implements WebSocket {

  public static final String WEBSOCKETS_SCHEME = BuildConfig.APTOIDE_WEBSOCKETS_SCHEME;
  public static final String WEBSOCKETS_HOST = BuildConfig.APTOIDE_WEBSOCKETS_HOST;
  protected static final String TAG = "Websockets";
  public static BlockingQueue<Cursor> blockingQueue;
  protected static String[] matrix_columns = new String[] {
      SearchManager.SUGGEST_COLUMN_ICON_1, SearchManager.SUGGEST_COLUMN_TEXT_1,
      SearchManager.SUGGEST_COLUMN_QUERY, "_id"
  };
  static WebSocket webSocket;
  static Request request;
  static OkHttpClient client;
  ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
  ScheduledFuture<?> future;

  public WebSocketManager() {
  }

  public void disconnect() {
    if (webSocket != null) {
      webSocket.close(1000, "");
    }
  }

  public static void setBlockingQueue(BlockingQueue a) {
    blockingQueue = a;
  }

  public WebSocket getWebSocket() {
    if (webSocket == null) {
      webSocket = reconnect();
    }
    return webSocket;
  }

  protected WebSocket reconnect() {
    return client.newWebSocket(request, new WebSocketManager());
  }

  public WebSocket connect(String port) {
    request = new Request.Builder().url(WEBSOCKETS_SCHEME + WEBSOCKETS_HOST + ":" + port).build();
    client = new OkHttpClient();
    webSocket = client.newWebSocket(request, new WebSocketManager());

    return webSocket;
  }

  @Override public void onOpen(WebSocket webSocket, Response response) {
    this.webSocket = webSocket;
  }

  @Override public void onMessage(WebSocket webSocket, ByteString bytes) {
    super.onMessage(webSocket, bytes);
  }

  @Override public void onClosing(WebSocket webSocket, int code, String reason) {
    Log.d(TAG, reason);
    webSocket.close(1000, null);
    this.webSocket = null;
  }

  @Override public void onClosed(WebSocket webSocket, int code, String reason) {
    Log.d(TAG, reason);
    super.onClosed(webSocket, code, reason);
    //TODO: Log no messages passing because socket is closed
  }

  @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
    Log.d(TAG, "Error was:", t);
    super.onFailure(webSocket, t, response);
    this.webSocket = null;
  }

  @Override public Request request() {
    return webSocket.request();
  }

  @Override public long queueSize() {
    return webSocket.queueSize();
  }

  @Override public boolean send(String text) {
    final boolean[] result = { false };
    Runnable runnable = () -> {
      result[0] = webSocket.send(text);
    };

    if (future != null) {
      future.cancel(false);
    }

    future = scheduledExecutorService.schedule(runnable, 500L, TimeUnit.MILLISECONDS);
    return result[0];
  }

  @Override public boolean send(ByteString bytes) {
    webSocket.send(bytes);
    return true;
  }

  @Override public boolean close(int code, String reason) {
    webSocket.close(code, reason);
    return false;
  }

  @Override public void cancel() {
    webSocket.cancel();
  }

  protected void addRow(MatrixCursor matrixCursor, String string, int i) {
    matrixCursor.newRow().add(null).add(string).add(string).add(i);
  }
}
