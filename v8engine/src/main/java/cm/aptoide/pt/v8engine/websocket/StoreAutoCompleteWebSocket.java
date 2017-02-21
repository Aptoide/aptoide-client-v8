package cm.aptoide.pt.v8engine.websocket;

import android.database.MatrixCursor;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.adapters.StoreAutoCompleteAdapter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by pedroribeiro on 23/01/17.
 */

public class StoreAutoCompleteWebSocket extends WebSocketManager {

  public static String STORE_WEBSOCKET_PORT = "9002";

  @Getter private static List<String> results = new ArrayList<String>();
  StoreAutoCompleteAdapter storeAutoCompleteAdapter1 =
      new StoreAutoCompleteAdapter(Application.getContext());

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
      CrashReport.getInstance().log(e);
    }
  }

  @Override public WebSocket getWebSocket() {
    if (webSocket == null) {
      webSocket = reconnect();
    }
    return webSocket;
  }

  @Override protected WebSocket reconnect() {
    return client.newWebSocket(request, new StoreAutoCompleteWebSocket());
  }

  @Override public WebSocket connect(String port) {
    request = new Request.Builder().url(WEBSOCKETS_SCHEME + WEBSOCKETS_HOST + ":" + port).build();
    client = new OkHttpClient();
    webSocket = client.newWebSocket(request, new StoreAutoCompleteWebSocket());

    return webSocket;
  }

  @Override public boolean send(String text) {
    if (webSocket == null) {
      connect(STORE_WEBSOCKET_PORT);
    }
    return super.send(text);
  }

  public void sendAndReceive(String query, StoreAutoCompleteAdapter storeAutoCompleteAdapter) {
    send(query);
    storeAutoCompleteAdapter1 = storeAutoCompleteAdapter;
  }
}
