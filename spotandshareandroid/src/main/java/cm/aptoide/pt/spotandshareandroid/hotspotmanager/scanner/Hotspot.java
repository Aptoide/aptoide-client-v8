package cm.aptoide.pt.spotandshareandroid.hotspotmanager.scanner;

import android.net.wifi.ScanResult;
import lombok.Data;

@Data public class Hotspot {

  private final String ssid;

  public Hotspot(String ssid) {
    this.ssid = ssid;
  }

  @Override public boolean equals(Object obj) {
    return (obj != null && obj instanceof Hotspot && ssid.equals(((Hotspot) obj).getSsid()));
  }

  @Override public int hashCode() {
    return ssid.hashCode();
  }

  public boolean isHidden() {
    return ssid == null || ssid.equals("");
  }

  public static Hotspot from(ScanResult scanResult) {
    String ssid = scanResult.SSID;

    return new Hotspot(ssid);
  }
}
