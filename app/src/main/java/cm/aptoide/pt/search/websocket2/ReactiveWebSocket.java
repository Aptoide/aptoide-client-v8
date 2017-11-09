package cm.aptoide.pt.search.websocket2;

import cm.aptoide.pt.logger.Logger;
import java.nio.charset.Charset;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import rx.Emitter;
import rx.Observable;

public class ReactiveWebSocket {

  private static final int CLOSE_STATUS_CODE = 1000;

  private final OkHttpClient client;
  private final Request request;
  private WebSocket webSocket;

  public ReactiveWebSocket(OkHttpClient client, Request request) {
    this.client = client;
    this.request = request;
  }

  public void send(String data) {
    if (webSocket != null) {
      webSocket.send(data);
    } else {
      throw new IllegalStateException(
          "Call listen() first to open the connection and wait for data.");
    }
  }

  public void send(byte[] data) {
    if (webSocket != null) {
      webSocket.send(ByteString.of(data));
    } else {
      throw new IllegalStateException(
          "Call listen() first to open the connection and wait for data.");
    }
  }

  public Observable<SocketEvent> listen() {

    // observable that emulates ping/pong in the websocket to keep the connection open.
    //Observable<Long> o1 = Observable.interval(2, TimeUnit.SECONDS)
    //    .doOnNext(__ -> {
    //      if (webSocket != null && webSocket.queueSize() == 0) {
    //        webSocket.send(ByteString.EMPTY);
    //      }
    //    });

    Observable<SocketEvent> o2 = Observable.create(emitter -> {
      webSocket = client.newWebSocket(request, new ReactiveWebSocketListener(emitter));
      emitter.setCancellation(() -> webSocket.close(CLOSE_STATUS_CODE, null));
    }, Emitter.BackpressureMode.LATEST);

    //return Observable.zip(o1, o2, (interval, event) -> event);
    return o2;
  }

  private static class ReactiveWebSocketListener extends WebSocketListener {

    private static final String TAG = ReactiveWebSocketListener.class.getName();

    private final Emitter<SocketEvent> emitter;

    private ReactiveWebSocketListener(Emitter<SocketEvent> emitter) {
      this.emitter = emitter;
    }

    @Override public void onOpen(WebSocket webSocket, Response response) {
      super.onOpen(webSocket, response);
      emitter.onNext(new SocketEvent(SocketEvent.Status.OPEN));
      Logger.v(TAG, "onOpen ::");
    }

    @Override public void onMessage(WebSocket webSocket, String text) {
      super.onMessage(webSocket, text);
      emitter.onNext(
          new SocketEvent(SocketEvent.Status.MESSAGE, text.getBytes(Charset.forName("UTF-8"))));
      Logger.v(TAG, "onMessage (String) :: message = " + text);
    }

    @Override public void onMessage(WebSocket webSocket, ByteString bytes) {
      super.onMessage(webSocket, bytes);
      emitter.onNext(new SocketEvent(SocketEvent.Status.MESSAGE, bytes.toByteArray()));
      Logger.v(TAG, "onMessage (bytes) :: size = " + bytes.size());
    }

    @Override public void onClosing(WebSocket webSocket, int code, String reason) {
      super.onClosing(webSocket, code, reason);
      emitter.onNext(new SocketEvent(SocketEvent.Status.CLOSING));
      //webSocket.close(CLOSE_STATUS_CODE, null);
      Logger.v(TAG, "onClosing :: ");
    }

    @Override public void onClosed(WebSocket webSocket, int code, String reason) {
      super.onClosed(webSocket, code, reason);
      emitter.onNext(new SocketEvent(SocketEvent.Status.CLOSED));
      emitter.onCompleted();
      Logger.v(TAG, "onClosed :: ");
    }

    @Override public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
      super.onFailure(webSocket, throwable, response);
      emitter.onError(throwable);
      Logger.v(TAG, "onFailure :: ");
    }
  }
}
