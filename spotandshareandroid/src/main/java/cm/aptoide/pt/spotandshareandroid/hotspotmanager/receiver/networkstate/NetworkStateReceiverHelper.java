package cm.aptoide.pt.spotandshareandroid.hotspotmanager.receiver.networkstate;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import rx.Observable;

/**
 * Created by neuro on 05-07-2017.
 */

public class NetworkStateReceiverHelper {

  private final Context context;

  public NetworkStateReceiverHelper(Context context) {
    this.context = context.getApplicationContext();
  }

  public Observable<NetworkState> newNetworkStateReceiver() {

    BroadcastRegisterOnSubscribe broadcastRegisterOnSubscribe =
        new BroadcastRegisterOnSubscribe(context,
            new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION), null, null);

    return Observable.unsafeCreate(broadcastRegisterOnSubscribe)
        .flatMap(intent -> {

          String ssid = extractSsid(intent);
          NetworkInfo.DetailedState detailedState = extractState(intent);

          NetworkState.State state = parseState(detailedState);
          if (state != null) {
            return Observable.just(new NetworkState(state, ssid));
          } else {
            return Observable.empty();
          }
        })
        .distinctUntilChanged();
  }

  private NetworkState.State parseState(NetworkInfo.DetailedState detailedState) {

    NetworkState.State state = null;

    switch (detailedState) {

      case AUTHENTICATING:
        break;
      case BLOCKED:
        break;
      case CAPTIVE_PORTAL_CHECK:
        break;
      case CONNECTED:
        state = NetworkState.State.CONNECTED;
        break;
      case CONNECTING:
        break;
      case DISCONNECTED:
        state = NetworkState.State.DISCONNECTED;
        break;
      case DISCONNECTING:
        break;
      case FAILED:
        break;
      case IDLE:
        break;
      case OBTAINING_IPADDR:
        break;
      case SCANNING:
        break;
      case SUSPENDED:
        break;
      case VERIFYING_POOR_LINK:
        break;
    }

    return state;
  }

  private String extractSsid(Intent intent) {
    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
    return wifiInfo != null ? wifiInfo.getSSID() : null;
  }

  private NetworkInfo.DetailedState extractState(Intent intent) {
    return ((NetworkInfo) intent.getParcelableExtra(
        WifiManager.EXTRA_NETWORK_INFO)).getDetailedState();
  }
}
