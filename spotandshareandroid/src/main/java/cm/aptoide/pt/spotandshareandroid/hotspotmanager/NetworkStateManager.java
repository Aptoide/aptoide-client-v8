package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.net.wifi.WifiManager;

/**
 * Created by filipe on 26-06-2017.
 */

public class NetworkStateManager {

  private WifiManager wifimanager;
  private boolean wifiEnabledOnStart;

  public NetworkStateManager(WifiManager wifimanager) {
    this.wifimanager = wifimanager;
  }

  public void saveActualNetworkState() {
    wifiEnabledOnStart = wifimanager.isWifiEnabled();
    //// TODO: 26-06-2017 filipe store on shared preferences
  }

  public void recoverNetworkState() {
    //// TODO: 26-06-2017 filipe get network state on shared preferences
    wifimanager.setWifiEnabled(wifiEnabledOnStart);
  }
}
