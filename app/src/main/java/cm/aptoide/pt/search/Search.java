package cm.aptoide.pt.search;

import cm.aptoide.pt.search.websocket2.ReactiveWebSocket;
import cm.aptoide.pt.search.websocket2.SocketEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;

public class Search {

  private static final int SUGGESTION_COUNT = 5;
  private final ObjectMapper objectMapper;
  private final ReactiveWebSocket webSocket;

  protected Search(ObjectMapper objectMapper, ReactiveWebSocket webSocket) {
    this.objectMapper = objectMapper;
    this.webSocket = webSocket;
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

  public Observable<List<String>> listenForSuggestions() {
    return webSocket.listen()
        .filter(event -> event.getStatus() == SocketEvent.Status.MESSAGE)
        .filter(event -> event.hasData())
        .flatMap(event -> {
          try {
            return Observable.just(getSuggestionsFrom(event.getData()));
          } catch (IOException e) {
            return Observable.error(e);
          }
        });
  }

  private List<String> getSuggestionsFrom(byte[] data) throws IOException {
    return Arrays.asList(objectMapper.readValue(data, String[].class));
  }
}
