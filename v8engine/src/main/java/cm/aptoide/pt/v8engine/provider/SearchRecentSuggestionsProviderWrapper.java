package cm.aptoide.pt.v8engine.provider;

import android.app.SearchManager;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.websocket.WebSocketManager;
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

  private static final String TAG = "StoreWebsockets";

  @Override public boolean onCreate() {

    setupSuggestions(getSearchProvider(), DATABASE_MODE_QUERIES);
    return super.onCreate();
  }

  @Override public Cursor query(final Uri uri, String[] projection, String selection,
      final String[] selectionArgs, String sortOrder) {
    Log.d(TAG, "search-query: " + selectionArgs[0]);

    Cursor c = super.query(uri, projection, selection, selectionArgs, sortOrder);

    if (c != null) {
      BlockingQueue<MatrixCursor> arrayBlockingQueue = new ArrayBlockingQueue<MatrixCursor>(1);
      WebSocketManager.setBlockingQueue(arrayBlockingQueue);

      MatrixCursor matrixCursor = null;

      WebSocketManager storeAutoCompleteWebSocket = getWebSocket();
      storeAutoCompleteWebSocket.send(buildJson(selectionArgs[0]));
      //SearchWebSocketManager.getWebSocket().send();
      try {
        matrixCursor = arrayBlockingQueue.poll(5, TimeUnit.SECONDS);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
          matrixCursor.newRow()
              .add(c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_1)))
              .add(c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)))
              .add(c.getString(c.getColumnIndex(SearchManager.SUGGEST_COLUMN_QUERY)))
              .add("1");
        }
      } catch (InterruptedException e) {
        CrashReport.getInstance().log(e);
      } finally {
        c.close();
      }
      return matrixCursor;
    } else {
      return null;
    }
  }

  public abstract String getSearchProvider();

  public abstract WebSocketManager getWebSocket();

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
