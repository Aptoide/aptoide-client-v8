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
import cm.aptoide.pt.spotandshareandroid.util.service.ServiceProvider;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 19-06-2017.
 */

class SpotAndShareV2 {

  private static final String APTOIDE_HOTSPOT = "AptoideHotspot";
  private final String PASSWORD_APTOIDE = "passwordAptoide";
  private final HotspotManager hotspotManager;
  private final TransferManager transferManager;
  //private final String DUMMY_UUID = "dummy_uuid";
  private final Context applicationContext;
  private final int TIMEOUT = 15 * 1000;
  private final Friend friend;
  private boolean enabled;
  private boolean isHotspot;
  private final ServiceProvider serviceProvider;

  SpotAndShareV2(Context context, Friend friend) {
    serviceProvider = new ServiceProvider(context);
    hotspotManager = new HotspotManager(context, (WifiManager) context.getApplicationContext()
        .getSystemService(Context.WIFI_SERVICE), serviceProvider.getWifiManager());
    applicationContext = context.getApplicationContext();
    transferManager = new TransferManager(new SpotAndShareMessageServer(55555, friend));
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

  public Observable<Collection<Friend>> observeFriends() {
    return transferManager.observeFriends();
  }

  public Observable<Integer> observeAmountOfFriends() {
    return transferManager.observeAmountOfFriends();
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
          startSpotAndShareMessageClient(serviceProvider.getConnectivityManager(), onError);
          onSuccess.call(createSpotAndShareSender());
        }, throwable -> {
          enabled = false;
          onError.onError(throwable);
        });
      }
    });
  }

  private void startSpotAndShareMessageClient(ConnectivityManager connectivityManager,
      OnError onError) {
    transferManager.startClient(applicationContext, connectivityManager, onError::onError);
  }

  private void startSpotAndShareMessageServer(OnError onError) {
    // TODO: 10-07-2017 neuro
    transferManager.startServer(createHostsChangedCallback(onError), onError::onError);
    startSpotAndShareMessageClient(serviceProvider.getConnectivityManager(), onError);
  }

  void receive(Action1<SpotAndShareSender> onSuccess, OnError onError) {
    // TODO: 10-07-2017 neuro duplicated with isGroupCreated()
    hotspotManager.saveActualNetworkState()
        .andThen(hotspotManager.isWifiEnabled()
            .flatMap(wifiEnabled -> hotspotManager.setWifiEnabled(true)))
        .flatMapCompletable(aBoolean -> hotspotManager.joinHotspot(APTOIDE_HOTSPOT, enabled1 -> {
          if (enabled1) {
            enabled = true;
            startSpotAndShareMessageClient(serviceProvider.getConnectivityManager(), onError);
            onSuccess.call(createSpotAndShareSender());
          } else {
            onError.onError(new Throwable("Failed to join hotspot"));
          }
        }, TIMEOUT))
        .doOnError(error -> {
          if (error instanceof TimeoutException) {
            onError.onError(new Throwable("Failed to join hotspot"));// or retry
          }
        })
        .subscribe(() -> {
        }, Throwable::printStackTrace);
  }

  public Completable enableOpenHotspot(Action1<Void> onSuccess, String Ssid) {
    return hotspotManager.enableOpenHotspot(Ssid)
        .doOnCompleted(() -> {
          isHotspot = true;
          enabled = true;
          onSuccess.call(null);
        });
  }

  private HostsChangedCallback createHostsChangedCallback(OnError onError) {
    return hostList -> {
      // TODO: 10-07-2017 neuro
      System.out.println("Filipe: " + hostList);
    };
  }

  private Completable joinHotspot(Action0 onSuccess, OnError onError) {
    return hotspotManager.joinHotspot(APTOIDE_HOTSPOT, enabled -> {
      if (enabled) {
        onSuccess.call();
      } else {
        onError.onError(new Throwable("Failed to join hotspot"));
      }
    }, 20000);
  }

  private Completable enableHotspot() {
    return hotspotManager.enablePrivateHotspot(APTOIDE_HOTSPOT, PASSWORD_APTOIDE)
        .doOnCompleted(() -> isHotspot = true);
  }

  private Single<Boolean> isGroupCreated() {
    return hotspotManager.saveActualNetworkState()
        .andThen(hotspotManager.isWifiEnabled()
            .flatMap(wifiEnabled -> hotspotManager.setWifiEnabled(true)
                .flatMap(wifiEnabled1 -> hotspotManager.scan())
                .map(hotspots -> !hotspots.isEmpty())));
  }

  public void exit(Action1<? super Throwable> onError) {
    if (isHotspot) {
      Completable.fromAction(transferManager::shutdown)
          .andThen(hotspotManager.resetHotspot()
              .andThen(hotspotManager.restoreNetworkState()
                  .toCompletable()))
          .subscribeOn(Schedulers.io())
          .subscribe(() -> {
          }, onError);
      isHotspot = false;
    } else {
      Completable.fromAction(transferManager::shutdown)
          .andThen(hotspotManager.restoreNetworkState()
              .toCompletable())
          .andThen(hotspotManager.forgetSpotAndShareNetworks())
          .subscribeOn(Schedulers.io())
          .subscribe(() -> {
          }, onError);
    }
    transferManager.clearTransfers();
  }

  public void sendApp(AndroidAppInfo androidAppInfo) {
    transferManager.sendApp(androidAppInfo);
  }

  public void sendApps(List<AndroidAppInfo> appsList) {
    transferManager.sendApps(appsList);
  }

  public Observable<List<Transfer>> observeTransfers() {
    return transferManager.observeTransfers();
  }

  public boolean canSend() {
    if (!isHotspot) {
      return true;
    }
    return observeFriends().map(friends -> friends.size() > 0)
        .toBlocking()
        .first();
  }

  public interface OnError {
    void onError(Throwable throwable);
  }
}
