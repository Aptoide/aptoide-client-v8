package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.net.wifi.WifiManager;
import rx.Single;

/**
 * Created by filipe on 26-06-2017.
 */

class NetworkStateManager {

  private final WifiManager wifimanager;

  private boolean wifiEnabledOnStart;

  NetworkStateManager(WifiManager wifimanager) {
    this.wifimanager = wifimanager;
  }

  public void saveActualNetworkState() {
    wifiEnabledOnStart = wifimanager.isWifiEnabled();
  }

  public void restoreNetworkState() {
    wifimanager.setWifiEnabled(wifiEnabledOnStart);
  }

  public Single<Boolean> isWifiEnabled() {
    return Single.just(wifimanager.isWifiEnabled());
  }

  public Single<Boolean> setWifiEnabled(boolean enabled) {
    return Single.just(wifimanager.setWifiEnabled(enabled));
  }
}
