package cm.aptoide.pt.search;

import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.search.websocket.ReactiveWebSocket;
import cm.aptoide.pt.search.websocket.SocketEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;

public class SearchSuggestionManager {

  private static final int SUGGESTION_COUNT = 5;
  private final ObjectMapper objectMapper;
  private final ReactiveWebSocket webSocket;
  private final CrashReport crashReport;

  SearchSuggestionManager(ObjectMapper objectMapper, ReactiveWebSocket webSocket,
      CrashReport crashReport) {
    this.objectMapper = objectMapper;
    this.webSocket = webSocket;
    this.crashReport = crashReport;
  }

  public void getSuggestionsFor(String query) {
    if (webSocket != null) {
      webSocket.send(buildPayload(query));
    }
  }

  private String buildPayload(String query) {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("query", query);
      jsonObject.put("limit", SUGGESTION_COUNT);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObject.toString();
  }

  private Observable<List<String>> listenWebSocket() {
    return webSocket.listen()
        .flatMap(event -> {
          if (event.getStatus() == SocketEvent.Status.FAILURE) {
            return Observable.error(new SearchFailureException("Socket failure"));
          }

          return Observable.just(event);
        })
        .filter(event -> event.getStatus() == SocketEvent.Status.MESSAGE)
        .filter(event -> event.hasData())
        .flatMap(event -> {
          try {
            return Observable.just(getSuggestionsFrom(event.getData()));
          } catch (IOException e) {
            return Observable.error(new SearchFailureException(e));
          }
        });
  }

  public Observable<List<String>> listenForSuggestions() {
    return listenWebSocket().doOnError(throwable -> {
      if (SearchFailureException.class.isAssignableFrom(throwable.getClass())) {
        crashReport.log(throwable);
      }
    })
        .retry((retryCount, throwable) -> {
          if (SearchFailureException.class.isAssignableFrom(throwable.getClass())) {
            return true;
          }
          return false;
        })
        .filter(data -> data != null && data.size() > 0);
  }

  private List<String> getSuggestionsFrom(byte[] data) throws IOException {
    return Arrays.asList(objectMapper.readValue(data, String[].class));
  }
}
