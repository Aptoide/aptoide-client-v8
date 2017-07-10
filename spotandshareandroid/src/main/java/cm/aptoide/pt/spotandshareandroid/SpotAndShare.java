package cm.aptoide.pt.spotandshareandroid;

import android.content.Context;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.Accepter;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.AndroidAppInfoAccepter;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.HotspotManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by neuro on 19-06-2017.
 */

public class SpotAndShare {

  public static final String DUMMY_HOTSPOT = "DummyHotspot";
  private final String PASSWORD_APTOIDE = "passwordAptoide";
  private final HotspotManager hotspotManager;
  private final String DUMMY_UUID = "dummy_uuid";
  private final SpotAndShareV2 spotAndShareV2;
  private Map<AndroidAppInfo, Accepter<AndroidAppInfo>> androidAppInfoAccepterMap = new HashMap<>();

  public SpotAndShare(Context context) {
    hotspotManager = new HotspotManager(context, (WifiManager) context.getApplicationContext()
        .getSystemService(Context.WIFI_SERVICE));
    spotAndShareV2 = new SpotAndShareV2(context);
  }

  public void createGroup(Action1<SpotAndShareSender> onSuccess, OnError onError,
      AndroidAppInfoAccepter androidAppInfoAccepter) {
    spotAndShareV2.send(onSuccess, onError::onError, androidAppInfoAccepter1 -> {
      androidAppInfoAccepter.call(androidAppInfoAccepter1);
      androidAppInfoAccepterMap.put(androidAppInfoAccepter1.getMeta(), androidAppInfoAccepter1);
    });
  }

  public void isGroupCreated(GroupCreated groupCreated) {
    // TODO: 19-06-2017 neuro
    groupCreated.isCreated(false);//added this in order to test the hotspot creation
  }

  public void joinGroup(Action1<SpotAndShareSender> onSuccess, OnError onError,
      AndroidAppInfoAccepter androidAppInfoAccepter) {
    spotAndShareV2.receive(onSuccess, onError::onError, androidAppInfoAccepter);
  }

  public void leaveGroup(Action0 onSuccess, Action1<? super Throwable> onError) {
    spotAndShareV2.exit(onSuccess, onError);
  }

  public void sendApps(List<AndroidAppInfo> appsList) {
    // TODO: 19-06-2017 neuro
    spotAndShareV2.sendApps(appsList);
  }

  public boolean canSend() {
    return true;
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
