package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.content.Context;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshareandroid.util.TaskQueue;
import lombok.experimental.Delegate;

public class HotspotManager {

  static final int ERROR_UNKNOWN = 3;

  private static final String TAG = HotspotManager.class.getSimpleName();

  @Delegate private final JoinHotspotManager joinHotspotManager;
  @Delegate private final CreateHotspotManager createHotspotManager;
  private final NetworkStateManager networkStateManager;

  private final TaskQueue taskQueue;

  public HotspotManager(Context context, WifiManager wifimanager) {
    taskQueue = new TaskQueue();

    this.joinHotspotManager = new JoinHotspotManager(context, wifimanager);
    this.createHotspotManager = new CreateHotspotManager(wifimanager, taskQueue);
    this.networkStateManager = new NetworkStateManager(wifimanager);
  }

  public void shutdown() {
    taskQueue.shutdown();
  }

  public void shutdownNow() {
    taskQueue.shutdownNow();
  }
}