package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.util.List;
import rx.Completable;
import rx.Single;

/**
 * Created by filipe on 26-06-2017.
 */

class NetworkStateManager {

  private final String TAG = getClass().getSimpleName();

  private final WifiManager wifimanager;

  private boolean wifiEnabledOnStart;

  NetworkStateManager(WifiManager wifimanager) {
    this.wifimanager = wifimanager;
  }

  public Completable saveActualNetworkState() {
    return Completable.fromAction(() -> wifiEnabledOnStart = wifimanager.isWifiEnabled());
  }

  public Single<Boolean> restoreNetworkState() {
    return Single.fromCallable(() -> wifimanager.disconnect())
        .doOnSuccess(aBoolean -> setWifiEnabled(wifiEnabledOnStart));
  }

  public Single<Boolean> isWifiEnabled() {
    return Single.fromCallable(wifimanager::isWifiEnabled);
  }

  public Single<Boolean> setWifiEnabled(boolean enabled) {
    return Single.fromCallable(() -> wifimanager.setWifiEnabled(enabled));
  }

  public Completable forgetSpotAndShareNetworks() {
    return Completable.fromAction(() -> cleanNetworks());
  }

  private void cleanNetworks() {
    List<WifiConfiguration> list = wifimanager.getConfiguredNetworks();
    if (list != null) {
      for (WifiConfiguration i : list) {
        if (i.SSID.contains("AptoideHotspot")) {
          boolean remove = wifimanager.removeNetwork(i.networkId);
          Log.i(TAG, "Removed network " + i.SSID + " :" + remove);
          if (!remove) {
            wifimanager.disableNetwork(i.networkId);
          }
        }
      }
    }
  }
}
