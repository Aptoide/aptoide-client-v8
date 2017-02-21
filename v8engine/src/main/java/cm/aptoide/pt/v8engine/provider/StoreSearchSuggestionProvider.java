package cm.aptoide.pt.v8engine.provider;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.websocket.StoreAutoCompleteWebSocket;
import cm.aptoide.pt.v8engine.websocket.WebSocketManager;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by pedroribeiro on 30/01/17.
 */

public class StoreSearchSuggestionProvider extends SearchRecentSuggestionsProviderWrapper {

  @Override public String getSearchProvider() {
    return "cm.aptoide.pt.v8engine.provider.StoreSearchSuggestionProvider";
  }

  @Override public WebSocketManager getWebSocket() {
    return new StoreAutoCompleteWebSocket();
  }
}
