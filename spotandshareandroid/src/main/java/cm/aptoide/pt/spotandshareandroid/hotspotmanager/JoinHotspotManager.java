package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.receiver.networkstate.NetworkState;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.receiver.networkstate.NetworkStateReceiverHelper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Completable;

/**
 * Created by neuro on 22-06-2017.
 */

public class JoinHotspotManager {

  private final Context context;
  private final WifiManager wifimanager;

  JoinHotspotManager(Context context, WifiManager wifimanager) {
    this.context = context;
    this.wifimanager = wifimanager;
  }

  public Completable joinHotspot(String ssid, WifiStateListener wifiStateListener, long timeout) {
    return joinHotspot(ssid).andThen(
        Completable.fromAction(() -> wifiStateListener.onStateChanged(true)))
        .timeout(timeout, TimeUnit.MILLISECONDS);
  }

  private Completable joinHotspot(String ssid) {
    return joinHotspot(ssid, wifimanager).andThen(
        new NetworkStateReceiverHelper(context).newNetworkStateReceiver()
            .filter(networkState -> networkState.getState() == NetworkState.State.CONNECTED)
            .filter(networkState -> networkState.getSsid()
                .equals("\"" + ssid + "\""))
            .first()
            .toCompletable());
  }

  private Completable joinHotspot(String ssid, WifiManager wifiManager) {
    return Completable.fromCallable(() -> {
      WifiConfiguration conf = new WifiConfiguration();

      conf.SSID = "\"" + ssid + "\"";
      conf.preSharedKey = "\"passwordAptoide\"";
      conf.hiddenSSID = true;
      conf.status = WifiConfiguration.Status.ENABLED;
      conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

      wifiManager.addNetwork(conf);

      List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
      for (WifiConfiguration i : list) {
        if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
          wifiManager.disconnect();
          wifiManager.enableNetwork(i.networkId, true);
          wifiManager.reconnect();

          return Completable.complete();
        }
      }

      throw new Exception("Error Joining Hotspot");
    });
  }

  public interface WifiStateListener {
    void onStateChanged(boolean enabled);
  }
}
