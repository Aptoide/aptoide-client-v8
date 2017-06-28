package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.net.wifi.WifiManager;

/**
 * Created by filipe on 26-06-2017.
 */

public class NetworkStateManager {

  private final WifiManager wifimanager;
  private final Persister<String, Boolean> booleanPersister;
  private final String key;

  public NetworkStateManager(WifiManager wifimanager, Persister<String, Boolean> booleanPersister) {
    this.wifimanager = wifimanager;
    this.booleanPersister = booleanPersister;

    key = getClass().getSimpleName() + "WIFI_STATE";
  }

  public void saveActualNetworkState() {
    booleanPersister.save(key, wifimanager.isWifiEnabled());
  }

  public void restoreNetworkState() {
    Boolean wifiEnabledOnStart = booleanPersister.load(key);
    wifimanager.setWifiEnabled(wifiEnabledOnStart);
  }
}
