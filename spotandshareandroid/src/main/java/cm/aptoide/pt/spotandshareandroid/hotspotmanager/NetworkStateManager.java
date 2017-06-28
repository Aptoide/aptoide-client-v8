package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.net.wifi.WifiManager;

/**
 * Created by filipe on 26-06-2017.
 */

public class NetworkStateManager {

  private final WifiManager wifimanager;

  private boolean wifiEnabledOnStart;

  public NetworkStateManager(WifiManager wifimanager) {
    this.wifimanager = wifimanager;
  }

  public void saveActualNetworkState() {
    wifiEnabledOnStart = wifimanager.isWifiEnabled();
  }

  public void restoreNetworkState() {
    wifimanager.setWifiEnabled(wifiEnabledOnStart);
  }
}
