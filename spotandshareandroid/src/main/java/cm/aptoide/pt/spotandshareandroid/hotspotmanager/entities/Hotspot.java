package cm.aptoide.pt.spotandshareandroid.hotspotmanager.entities;

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
}
