/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.v8engine.websocket;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created with IntelliJ IDEA. User: brutus Date: 30-09-2013 Time: 11:33 To change this template
 * use
 * File | Settings |
 * File Templates.
 */
public class WebSocketSingleton {

  public static final String TAG = "WebSocketSingleton";

  private static WebSocketClient web_socket_client;
  String[] matrix_columns = new String[] {
      SearchManager.SUGGEST_COLUMN_ICON_1, SearchManager.SUGGEST_COLUMN_TEXT_1,
      SearchManager.SUGGEST_COLUMN_QUERY, "_id"
  };
  ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
  ScheduledFuture<?> future;
  private String query;
  private String buffer;
  private BlockingQueue<Cursor> blockingQueue;
  private WebSocketClient.Listener listener = new WebSocketClient.Listener() {
    @Override public void onConnect() {
      Logger.d(TAG, "On Connect");
    }

    @Override public void onMessage(String message) {

      try {
        JSONArray array = new JSONArray(message);
        MatrixCursor mCursor = new MatrixCursor(matrix_columns);
        for (int i = 0; i < array.length(); i++) {
          String suggestion = array.get(i).toString();
          Logger.d(TAG, "Suggestion " + suggestion);
          addRow(mCursor, suggestion, i);
        }

        if (array.length() == 0) {
          buffer = query;
        }

        blockingQueue.add(mCursor);
      } catch (JSONException e) {
        CrashReport.getInstance().log(e);
      }
    }

    @Override public void onMessage(byte[] data) {
      Logger.d(TAG, Arrays.toString(data));
    }

    @Override public void onDisconnect(int code, String reason) {
      Logger.d(TAG, reason);
    }

    @Override public void onError(Exception error) {
      CrashReport.getInstance().log(error);
    }
  };

  private WebSocketSingleton() {
  }

  public static WebSocketSingleton getInstance() {
    return WebSocketHolder.INSTANCE;
  }

  public void send(final String query) {
    this.query = query;
    // Fix nullPointer
    if (web_socket_client != null && web_socket_client.isConnected() && query.length() > 2 && (
        buffer == null
            || !query.startsWith(buffer))) {

      Runnable runnable = () -> {
        JsonFactory f = new JsonFactory();

        StringWriter writer = new StringWriter();
        try {
          JsonGenerator g = f.createJsonGenerator(writer);
          g.writeStartObject();
          g.writeStringField("query", query);
          g.writeEndObject();
          g.close();
        } catch (IOException e) {
          CrashReport.getInstance().log(e);
        }
        //"{\"query\":\"" + query + "\"}"

        web_socket_client.send(writer.toString());

        Logger.d(TAG, "Sending " + writer.toString());
      };

      if (future != null) {
        future.cancel(false);
      }

      future = scheduledExecutorService.schedule(runnable, 500L, TimeUnit.MILLISECONDS);
    } else {
      MatrixCursor mCursor = null;
      blockingQueue.add(mCursor);
    }
  }

  public void disconnect() {

    Logger.d(TAG, "onDisconnect");

    if (web_socket_client != null) {
      web_socket_client.disconnect();
      web_socket_client = null;
    }
  }

  public void connect() {

    if (web_socket_client == null) {
      web_socket_client =
          new WebSocketClient(URI.create("ws://buzz.webservices.aptoide.com:9000"), listener, null);
      web_socket_client.connect();
    }
    Logger.d(TAG, "OnConnecting");
  }

  private void addRow(MatrixCursor matrix_cursor, String string, int i) {
    matrix_cursor.newRow().add(null).add(string).add(string).add(i);
  }

  public void setBlockingQueue(BlockingQueue a) {
    this.blockingQueue = a;
  }

  private static class WebSocketHolder {

    public static final WebSocketSingleton INSTANCE = new WebSocketSingleton();
  }
}
