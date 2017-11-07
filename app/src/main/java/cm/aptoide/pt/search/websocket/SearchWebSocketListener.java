package cm.aptoide.pt.search.websocket;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import cm.aptoide.pt.crashreports.CrashReport;
import java.util.concurrent.BlockingQueue;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by franciscocalado on 11/7/17.
 */

public class SearchWebSocketListener extends WebSocketListener {

  private static final String TAG = SearchWebSocketListener.class.getName();
  private final BlockingQueue<Cursor> blockingQueue;
  private String[] matrix_columns = new String[] {
      SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_QUERY, "_id"
  };

  public SearchWebSocketListener(BlockingQueue<Cursor> blockingQueue) {
    this.blockingQueue = blockingQueue;
  }

  @Override public void onOpen(WebSocket webSocket, Response response) {
  }

  @Override public void onMessage(WebSocket webSocket, String responseMessage) {
    super.onMessage(webSocket, responseMessage);
    try {
      JSONArray jsonArray = new JSONArray(responseMessage);
      MatrixCursor matrixCursor = new MatrixCursor(matrix_columns);
      for (int i = 0; i < jsonArray.length(); i++) {
        String suggestion = jsonArray.get(i)
            .toString();
        addRow(matrixCursor, suggestion, i);
      }
      blockingQueue.add(matrixCursor);
    } catch (JSONException e) {
      CrashReport.getInstance()
          .log(e);
    }
  }

  @Override public void onClosing(WebSocket webSocket, int code, String reason) {
    Log.d(TAG, reason);
    webSocket.close(1000, null);
  }

  @Override public void onClosed(WebSocket webSocket, int code, String reason) {
    Log.d(TAG, reason);
    super.onClosed(webSocket, code, reason);
    //TODO: Log no messages passing because socket is closed
  }

  @Override public void onFailure(WebSocket webSocket, Throwable t, Response response) {
    Log.d(TAG, "Error was:", t);
    super.onFailure(webSocket, t, response);
  }

  private void addRow(MatrixCursor matrixCursor, String string, int i) {
    matrixCursor.newRow()
        .add(string)
        .add(string)
        .add(i);
  }
}
