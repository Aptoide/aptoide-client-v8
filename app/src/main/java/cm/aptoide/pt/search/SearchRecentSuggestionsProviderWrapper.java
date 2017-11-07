package cm.aptoide.pt.search;

import android.app.SearchManager;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.search.websocket.SearchSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pedroribeiro on 18/01/17.
 */

public abstract class SearchRecentSuggestionsProviderWrapper
    extends SearchRecentSuggestionsProvider {

  private static final String TAG = SearchRecentSuggestionsProviderWrapper.class.getName();
  private final SearchSocket searchSocket;

  protected SearchRecentSuggestionsProviderWrapper(SearchSocket webSocketManager) {
    this.searchSocket = webSocketManager;
  }

  @Override public boolean onCreate() {

    setupSuggestions(getSearchProvider(), DATABASE_MODE_QUERIES);
    return super.onCreate();
  }

  @Override public Cursor query(final Uri uri, String[] projection, String selection,
      final String[] selectionArgs, String sortOrder) {
    Log.v(TAG, String.format("Search query: %s", selectionArgs[0]));

    Cursor c = super.query(uri, projection, selection, selectionArgs, sortOrder);

    if (c != null) {
      BlockingQueue<MatrixCursor> arrayBlockingQueue = new ArrayBlockingQueue<>(1);

      MatrixCursor matrixCursor = null;

      searchSocket.send(buildJson(selectionArgs[0]));

      try {
        matrixCursor = arrayBlockingQueue.poll(5, TimeUnit.SECONDS);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
          matrixCursor.newRow()
              .add(c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)))
              .add(c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_QUERY)))
              .add("1");
        }
      } catch (InterruptedException e) {
        CrashReport.getInstance()
            .log(e);
      } finally {
        c.close();
      }
      return matrixCursor;
    } else {
      return null;
    }
  }

  public abstract String getSearchProvider();

  protected String buildJson(String query) {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("query", query);
      jsonObject.put("limit", 5);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jsonObject.toString();
  }
}
