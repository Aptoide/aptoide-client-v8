package cm.aptoide.pt.v8engine.analytics;

import cm.aptoide.pt.dataprovider.ws.v7.BIUtmAnalyticsRequestBody;

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

  public BIUtmAnalyticsRequestBody.Data getUTMTrackingData() {
    BIUtmAnalyticsRequestBody.Data data = new BIUtmAnalyticsRequestBody.Data();
    data.setEntryPoint(this.utm.getEntryPoint());
    data.setSiteVersion(tracking.getSiteVersion());
    data.setUserAgent(tracking.getUserAgent());

    BIUtmAnalyticsRequestBody.App app = new BIUtmAnalyticsRequestBody.App();
    app.setPackageName(tracking.getPackageName());
    app.setUrl(tracking.getUrl());
    data.setApp(app);

    BIUtmAnalyticsRequestBody.UTM utm = new BIUtmAnalyticsRequestBody.UTM();
    utm.setCampaign(this.utm.getUtmCampaign());
    utm.setContent(this.utm.getUtmContent());
    utm.setMedium(this.utm.getUtmMedium());
    utm.setSource(this.utm.getUtmSource());
    data.setUtm(utm);

    return data;
  }
}
