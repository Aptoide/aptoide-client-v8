package cm.aptoide.pt.analytics;

import cm.aptoide.pt.dataprovider.ws.v7.BiUtmAnalyticsRequestBody;

/**
 * Created by pedroribeiro on 29/06/17.
 */

public class UTMTrackingBuilder {

  private final Tracking tracking;
  private final UTM utm;

  public UTMTrackingBuilder(Tracking tracking, UTM utm) {
    this.tracking = tracking;
    this.utm = utm;
  }

  public BiUtmAnalyticsRequestBody.Data getUTMTrackingData() {
    BiUtmAnalyticsRequestBody.Data data = new BiUtmAnalyticsRequestBody.Data();
    data.setEntryPoint(this.utm.getEntryPoint());
    data.setSiteVersion(tracking.getSiteVersion());
    data.setUserAgent(tracking.getUserAgent());

    BiUtmAnalyticsRequestBody.App app = new BiUtmAnalyticsRequestBody.App();
    app.setPackageName(tracking.getPackageName());
    app.setUrl(tracking.getUrl());
    data.setApp(app);

    BiUtmAnalyticsRequestBody.UTM utm = new BiUtmAnalyticsRequestBody.UTM();
    utm.setCampaign(this.utm.getUtmCampaign());
    utm.setContent(this.utm.getUtmContent());
    utm.setMedium(this.utm.getUtmMedium());
    utm.setSource(this.utm.getUtmSource());
    data.setUtm(utm);

    return data;
  }
}
