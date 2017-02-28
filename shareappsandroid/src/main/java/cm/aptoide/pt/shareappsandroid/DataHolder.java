package cm.aptoide.pt.shareappsandroid;

import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import cm.aptoide.pt.shareapps.socket.entities.Host;
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
  private static final DataHolder holder = new DataHolder();
  private AsyncTask<String, String, String> dlfile;
  private TextView textView;
  private Map<String, Long> updateStartingTimestamp = new HashMap<>();
  private boolean isServiceRunning = false;
  private boolean isHotspot = false;
  private WifiConfiguration wcOnJoin;
  private List<Host> connectedClients;

  public static DataHolder getInstance() {
    return holder;
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

  //  public void addConnectedClient(String arg) {
  //    if (arg != null && connectedClients != null) {
  //      this.connectedClients.add(arg);
  //    }
  //  }

  //  public void removeConnectedClient(String arg) {
  //    if (connectedClients != null && arg != null) {
  //
  //      System.out.println("Size of the list before removing : " + connectedClients.size());
  //      if (connectedClients.contains(arg)) {
  //        List<String> listaTmp = new ArrayList<>();
  //        for (int i = 0; i < connectedClients.size(); i++) {
  //          if (connectedClients.get(i).equals(arg)) {
  //            listaTmp.add(connectedClients.get(i));
  //          }
  //        }
  //
  //        if (listaTmp != null) {
  //          connectedClients.removeAll(listaTmp);
  //          System.out.println("removing arg, now size is : " + connectedClients.size());
  //        }
  //      }
  //    }
  //  }

  public void createConnectedClientsList() {
    this.connectedClients = new ArrayList<>();
  }
}
