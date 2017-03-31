package cm.aptoide.pt.spotandshareandroid;

/**
 * Created by filipegoncalves on 02-02-2017.
 */
public class Group {

  private final String ssid;
  private final String deviceName;
  private final String hotspotControlCounter;
  private String deviceID;

  public Group(String ssid, String deviceName, String hotspotControlCounter, String deviceID) {

    this.ssid = ssid;
    this.deviceName = deviceName;
    this.hotspotControlCounter = hotspotControlCounter;
    this.deviceID = deviceID;
  }

  public String getDeviceName() {
    return deviceName;
  }

  public String getHotspotControlCounter() {
    return hotspotControlCounter;
  }

  public String getDeviceID() {
    return deviceID;
  }

  @Override public boolean equals(Object obj) {
    Group group = (Group) obj;
    if (ssid.equals(group.getSsid())) {
      return true;
    }
    return false;
  }

  public String getSsid() {
    return ssid;
  }
}
