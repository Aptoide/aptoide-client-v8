package cm.aptoide.pt.v8engine.search.websocket;

import okhttp3.WebSocket;

/**
 * Created by pedroribeiro on 23/01/17.
 */

public class SearchAppsWebSocket extends WebSocketManager {

  public static String SEARCH_WEBSOCKET_PORT = "9000";

  @Override protected WebSocket reconnect() {
    return client.newWebSocket(request, new SearchAppsWebSocket());
  }

  @Override protected String getPort() {
    return SEARCH_WEBSOCKET_PORT;
  }
}
