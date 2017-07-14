package cm.aptoide.pt.spotandshareandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshare.socket.interfaces.HostsChangedCallback;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.HotspotManager;
import cm.aptoide.pt.spotandshareandroid.transfermanager.Transfer;
import cm.aptoide.pt.spotandshareandroid.transfermanager.TransferManager;
import cm.aptoide.pt.spotandshareandroid.util.MessageServerConfiguration;
import cm.aptoide.pt.spotandshareandroid.util.service.ServiceProvider;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
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
  private final TransferManager transferManager;
  private final String DUMMY_UUID = "dummy_uuid";
  private final Context applicationContext;
  private final int TIMEOUT = 60 * 1000;
  private final Friend friend;
  private boolean enabled;
  private boolean isHotspot;
  private final ServiceProvider serviceProvider;

  SpotAndShareV2(Context context, Friend friend) {
    serviceProvider = new ServiceProvider(context);
    hotspotManager = new HotspotManager(context, (WifiManager) context.getApplicationContext()
        .getSystemService(Context.WIFI_SERVICE), serviceProvider.getWifiManager());
    spotAndShareMessageServer = new SpotAndShareMessageServer(55555);
    applicationContext = context.getApplicationContext();
    transferManager = new TransferManager();
    this.friend = friend;
  }

  private SpotAndShareSender createSpotAndShareSender() {
    return androidAppInfo -> {
      if (enabled) {

      } else {
        throw new IllegalStateException("Spot and Share not connected!");
      }
    };
  }

  Completable send(Action1<SpotAndShareSender> onSuccess, OnError onError) {

    return isGroupCreated().flatMapCompletable(created -> {
      if (!created) {
        return enableHotspot().doOnCompleted(() -> {
          enabled = true;
          startSpotAndShareMessageServer(onError);

          onSuccess.call(createSpotAndShareSender());
        });
      } else {
        return joinHotspot(() -> {
          enabled = true;
          startSpotAndShareMessageClient(serviceProvider.getConnectivityManager(), friend);
          onSuccess.call(createSpotAndShareSender());
        }, throwable -> {
          enabled = false;
          onError.onError(throwable);
        });
      }
    });
  }

  private void startSpotAndShareMessageClient(ConnectivityManager connectivityManager,
      Friend friend) {
    spotAndShareMessageServer.startClient(
        new MessageServerConfiguration(applicationContext, Throwable::printStackTrace,
            transferManager.getAndroidAppInfoAccepter(), connectivityManager), friend);
  }

  private void startSpotAndShareMessageServer(OnError onError) {
    // TODO: 10-07-2017 neuro
    spotAndShareMessageServer.startServer(createHostsChangedCallback(onError));
    startSpotAndShareMessageClient(serviceProvider.getConnectivityManager(), friend);
  }

  void receive(Action1<SpotAndShareSender> onSuccess, OnError onError) {
    // TODO: 10-07-2017 neuro duplicated with isGroupCreated()
    hotspotManager.saveActualNetworkState()
        .andThen(hotspotManager.isWifiEnabled()
            .flatMap(wifiEnabled -> hotspotManager.setWifiEnabled(true)))
        .flatMapCompletable(aBoolean -> hotspotManager.joinHotspot(DUMMY_HOTSPOT, enabled1 -> {
          if (enabled1) {
            enabled = true;
            startSpotAndShareMessageClient(serviceProvider.getConnectivityManager(), friend);
            onSuccess.call(createSpotAndShareSender());
          } else {
            onError.onError(new Throwable("Failed to join hotspot"));
          }
        }, TIMEOUT))
        .subscribe(() -> {
        }, Throwable::printStackTrace);
  }

  private HostsChangedCallback createHostsChangedCallback(OnError onError) {
    return hostList -> {
      // TODO: 10-07-2017 neuro
      System.out.println("Filipe: " + hostList);
    };
  }

  private Completable joinHotspot(Action0 onSuccess, OnError onError) {
    return hotspotManager.joinHotspot(DUMMY_HOTSPOT, enabled -> {
      if (enabled) {
        onSuccess.call();
      } else {
        onError.onError(new Throwable("Failed to join hotspot"));
      }
    }, 20000);
  }

  private Completable enableHotspot() {
    return hotspotManager.enablePrivateHotspot(DUMMY_HOTSPOT, PASSWORD_APTOIDE)
        .doOnCompleted(() -> isHotspot = true);
  }

  private Single<Boolean> isGroupCreated() {
    return hotspotManager.saveActualNetworkState()
        .andThen(hotspotManager.isWifiEnabled()
            .flatMap(wifiEnabled -> hotspotManager.setWifiEnabled(true)
                .flatMap(wifiEnabled1 -> hotspotManager.scan())
                .map(hotspots -> !hotspots.isEmpty())));
  }

  public void exit(Action0 onSuccess, Action1<? super Throwable> onError) {
    if (isHotspot) {
      Completable.fromAction(spotAndShareMessageServer::exit)
          .andThen(hotspotManager.resetHotspot()
              .andThen(hotspotManager.restoreNetworkState()
                  .toCompletable()))
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(onSuccess, onError);
    } else {
      Completable.fromAction(spotAndShareMessageServer::exit)
          .andThen(hotspotManager.restoreNetworkState()
              .toCompletable())
          .andThen(hotspotManager.forgetSpotAndShareNetworks())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(onSuccess, onError);
    }
  }

  public void sendApp(AndroidAppInfo androidAppInfo) {
    spotAndShareMessageServer.sendApp(androidAppInfo);
  }

  public void sendApps(List<AndroidAppInfo> appsList) {
    spotAndShareMessageServer.sendApps(appsList);
  }

  public Observable<List<Transfer>> observeTransfers() {
    return transferManager.observeTransfers();
  }

  public interface OnError {
    void onError(Throwable throwable);
  }
}
