package cm.aptoide.pt.spotandshareandroid.hotspotmanager.receiver.scanresults;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.MultiRulesValidator;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.scanner.Hotspot;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.Single;

/**
 * Created by neuro on 05-07-2017.
 */

public class ScanResultsReceiverHelper {

  private final Context context;
  private final WifiManager wifiManager;
  private final MultiRulesValidator<ScanResult> multiRulesValidator;

  public ScanResultsReceiverHelper(Context context, WifiManager wifiManager,
      MultiRulesValidator<ScanResult> multiRulesValidator) {
    this.context = context.getApplicationContext();
    this.wifiManager = wifiManager;
    this.multiRulesValidator = multiRulesValidator;
  }

  public Single<List<Hotspot>> newScanResultsReceiver() {

    BroadcastRegisterOnSubscribe broadcastRegisterOnSubscribe =
        new BroadcastRegisterOnSubscribe(context,
            new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), null, null);

    return Observable.unsafeCreate(broadcastRegisterOnSubscribe)
        .doOnSubscribe(() -> {
          if (!wifiManager.startScan()) {
            throw new IllegalStateException("Unable to start scan");
          }
        })
        .flatMap(intent -> {

          List<ScanResult> scanResults = wifiManager.getScanResults();
          List<ScanResult> filteredResults = multiRulesValidator.filter(scanResults);

          List<Hotspot> hotspots = parseResults(filteredResults);
          return Observable.just(hotspots);
        })
        .first()
        .toSingle();
  }

  private List<Hotspot> parseResults(List<ScanResult> filteredResults) {
    List<Hotspot> hotspots = new LinkedList<>();

    for (ScanResult filteredResult : filteredResults) {
      hotspots.add(Hotspot.from(filteredResult));
    }

    return hotspots;
  }
}
