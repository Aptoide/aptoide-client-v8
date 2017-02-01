package cm.aptoide.pt.v8engine.websocket;

import android.app.SearchManager;
import android.database.MatrixCursor;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.logger.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by pedroribeiro on 23/01/17.
 */

public class SearchAppsWebSocket extends WebSocketManager {

  @Override public WebSocket connect(String port) {
    request = new Request.Builder().url(WEBSOCKETS_SCHEME + WEBSOCKETS_HOST + ":" + port).build();
    client = new OkHttpClient();
    webSocket = client.newWebSocket(request, new SearchAppsWebSocket());

    return webSocket;
  }

  @Override protected WebSocket reconnect() {
    return client.newWebSocket(request, new SearchAppsWebSocket());
  }

  @Override public WebSocket getWebSocket() {
    if (webSocket == null) {
      webSocket = reconnect();
    }
    return webSocket;
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

  @Override public boolean send(String text) {
    if (webSocket == null) {
      connect("9000");
    }
    return super.send(text);
  }
}
