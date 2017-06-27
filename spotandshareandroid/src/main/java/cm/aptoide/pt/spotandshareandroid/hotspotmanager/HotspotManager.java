package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.content.Context;
import android.net.wifi.WifiManager;
import lombok.experimental.Delegate;

public class HotspotManager {

  static final int ERROR_UNKNOWN = 3;
  private static final String TAG = HotspotManager.class.getSimpleName();
  @Delegate private final JoinHotspotManager joinHotspotManager;
  @Delegate private final CreateHotspotManager createHotspotManager;
  private final NetworkStateManager networkStateManager;

  public HotspotManager(Context context, WifiManager wifimanager) {
    joinHotspotManager = new JoinHotspotManager(context, wifimanager);
    createHotspotManager = new CreateHotspotManager(wifimanager);
    networkStateManager = new NetworkStateManager(wifimanager);
  }
}