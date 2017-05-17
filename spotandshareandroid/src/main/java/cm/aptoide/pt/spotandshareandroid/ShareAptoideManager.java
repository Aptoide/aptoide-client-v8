package cm.aptoide.pt.spotandshareandroid;

/**
 * Created by filipe on 17-05-2017.
 */

public class ShareAptoideManager {

  private static final String SSID = "Aptoide_Share";
  private HotspotManager hotspotManager;

  public ShareAptoideManager(HotspotManager hotspotManager) {
    this.hotspotManager = hotspotManager;
  }

  public int enableHotspot() {
    return hotspotManager.enableHotspot(SSID);
  }
}
