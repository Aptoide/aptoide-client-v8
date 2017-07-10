package cm.aptoide.pt.spotandshareandroid;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import cm.aptoide.pt.spotandshare.socket.interfaces.HostsChangedCallback;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.AndroidAppInfoAccepter;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.HotspotManager;
import rx.Completable;
import rx.Single;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by neuro on 19-06-2017.
 */

class SpotAndShareV2 {

  private static final String DUMMY_HOTSPOT = "DummyHotspot";

  private final String PASSWORD_APTOIDE = "passwordAptoide";
  private final HotspotManager hotspotManager;
  private final SpotAndShareMessageServer spotAndShareMessageServer;
  private final String DUMMY_UUID = "dummy_uuid";
  private boolean enabled;

  SpotAndShareV2(Context context) {
    hotspotManager = new HotspotManager(context, (WifiManager) context.getApplicationContext()
        .getSystemService(Context.WIFI_SERVICE));
    spotAndShareMessageServer = new SpotAndShareMessageServer(55555);
  }

  SpotAndShareSender spotAndShareSender = createSpotAndShareSender();

  private SpotAndShareSender createSpotAndShareSender() {
    return androidAppInfo -> {
      if (enabled) {

      } else {
        throw new IllegalStateException("Spot and Share not connected!");
      }
    };
  }

  void send(Action1<SpotAndShareSender> onSuccess, OnError onError,
      AndroidAppInfoAccepter androidAppInfoAccepter) {

    isGroupCreated().flatMap(created -> {
      if (!created) {
        return enableHotspot(null, null).doOnCompleted(() -> {
          enabled = true;
          // TODO: 10-07-2017 neuro
          spotAndShareMessageServer.startServer(createHostsChangedCallback(onError));
          spotAndShareMessageServer.startClient(null, null, null, null, null,
              androidAppInfoAccepter);
          onSuccess.call(createSpotAndShareSender());
        })
            .toSingle(() -> 0);
      } else {
        return joinHotspot(() -> {
          enabled = true;
          // TODO: 10-07-2017 neuro
          spotAndShareMessageServer.startClient(null, null, null, null, null,
              androidAppInfoAccepter);
          onSuccess.call(createSpotAndShareSender());
        }, throwable -> {
          enabled = false;
          onError.onError(throwable);
        }).toSingle(() -> 0);
      }
    })
        .toCompletable()
        .subscribe(() -> {
        }, Throwable::printStackTrace);
  }

  private HostsChangedCallback createHostsChangedCallback(OnError onError) {
    return hostList -> {
      // TODO: 10-07-2017 neuro
    };
  }

  @NonNull private Completable joinHotspot(Action0 onSuccess, OnError onError) {
    return hotspotManager.joinHotspot(DUMMY_HOTSPOT, enabled -> {
      if (enabled) {
        onSuccess.call();
      } else {
        onError.onError(new Throwable("Failed to join hotspot"));
      }
    }, 20000);
  }

  @NonNull private Completable enableHotspot(Action0 onSuccess, OnError onError) {
    return hotspotManager.enablePrivateHotspot(DUMMY_HOTSPOT, PASSWORD_APTOIDE);
  }

  private Single<Boolean> isGroupCreated() {
    return hotspotManager.saveActualNetworkState()
        .toSingle(() -> 0)
        .flatMap(__ -> hotspotManager.isWifiEnabled()
            .flatMap(wifiEnabled -> hotspotManager.setWifiEnabled(true)
                .flatMap(wifiEnabled1 -> hotspotManager.scan())
                .map(hotspots -> !hotspots.isEmpty())));
  }

  public interface OnError {
    void onError(Throwable throwable);
  }
}
