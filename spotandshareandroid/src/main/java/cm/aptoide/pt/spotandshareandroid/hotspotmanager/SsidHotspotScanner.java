package cm.aptoide.pt.spotandshareandroid.hotspotmanager;

import android.content.Context;
import android.net.wifi.ScanResult;
import cm.aptoide.pt.spotandshareandroid.util.TaskQueue;
import java.util.Collections;
import java.util.List;

/**
 * Created by filipe on 26-06-2017.
 */

public class SsidHotspotScanner extends HotspotScanner {

  public SsidHotspotScanner(Context context, TaskQueue taskQueue, String ssid) {
    super(context, taskQueue, createSsidValidators(ssid));
  }

  private static List<Validator<ScanResult>> createSsidValidators(String ssid) {
    return Collections.singletonList(scanResult -> scanResult.SSID.equals(ssid));
  }
}
