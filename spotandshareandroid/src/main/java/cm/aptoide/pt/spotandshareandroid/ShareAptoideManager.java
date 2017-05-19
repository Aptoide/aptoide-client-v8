package cm.aptoide.pt.spotandshareandroid;

import android.os.AsyncTask;

/**
 * Created by filipe on 17-05-2017.
 */

public class ShareAptoideManager {

  public static final String SSID = "Aptoide_Share";
  private HotspotManager hotspotManager;
  private EnabledHotspotListener listener;

  public ShareAptoideManager(HotspotManager hotspotManager) {
    this.hotspotManager = hotspotManager;
  }

  public void enableHotspot(EnabledHotspotListener listener) {
    this.listener = listener;
    new EnableHotspotTask().execute();
  }

  public void stop() {
    this.hotspotManager.stop();
    this.listener = null;
  }

  public interface EnabledHotspotListener {
    void onResult(boolean result);
  }

  private class EnableHotspotTask extends AsyncTask<Void, Void, Integer> {

    @Override protected Integer doInBackground(Void... params) {
      return hotspotManager.enableOpenHotspot(SSID);
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
