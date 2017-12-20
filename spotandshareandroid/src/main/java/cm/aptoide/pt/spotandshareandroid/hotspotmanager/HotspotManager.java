package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.content.Context;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.scanner.Hotspot;
import cm.aptoide.pt.spotandshareandroid.util.TaskQueue;
import java.util.List;
import rx.Completable;
import rx.Single;

public class HotspotManager {

  private static final String TAG = HotspotManager.class.getSimpleName();

  private final JoinHotspotManager joinHotspotManager;
  private final CreateHotspotManager createHotspotManager;
  private final NetworkStateManager networkStateManager;
  private final HotspotScanner hotspotScanner;

  private final TaskQueue taskQueue;

  public HotspotManager(Context context, WifiManager wifimanager, WifiManager wifiManager) {
    taskQueue = new TaskQueue();

    this.joinHotspotManager = new JoinHotspotManager(context, wifimanager);
    this.createHotspotManager = new CreateHotspotManager(wifimanager, taskQueue);
    this.networkStateManager = new NetworkStateManager(wifimanager);
    this.hotspotScanner =
        new SsidHotspotScanner(context, taskQueue, SpotAndShare.APTOIDE_HOTSPOT, wifiManager);
  }

  public void shutdown() {
    taskQueue.shutdown();
  }

  public void shutdownNow() {
    taskQueue.shutdownNow();
  }

  public Completable joinHotspot(String ssid,
      JoinHotspotManager.WifiStateListener wifiStateListener, long timeout) {
    return this.joinHotspotManager.joinHotspot(ssid, wifiStateListener, timeout);
  }

  public Completable enablePrivateHotspot(String SSID, String password) {
    return this.createHotspotManager.enablePrivateHotspot(SSID, password);
  }

  public Completable resetHotspot() {
    return this.createHotspotManager.resetHotspot();
  }

  public Completable enableOpenHotspot(String SSID) {
    return this.createHotspotManager.enableOpenHotspot(SSID);
  }

  public Single<Boolean> isConfigurationStored() {
    return this.createHotspotManager.isConfigurationStored();
  }

  public Single<Boolean> isWifiEnabled() {
    return this.networkStateManager.isWifiEnabled();
  }

  public Single<Boolean> setWifiEnabled(boolean enabled) {
    return this.networkStateManager.setWifiEnabled(enabled);
  }

  public Completable saveActualNetworkState() {
    return this.networkStateManager.saveActualNetworkState();
  }

  public Single<Boolean> restoreNetworkState() {
    return this.networkStateManager.restoreNetworkState();
  }

  public Completable forgetSpotAndShareNetworks() {
    return this.networkStateManager.forgetSpotAndShareNetworks();
  }

  public Single<List<Hotspot>> scan() {
    return this.hotspotScanner.scan();
  }
}