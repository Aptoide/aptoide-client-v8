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

  public String getSsid() {
    return ssid;
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


}
