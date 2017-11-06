package cm.aptoide.pt.spotandshare.connection;

import android.os.AsyncTask;
import cm.aptoide.pt.spotandshare.SimpleListener;

/**
 * Created by filipegoncalves on 31-01-2017.
 */

public class DeactivateHotspotTask extends AsyncTask<Void, Void, Void> {

  private final ConnectionManager connectionManager;
  private SimpleListener listener;

  public DeactivateHotspotTask(ConnectionManager connectionManager) {

    this.connectionManager = connectionManager;
  }

  public void setListener(SimpleListener listener) {
    this.listener = listener;
  }

  @Override protected Void doInBackground(Void... params) {
    if (!isCancelled()) {
      connectionManager.resetHotspot(false);
    }

    return null;
  }

  @Override protected void onPreExecute() {
    super.onPreExecute();
  }

  @Override protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    if (listener != null) {
      listener.onEvent();
    }
  }

  @Override protected void onCancelled() {
    super.onCancelled();
    listener = null;
  }
}