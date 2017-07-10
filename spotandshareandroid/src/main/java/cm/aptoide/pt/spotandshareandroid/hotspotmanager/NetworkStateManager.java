package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.net.wifi.WifiManager;
import rx.Completable;
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

  public Completable saveActualNetworkState() {
    return Completable.fromAction(() -> wifiEnabledOnStart = wifimanager.isWifiEnabled());
  }

  public Single<Boolean> restoreNetworkState() {
    return setWifiEnabled(wifiEnabledOnStart);
  }

  public Single<Boolean> isWifiEnabled() {
    return Single.fromCallable(wifimanager::isWifiEnabled);
  }

  public Single<Boolean> setWifiEnabled(boolean enabled) {
    return Single.fromCallable(() -> wifimanager.setWifiEnabled(enabled));
  }
}
