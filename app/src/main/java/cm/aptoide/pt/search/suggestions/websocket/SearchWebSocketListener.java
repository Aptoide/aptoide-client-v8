package cm.aptoide.pt.search.suggestions.websocket;

import android.app.SearchManager;
import android.database.MatrixCursor;
import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.BlockingQueue;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by franciscocalado on 11/7/17.
 */
@Deprecated
public class SearchWebSocketListener extends WebSocketListener {

  private static final String TAG = SearchWebSocketListener.class.getName();
  private final BlockingQueue<MatrixCursor> blockingQueue;
  private final CrashReport crashReport;

  SearchWebSocketListener(BlockingQueue<MatrixCursor> blockingQueue, CrashReport crashReport) {
    this.blockingQueue = blockingQueue;
    this.crashReport = crashReport;
  }

  @Override public void onOpen(WebSocket webSocket, Response response) {
    Logger.d(TAG, "onOpen :: " + response);
  }

  @Override public void onMessage(WebSocket webSocket, String responseMessage) {
    Logger.v(TAG, "onMessage :: " + responseMessage);
    try {
      final ObjectMapper objectMapper = new ObjectMapper();
      String[] suggestions = objectMapper.readValue(responseMessage, String[].class);
      if (suggestions != null && suggestions.length > 0 && blockingQueue.isEmpty()) {
        blockingQueue.add(getMatrixCursorWithSuggestions(suggestions));
      }
    } catch (Exception e) {
      crashReport.log(e);
    }
  }

  @Override public void onClosing(WebSocket webSocket, int code, String reason) {
    Logger.v(TAG, "onClosing :: " + reason);
  }

  @Override public void onClosed(WebSocket webSocket, int code, String reason) {
    Logger.d(TAG, "onClosed :: " + reason);
  }

  @Override public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
    crashReport.log(throwable);
  }

  @NonNull private MatrixCursor getMatrixCursorWithSuggestions(String[] suggestions) {
    MatrixCursor matrixCursor = new MatrixCursor(new String[] {
        CursorFields.REMOTE_SUGESTION, CursorFields.LOCAL_QUERY, CursorFields.ID
    });
    int i = 0;
    for (String suggestion : suggestions) {
      matrixCursor.newRow()
          .add(suggestion)
          .add(suggestion)
          .add(Integer.toString(i++));
    }
    return matrixCursor;
  }

  public static final class CursorFields {
    public static final String REMOTE_SUGESTION = SearchManager.SUGGEST_COLUMN_QUERY;
    public static final String LOCAL_QUERY = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String ID = "_id";
  }
}
