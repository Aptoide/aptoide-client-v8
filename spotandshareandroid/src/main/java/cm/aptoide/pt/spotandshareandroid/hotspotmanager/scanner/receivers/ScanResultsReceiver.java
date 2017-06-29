package cm.aptoide.pt.spotandshareandroid.hotspotmanager.scanner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.scanner.Hotspot;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.scanner.MultiRulesValidator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 28-06-2017.
 */
public class ScanResultsReceiver extends BroadcastReceiver {

  private final MultiRulesValidator<ScanResult> multiRulesValidator;
  private final WifiManager wifimanager;
  private final OnScanResults onScanResults;

  public ScanResultsReceiver(WifiManager wifimanager,
      MultiRulesValidator<ScanResult> multiRulesValidator, OnScanResults onScanResults) {
    this.wifimanager = wifimanager;
    this.multiRulesValidator = multiRulesValidator;
    this.onScanResults = onScanResults;
  }

  @Override public void onReceive(Context context, Intent intent) {
    List<ScanResult> scanResults = wifimanager.getScanResults();
    List<ScanResult> filteredResults = multiRulesValidator.filter(scanResults);

    onScanResults.onScanResults(parseResults(filteredResults));

    context.unregisterReceiver(this);
  }

  private List<Hotspot> parseResults(List<ScanResult> filteredResults) {
    List<Hotspot> hotspots = new LinkedList<>();

    for (ScanResult filteredResult : filteredResults) {
      hotspots.add(Hotspot.from(filteredResult));
    }

    return hotspots;
  }

  public interface OnScanResults {

    void onScanResults(List<Hotspot> hotspots);
  }
}
