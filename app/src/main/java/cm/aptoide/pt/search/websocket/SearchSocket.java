package cm.aptoide.pt.search.websocket;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by pedroribeiro on 16/01/17.
 */

public class SearchSocket implements WebSocket {

  private final Request request;
  private final OkHttpClient client;
  private final ScheduledExecutorService scheduledExecutorService;
  private final WebSocketListener listener;
  private WebSocket webSocket;
  private ScheduledFuture future;

  public SearchSocket(ScheduledExecutorService scheduledExecutorService, Request request,
      OkHttpClient client, WebSocketListener listener) {
    this.scheduledExecutorService = scheduledExecutorService;
    this.request = request;
    this.client = client;
    this.listener = listener;
  }

  public void disconnect() {
    if (webSocket != null) {
      webSocket.close(1000, "");
    }
  }

  @Override public Request request() {
    return webSocket.request();
  }

  @Override public long queueSize() {
    return webSocket.queueSize();
  }

  @Override public boolean send(String text) {
    if (webSocket == null) {
      webSocket = connect();
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

  public WebSocket connect() {
    return client.newWebSocket(request, listener);
  }
}
