package cm.aptoide.pt.spotandshareandroid;

import android.net.Network;
import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton without controller.
 * <p>Holds references to main stuff used in our webview app
 * </p>
 * Created by FilipeGonçalves on 10-08-2015.
 */
public class DataHolder {
  private static DataHolder holder;
  public Network network;
  private AsyncTask<String, String, String> dlfile;
  private TextView textView;
  private Map<String, Long> updateStartingTimestamp = new HashMap<>();
  private boolean isServiceRunning = false;
  private boolean isHotspot = false;
  private WifiConfiguration wcOnJoin;
  private List<Host> connectedClients;

  public static DataHolder getInstance() {
    if (holder == null) {
      holder = new DataHolder();
    }

    return holder;
  }

  public static void reset() {
    holder = null;
  }

  public Map<String, Long> getUpdateStartingTimestamp() {
    return updateStartingTimestamp;
  }

  public void putEntryToUpdateStartingTimestamp(String packageName, Long timestamp) {
    updateStartingTimestamp.put(packageName, timestamp);
  }

  public AsyncTask<String, String, String> getDlfile() {
    return this.dlfile;
  }

  public void setDlfile(AsyncTask<String, String, String> dlfile) {
    if (dlfile == null) {
      Log.d("DataHolder", "Download finish!");
    }
    this.dlfile = dlfile;
  }

  public boolean isServiceRunning() {
    return isServiceRunning;
  }

  public void setServiceRunning(boolean serviceRunning) {
    isServiceRunning = serviceRunning;
  }

  public boolean isHotspot() {
    return isHotspot;
  }

  public void setHotspot(boolean hotspot) {
    isHotspot = hotspot;
  }

  public WifiConfiguration getWcOnJoin() {
    return wcOnJoin;
  }

  public void setWcOnJoin(WifiConfiguration wcOnJoin) {
    this.wcOnJoin = wcOnJoin;
  }

  public List<Host> getConnectedClients() {
    return connectedClients;
  }

  public void setConnectedClients(List<Host> connectedClients) {
    this.connectedClients = connectedClients;
  }

  public void createConnectedClientsList() {
    this.connectedClients = new ArrayList<>();
  }
}
