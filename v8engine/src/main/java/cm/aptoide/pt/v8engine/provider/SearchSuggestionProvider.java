/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.v8engine.provider;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.websocket.SearchAppsWebSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Refactored by pedroribeiro in 17/01/2017
 */
public class SearchSuggestionProvider extends SearchRecentSuggestionsProviderWrapper {

  public String getSearchProvider() {
    return "cm.aptoide.pt.v8engine.provider.SearchSuggestionProvider";
  }

  @Override public boolean onCreate() {

    setupSuggestions(getSearchProvider(), DATABASE_MODE_QUERIES);
    return super.onCreate();
  }

  @Override public Cursor query(final Uri uri, String[] projection, String selection,
      final String[] selectionArgs, String sortOrder) {
    Logger.d("TAG", "search-query: " + selectionArgs[0]);

    Cursor c = super.query(uri, projection, selection, selectionArgs, sortOrder);

    if (c != null) {
      BlockingQueue<MatrixCursor> arrayBlockingQueue = new ArrayBlockingQueue<MatrixCursor>(1);
      SearchAppsWebSocket.setBlockingQueue(arrayBlockingQueue);

      MatrixCursor matrixCursor = null;

      SearchAppsWebSocket searchAppsWebSocket = new SearchAppsWebSocket();
      searchAppsWebSocket.send(buildJson(selectionArgs[0]));
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
        Logger.printException(e);
        CrashReports.logException(e);
      } finally {
        c.close();
      }
      return matrixCursor;
    } else {
      return null;
    }
  }
}
