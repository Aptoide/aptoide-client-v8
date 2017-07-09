package cm.aptoide.pt.spotandshareandroid.hotspotmanager.scanner;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.Validator;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.scanner.receivers.ScanResultsReceiver;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by filipe on 26-06-2017.
 */

public class HotspotScanner {

  private final Context context;
  private final WifiManager wifimanager;
  private final List<Validator<ScanResult>> validators;

  public HotspotScanner(Context context, WifiManager wifimanager,
      List<Validator<ScanResult>> validators) {
    this.context = context;
    this.wifimanager = wifimanager;
    this.validators = validators;
  }

  public HotspotScanner(Context context, WifiManager wifimanager) {
    this(context, wifimanager, new LinkedList<>());
  }

  public void scan(ScanResultsReceiver.OnScanResults onScanResults) {
    registerScannerBroadcast(onScanResults);
    wifimanager.startScan();
  }

  private void registerScannerBroadcast(ScanResultsReceiver.OnScanResults onScanResults) {
    context.registerReceiver(
        new ScanResultsReceiver(wifimanager, new MultiRulesValidator<>(validators), onScanResults),
        new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
  }
}
