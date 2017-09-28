package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.content.Context;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareandroid.util.TaskQueue;
import lombok.experimental.Delegate;

public class HotspotManager {

  private static final String TAG = HotspotManager.class.getSimpleName();

  @Delegate private final JoinHotspotManager joinHotspotManager;
  @Delegate private final CreateHotspotManager createHotspotManager;
  @Delegate private final NetworkStateManager networkStateManager;
  @Delegate private final HotspotScanner hotspotScanner;

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
}