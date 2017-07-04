package cm.aptoide.pt.spotandshareandroid;

import android.content.Context;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.HotspotManager;
import java.util.List;

/**
 * Created by neuro on 19-06-2017.
 */

public class SpotAndShare {

  private final String PASSWORD_APTOIDE = "passwordAptoide";

  private final HotspotManager hotspotManager;
  private final String DUMMY_HOTSPOT = "DummyHotspot";
  private final String DUMMY_UUID = "dummy_uuid";

  public SpotAndShare(Context context) {
    hotspotManager = new HotspotManager(context, (WifiManager) context.getApplicationContext()
        .getSystemService(Context.WIFI_SERVICE));
  }

  public void createGroup(OnSuccess onSuccess, OnError onError, AcceptReception acceptReception) {

    isGroupCreated(created -> {
      if (!created) {
        if (hotspotManager.enablePrivateHotspot(DUMMY_HOTSPOT, PASSWORD_APTOIDE)) {
          onSuccess.onSuccess(DUMMY_UUID);
        }
      } else {
        //// TODO: 27-06-2017 filipe join the group that is already created
      }
    });
  }

  public void isGroupCreated(GroupCreated groupCreated) {
    // TODO: 19-06-2017 neuro
    groupCreated.isCreated(false);//added this in order to test the hotspot creation
  }

  public void joinGroup(OnSuccess onSuccess, OnError onError, AcceptReception acceptReception) {
    hotspotManager.joinHotspot(DUMMY_HOTSPOT, enabled -> {
      if (enabled) {
        // TODO: 19-06-2017 neuro
        onSuccess.onSuccess(DUMMY_UUID);
      } else {
        System.out.println("Failed to connect to DummyHotspot");
      }
    }, 10000);
  }

  public void leaveGroup(OnSuccess onSuccess, OnError onError) {
    // TODO: 19-06-2017 neuro
  }

  public void sendApps(List<AndroidAppInfo> appsList) {
    // TODO: 19-06-2017 neuro
  }

  public interface GroupCreated {
    void isCreated(boolean created);
  }

  public interface OnSuccess {
    void onSuccess(String uuid);
  }

  public interface OnError {
    void onError(Throwable throwable);
  }

  public interface AcceptReception {
    boolean accept(AndroidAppInfo androidAppInfo);
  }
}
