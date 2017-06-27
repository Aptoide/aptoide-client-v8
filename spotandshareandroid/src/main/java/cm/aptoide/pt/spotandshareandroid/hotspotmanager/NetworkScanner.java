package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import java.util.List;

/**
 * Created by filipe on 26-06-2017.
 */

public class NetworkScanner {

  private Context context;
  private WifiManager wifimanager;

  public NetworkScanner(Context context, WifiManager wifimanager) {
    this.context = context;
    this.wifimanager = wifimanager;
  }

  public void scan() {
    registerScannerBroadcast();
    wifimanager.startScan();
  }

  private void registerScannerBroadcast() {
    context.registerReceiver(new ScanResultsProvider(),
        new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
  }

  private class ScanResultsProvider extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
      List<ScanResult> scanResults = wifimanager.getScanResults();
      for (int i = 0; i < scanResults.size(); i++) {
        //// TODO: 26-06-2017 filipe APPLY SPOT&SHARE RULES TO FILTER NON GROUP NETWORKS & store them in a list
      }

      context.unregisterReceiver(this);
    }
  }
}
