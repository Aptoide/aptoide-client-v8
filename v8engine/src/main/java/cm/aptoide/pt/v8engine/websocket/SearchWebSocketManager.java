package cm.aptoide.pt.v8engine.websocket;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.logger.Logger;
import java.util.concurrent.BlockingQueue;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.json.JSONArray;
import org.json.JSONException;

import static cm.aptoide.pt.v8engine.websocket.WebSocketSingleton.WEBSOCKETS_HOST;
import static cm.aptoide.pt.v8engine.websocket.WebSocketSingleton.WEBSOCKETS_PORT;
import static cm.aptoide.pt.v8engine.websocket.WebSocketSingleton.WEBSOCKETS_SCHEME;

/**
 * Created by pedroribeiro on 16/01/17.
 */

public class SearchWebSocketManager extends WebSocketListener {

  static WebSocket webSocket;
  static Request request;
  static OkHttpClient client;
  public static BlockingQueue<Cursor> blockingQueue;
  String[] matrix_columns = new String[] {
      SearchManager.SUGGEST_COLUMN_ICON_1, SearchManager.SUGGEST_COLUMN_TEXT_1,
      SearchManager.SUGGEST_COLUMN_QUERY, "_id"};

  public WebSocket connect() {
    request = new Request.Builder().url(WEBSOCKETS_SCHEME + WEBSOCKETS_HOST + ":" + WEBSOCKETS_PORT).build();
    client = new OkHttpClient();
    webSocket = client.newWebSocket(request, new SearchWebSocketManager()) ;

    return webSocket;
  }

  public static void disconnect() {
    if (webSocket != null) {
      webSocket.close(1000, "");
    }
  }

  private static WebSocket reconnect() {
    return client.newWebSocket(request, new SearchWebSocketManager());
  }

  public static WebSocket getWebSocket() {
    if (webSocket == null) {
      webSocket = reconnect();
    }
    return webSocket;
  }

  @Override public void onOpen(WebSocket webSocket, Response response) {
    this.webSocket = webSocket;
  }

  @Override public void onMessage(WebSocket webSocket, String responseMessage) {
    super.onMessage(webSocket, responseMessage);
    try {
      JSONArray jsonArray = new JSONArray(responseMessage);
      MatrixCursor matrixCursor = new MatrixCursor(matrix_columns);
      for (int i = 0; i < jsonArray.length(); i++) {
        String suggestion = jsonArray.get(i).toString();
        addRow(matrixCursor, suggestion, i);
      }

      blockingQueue.add(matrixCursor);
    } catch (JSONException e) {
      Logger.printException(e);
      CrashReports.logException(e);
    }
  }

  @Override public void onMessage(WebSocket webSocket, ByteString bytes) {
    super.onMessage(webSocket, bytes);
  }

  @Override public void onClosing(WebSocket webSocket, int code, String reason) {
    webSocket.close(1000, null);
    this.webSocket = null;
  }

  @Override public void onClosed(WebSocket webSocket, int code, String reason) {
    super.onClosed(webSocket, code, reason);
    //TODO: Log no messages passing because socket is closed
  }

  @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
    super.onFailure(webSocket, t, response);
    this.webSocket = null;
    //TODO: Log the failure messages.
  }

  private void addRow(MatrixCursor matrixCursor, String string, int i) {
    matrixCursor.newRow().add(null).add(string).add(string).add(i);
  }

  public static void setBlockingQueue(BlockingQueue a) {
    blockingQueue = a;
  }
}
