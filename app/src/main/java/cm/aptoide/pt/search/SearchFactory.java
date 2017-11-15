package cm.aptoide.pt.search;

import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.search.websocket.ReactiveWebSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SearchFactory {
  private static final int SEARCH_WEB_SOCKET_PORT = 9000;
  private static final int STORE_WEB_SOCKET_PORT = 9002;

  public SearchSuggestionManager createSearchForStore() {
    return createSearch(STORE_WEB_SOCKET_PORT);
  }

  public SearchSuggestionManager createSearchForApps() {
    return createSearch(SEARCH_WEB_SOCKET_PORT);
  }

  private SearchSuggestionManager createSearch(int socketPort) {

    final String url = String.format("%s%s:%d", BuildConfig.APTOIDE_WEBSOCKETS_SCHEME,
        BuildConfig.APTOIDE_WEBSOCKETS_HOST, socketPort);

    final Request request = new Request.Builder().url(url)
        .build();

    final OkHttpClient client = new OkHttpClient();

    return new SearchSuggestionManager(new ObjectMapper(), new ReactiveWebSocket(client, request),
        CrashReport.getInstance());
  }
}
