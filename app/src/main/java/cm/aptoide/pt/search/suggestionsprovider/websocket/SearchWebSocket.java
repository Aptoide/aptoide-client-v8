package cm.aptoide.pt.search.suggestionsprovider.websocket;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

@Deprecated public class SearchWebSocket implements WebSocket {

  private static final int CLOSE_STATUS_CODE = 1000;
  private static final String CLOSE_REASON = "";

  private final Request request;
  private final OkHttpClient client;
  private final ScheduledExecutorService scheduledExecutorService;
  private final WebSocketListener listener;
  private WebSocket webSocket;
  private ScheduledFuture future;

  SearchWebSocket(ScheduledExecutorService scheduledExecutorService, Request request,
      OkHttpClient client, WebSocketListener listener) {
    this.scheduledExecutorService = scheduledExecutorService;
    this.request = request;
    this.client = client;
    this.listener = listener;
  }

  @Override public Request request() {
    return webSocket.request();
  }

  @Override public long queueSize() {
    return webSocket.queueSize();
  }

  @Override public boolean send(String text) {
    final Runnable runnable = () -> webSocket.send(text);
    sendData(runnable);
    return true;
  }

  @Override public boolean send(ByteString bytes) {
    final Runnable runnable = () -> webSocket.send(bytes);
    sendData(runnable);
    return true;
  }

  @Override public boolean close(int code, String reason) {
    webSocket.close(code, reason);
    return false;
  }

  @Override public void cancel() {
    webSocket.cancel();
  }

  private void sendData(Runnable runnable) {
    if (webSocket == null) {
      webSocket = client.newWebSocket(request, listener);
    }

    if (future != null) {
      future.cancel(true);
    }

    future = scheduledExecutorService.schedule(runnable, 500L, TimeUnit.MILLISECONDS);
  }

  public void disconnect() {
    if (webSocket != null) {
      webSocket.close(CLOSE_STATUS_CODE, CLOSE_REASON);
    }
  }
}
