package cm.aptoide.pt.search.suggestionsprovider.websocket;

import android.content.SearchRecentSuggestionsProvider;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import cm.aptoide.pt.crashreports.CrashReport;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class WebSocketSearchRecentSuggestionsProvider extends SearchRecentSuggestionsProvider {

  private final CrashReport crashReport;

  public WebSocketSearchRecentSuggestionsProvider(CrashReport crashReport) {
    this.crashReport = crashReport;
  }

  @Override public boolean onCreate() {
    setupSuggestions(getSearchProvider(getContext().getResources()), DATABASE_MODE_QUERIES);
    return super.onCreate();
  }

  @Nullable @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
      @Nullable String[] selectionArgs, @Nullable String sortOrder,
      @Nullable CancellationSignal cancellationSignal) {

    // TODO use the cancellationSignal with the blocking queue

    return super.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
  }

  @Nullable @Override public Cursor query(final Uri uri, String[] projection, String selection,
      final String[] selectionArgs, String sortOrder) {

    final Cursor c = super.query(uri, projection, selection, selectionArgs, sortOrder);
    final String query = selectionArgs[0];

    try {

      if (c != null
          && query != null
          && query.trim()
          .length() > 0) {

        getSearchSocket().send(buildJson(query));

        MatrixCursor matrixCursor = getBlockingQueue().poll(5, TimeUnit.SECONDS);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
          matrixCursor.newRow()
              .add(c.getString(
                  c.getColumnIndex(SearchWebSocketListener.CursorFields.REMOTE_SUGESTION)))
              .add(c.getString(c.getColumnIndex(SearchWebSocketListener.CursorFields.LOCAL_QUERY)))
              .add(c.getString(c.getColumnIndex(SearchWebSocketListener.CursorFields.ID)));
        }
        return matrixCursor;
      }
    } catch (Exception e) {
      crashReport.log(e);
    } finally {
      if (c != null && !c.isClosed()) {
        c.close();
      }
    }

    return null;
  }

  public abstract BlockingQueue<MatrixCursor> getBlockingQueue();

  public abstract String getSearchProvider(Resources resources);

  public abstract SearchWebSocket getSearchSocket();

  private String buildJson(String query) {
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
