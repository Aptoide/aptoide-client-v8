package cm.aptoide.pt.v8engine.analytics;

import lombok.Data;

/**
 * Created by pedroribeiro on 29/06/17.
 */

@Data public class UTM {

  private final String utmSource;
  private final String utmMedium;
  private final String utmCampaign;
  private final String utmContent;
  private final String entryPoint;

  public UTM(String utmSource, String utmMedium, String utmCampaign, String utmContent,
      String entryPoint) {
    this.utmSource = utmSource;
    this.utmMedium = utmMedium;
    this.utmCampaign = utmCampaign;
    this.utmContent = utmContent;
    this.entryPoint = entryPoint;
  }
}
