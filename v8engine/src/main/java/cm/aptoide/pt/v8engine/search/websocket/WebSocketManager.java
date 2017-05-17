package cm.aptoide.pt.v8engine.search.websocket;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
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
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by pedroribeiro on 16/01/17.
 */

public abstract class WebSocketManager extends WebSocketListener implements WebSocket {

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

  public static void setBlockingQueue(BlockingQueue a) {
    blockingQueue = a;
  }

  public void disconnect() {
    if (webSocket != null) {
      webSocket.close(1000, "");
    }
  }

  public WebSocket getWebSocket() {
    if (webSocket == null) {
      webSocket = reconnect();
    }
    return webSocket;
  }

  protected abstract WebSocket reconnect();

  @Override public void onOpen(WebSocket webSocket, Response response) {
    this.webSocket = webSocket;
  }

  @Override public void onMessage(WebSocket webSocket, String responseMessage) {
    super.onMessage(webSocket, responseMessage);
    try {
      JSONArray jsonArray = new JSONArray(responseMessage);
      MatrixCursor matrixCursor = new MatrixCursor(matrix_columns);
      for (int i = 0; i < jsonArray.length(); i++) {
        String suggestion = jsonArray.get(i)
            .toString();
        addRow(matrixCursor, suggestion, i);
      }
      blockingQueue.add(matrixCursor);
    } catch (JSONException e) {
      CrashReport.getInstance()
          .log(e);
    }
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

  protected void addRow(MatrixCursor matrixCursor, String string, int i) {
    matrixCursor.newRow()
        .add(null)
        .add(string)
        .add(string)
        .add(i);
  }

  @Override public Request request() {
    return webSocket.request();
  }

  @Override public long queueSize() {
    return webSocket.queueSize();
  }

  @Override public boolean send(String text) {
    if (webSocket == null) {
      connect(getPort());
    }
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
    return false;
  }

  @Override public boolean close(int code, String reason) {
    webSocket.close(code, reason);
    return false;
  }

  @Override public void cancel() {
    webSocket.cancel();
  }

  public WebSocket connect(String port) {
    request = new Request.Builder().url(WEBSOCKETS_SCHEME + WEBSOCKETS_HOST + ":" + getPort())
        .build();
    client = new OkHttpClient();
    webSocket = client.newWebSocket(request, new StoreAutoCompleteWebSocket());

    return webSocket;
  }

  protected abstract String getPort();
}
