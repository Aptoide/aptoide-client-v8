package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.logger.Logger;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

/**
 * Created by neuro on 22-06-2017.
 */

class JoinHotspotManager {

  private static final String TAG = JoinHotspotManager.class.getSimpleName();

  private static final int ERROR_ON_RECONNECT = 0;
  private static final int SUCCESSFUL_JOIN = 2;

  private final Context context;
  private final WifiManager wifimanager;

  @Getter(lazy = true) private final ScheduledExecutorService scheduledExecutorService =
      newSingleThreadScheduledExecutor();
  private final Executor executor;
  private ScheduledFuture<?> timeoutFuture;

  JoinHotspotManager(Context context, WifiManager wifimanager) {
    this.context = context;
    this.wifimanager = wifimanager;
    executor = Executors.newSingleThreadExecutor();
  }

  private ScheduledExecutorService newSingleThreadScheduledExecutor() {
    return Executors.newSingleThreadScheduledExecutor();
  }

  public boolean joinHotspot(String ssid, WifiStateListener wifiStateListener, long timeout) {
    return joinHotspot(ssid, wifiStateListener, timeout,
        new JoinNetworkBroadcastReceiver(wifiStateListener, wifimanager, ssid));
  }

  private boolean joinHotspot(String ssid, WifiStateListener wifiStateListener, long timeout,
      JoinNetworkBroadcastReceiver joinNetworkBroadcastReceiver) {

    timeoutFuture = getScheduledExecutorService().schedule(() -> {
      context.unregisterReceiver(joinNetworkBroadcastReceiver);
      wifiStateListener.onStateChanged(false);
      Logger.e(TAG, "joinHotspot: JoinHotspotManager timed out!");
    }, timeout, TimeUnit.MILLISECONDS);

    return joinHotspot(ssid, true, joinNetworkBroadcastReceiver) == SUCCESSFUL_JOIN;
  }

  private int joinHotspot(String ssid, boolean shouldReconnect,
      @Nullable JoinNetworkBroadcastReceiver joinNetworkBroadcastReceiver) {
    // TODO: 22-06-2017 neuro ligar wifi when needed
    WifiConfiguration conf = new WifiConfiguration();
    //Logger.d(TAG, "chosen hotspot is : " + ssid);
    conf.SSID = "\"" + ssid + "\"";
    conf.preSharedKey = "\"passwordAptoide\"";
    conf.hiddenSSID = true;
    conf.status = WifiConfiguration.Status.ENABLED;
    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

    int netid = wifimanager.addNetwork(conf);
    //Logger.d(TAG, "netid is: " + netid);

    List<WifiConfiguration> list = wifimanager.getConfiguredNetworks();
    if (list != null) {
      for (WifiConfiguration i : list) {
        if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
          try {
            // TODO: 21-06-2017 neuro sleeps lol

            wifimanager.disconnect();
            wifimanager.enableNetwork(i.networkId, true);
            ////Logger.d(TAG,
            //    "i.networkId " + i.networkId + "\n" + "o net id do add esta a : " + netid);

            if (shouldReconnect) {
              boolean reconnect = wifimanager.reconnect();

              if (reconnect) {
                if (joinNetworkBroadcastReceiver != null) {
                  registerStateChangeReceiver(joinNetworkBroadcastReceiver);
                }
                return SUCCESSFUL_JOIN;
              } else {
                return ERROR_ON_RECONNECT;
              }
            } else {
              if (joinNetworkBroadcastReceiver != null) {
                registerStateChangeReceiver(joinNetworkBroadcastReceiver);
              }
              return SUCCESSFUL_JOIN;
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
    return HotspotManager.ERROR_UNKNOWN;
  }

  private void registerStateChangeReceiver(
      JoinNetworkBroadcastReceiver joinNetworkBroadcastReceiver) {
    context.registerReceiver(joinNetworkBroadcastReceiver,
        new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
  }

  public interface WifiStateListener {
    void onStateChanged(boolean enabled);
  }

  private class JoinNetworkBroadcastReceiver extends BroadcastReceiver {

    private final WifiStateListener wifiStateListener;
    private final WifiManager wifimanager;
    private final String ssid;

    private boolean retried = false;

    public JoinNetworkBroadcastReceiver(WifiStateListener wifiStateListener,
        WifiManager wifimanager, String ssid) {
      this.wifiStateListener = wifiStateListener;
      this.wifimanager = wifimanager;
      this.ssid = ssid;
    }

    @Override public void onReceive(Context context, Intent intent) {

      ConnectivityManager conMgr =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      int currentApiVersion = Build.VERSION.SDK_INT;
      if (currentApiVersion >= Build.VERSION_CODES.LOLLIPOP) {
        Network[] networks = conMgr.getAllNetworks();
        if (networks != null) {
          for (Network network : networks) {
            NetworkInfo networkInfo = conMgr.getNetworkInfo(network);
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
              if (networkInfo.isAvailable() && networkInfo.isConnected()) {
                checkCorrectNetworkConnected();
              }
            }
          }
        }
      } else {
        NetworkInfo[] networkInfoArray = conMgr.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfoArray) {
          if (networkInfo.getState() == NetworkInfo.State.CONNECTED
              && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            checkCorrectNetworkConnected();
          }
        }
      }
    }

    private void checkCorrectNetworkConnected() {

      WifiInfo wifiInfo = wifimanager.getConnectionInfo();
      if (wifiInfo.getSSID()
          .contains(ssid)) {
        wifiStateListener.onStateChanged(true);
        context.unregisterReceiver(this);
        timeoutFuture.cancel(false);
      } else {//connected to the wrong network
        if (!retried && !TextUtils.isEmpty(ssid)) {
          retried = true;
          executor.execute(() -> joinHotspot(ssid, false, null));
        } else {
          retried = false;
          joinHotspot(ssid, true, null);
        }
      }
    }
  }
}
