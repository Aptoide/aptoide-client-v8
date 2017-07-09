package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.receiver.scanresults.ScanResultsReceiverHelper;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.scanner.Hotspot;
import cm.aptoide.pt.spotandshareandroid.util.TaskQueue;
import java.util.Collections;
import java.util.List;
import rx.Single;

/**
 * Created by filipe on 26-06-2017.
 */

class HotspotScanner {

  private final Context context;
  private final TaskQueue taskQueue;
  private final List<Validator<ScanResult>> validators;

  public HotspotScanner(Context context, TaskQueue taskQueue) {
    this(context, taskQueue, Collections.singletonList(scanResult -> true));
  }

  HotspotScanner(Context context, TaskQueue taskQueue, List<Validator<ScanResult>> validators) {
    this.context = context;
    this.taskQueue = taskQueue;
    this.validators = validators;
  }

  public Single<List<Hotspot>> scan() {
    WifiManager wifiManager = (WifiManager) context.getApplicationContext()
        .getSystemService(Context.WIFI_SERVICE);

    ScanResultsReceiverHelper scanResultsReceiverHelper =
        new ScanResultsReceiverHelper(context, wifiManager, new MultiRulesValidator<>(validators));
    Single<List<Hotspot>> hotspotsSingle = scanResultsReceiverHelper.newScanResultsReceiver();

    return taskQueue.submitTask(hotspotsSingle);
  }
}
