package cm.aptoide.pt.spotandshareandroid.hotspotmanager.validators;

import android.net.wifi.ScanResult;
import cm.aptoide.pt.spotandshareandroid.hotspotmanager.Validator;

/**
 * Created by neuro on 28-06-2017.
 */

public class ScanResultValidator implements Validator<ScanResult> {

  private final String APTXV = "APTXV";

  @Override public boolean validate(ScanResult scanResult) {
    return scanResult.SSID.contains(APTXV);
  }
}
