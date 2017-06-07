package cm.aptoide.pt.v8engine.spotandshare.shareaptoide;

import android.os.AsyncTask;
import cm.aptoide.pt.v8engine.spotandshare.connection.ConnectionManager;
import cm.aptoide.pt.v8engine.spotandshare.connection.HotspotManager;

/**
 * Created by filipe on 17-05-2017.
 */

public class ShareAptoideManager {

  private HotspotManager hotspotManager;
  private ConnectionManager connectionManager;
  private String ssid;
  private EnabledHotspotListener listener;

  public ShareAptoideManager(HotspotManager hotspotManager, ConnectionManager connectionManager,
      String ssid) {
    this.hotspotManager = hotspotManager;
    this.connectionManager = connectionManager;
    this.ssid = ssid;
  }

  public void enableHotspot(EnabledHotspotListener listener) {
    this.listener = listener;
    new EnableHotspotTask().execute();
  }

  public void stop() {
    this.hotspotManager.resetHotspot(false);
    this.connectionManager.recoverNetworkState();
    this.hotspotManager.stop();
    this.connectionManager.stop();
    this.listener = null;
  }

  public interface EnabledHotspotListener {
    void onResult(boolean result);
  }

  private class EnableHotspotTask extends AsyncTask<Void, Void, Integer> {

    @Override protected Integer doInBackground(Void... params) {
      return hotspotManager.enableOpenHotspot(ssid);
    }

    @Override protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override protected void onPostExecute(Integer integer) {
      super.onPostExecute(integer);
      if (listener != null) {
        if (integer == ConnectionManager.SUCCESS_HOTSPOT_CREATION) {
          listener.onResult(true);
        } else {
          listener.onResult(false);
        }
      }
    }

    @Override protected void onCancelled() {
      super.onCancelled();
      if (listener != null) {
        listener = null;
      }
    }
  }
}
