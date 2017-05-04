package cm.aptoide.pt.v8engine.search.websocket;

import okhttp3.WebSocket;

/**
 * Created by pedroribeiro on 23/01/17.
 */

public class StoreAutoCompleteWebSocket extends WebSocketManager {

  public static String STORE_WEBSOCKET_PORT = "9002";

  @Override protected WebSocket reconnect() {
    return client.newWebSocket(request, new StoreAutoCompleteWebSocket());
  }

  @Override protected String getPort() {
    return STORE_WEBSOCKET_PORT;
  }
}
