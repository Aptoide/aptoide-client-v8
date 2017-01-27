package cm.aptoide.pt.v8engine.websocket;

import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.logger.Logger;
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

  @Getter private static List<String> results = new ArrayList<String>();
  StoreAutoCompleteAdapter storeAutoCompleteAdapter1 = new StoreAutoCompleteAdapter(Application.getContext());

  @Override public WebSocket connect(String port) {
    request = new Request.Builder().url(WEBSOCKETS_SCHEME + WEBSOCKETS_HOST + ":" + port).build();
    client = new OkHttpClient();
    webSocket = client.newWebSocket(request, new StoreAutoCompleteWebSocket());

    return webSocket;
  }

  @Override public void onMessage(WebSocket webSocket, String responseMessage) {
    super.onMessage(webSocket, responseMessage);
    try {
      JSONArray jsonArray = new JSONArray(responseMessage);
      results.clear();
      for (int i = 0; i < jsonArray.length(); i++) {
        results.add(jsonArray.get(i).toString());
      }
      if (storeAutoCompleteAdapter1 != null) {
        storeAutoCompleteAdapter1.updateSuggestions(results);
        storeAutoCompleteAdapter1.notifyDataSetChanged();
      }
    } catch (JSONException e) {
      Logger.printException(e);
      CrashReports.logException(e);
    }
  }

  @Override protected WebSocket reconnect() {
    return client.newWebSocket(request, new StoreAutoCompleteWebSocket());
  }

  @Override public WebSocket getWebSocket() {
    if (webSocket == null) {
      webSocket = reconnect();
    }
    return webSocket;
  }

  public void sendAndReceive(String query, StoreAutoCompleteAdapter storeAutoCompleteAdapter) {
    send(query);
    storeAutoCompleteAdapter1 = storeAutoCompleteAdapter;
  }
}
