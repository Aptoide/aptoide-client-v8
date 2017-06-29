package cm.aptoide.pt.spotandshareandroid.hotspotmanager.validators;

import android.net.wifi.ScanResult;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by neuro on 28-06-2017.
 */
public class ScanResultValidatorTest {

  private ScanResult validScanResult;
  private ScanResult invalidScanResult;

  @Before public void init() {
    validScanResult = mock(ScanResult.class);
    validScanResult.SSID = "APTXVgagdsa";

    invalidScanResult = mock(ScanResult.class);
    invalidScanResult.SSID = "APTX_Vgagdsa";
  }

  @Test public void validateValidSsid() throws Exception {
    ScanResultValidator scanResultValidator = new ScanResultValidator();

    assertEquals(scanResultValidator.validate(validScanResult), true);
  }

  @Test public void validateInvalidSsid() throws Exception {
    ScanResultValidator scanResultValidator = new ScanResultValidator();

    assertEquals(scanResultValidator.validate(invalidScanResult), false);
  }
}