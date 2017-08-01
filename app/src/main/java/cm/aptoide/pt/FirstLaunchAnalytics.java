package cm.aptoide.pt;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import cm.aptoide.pt.analytics.events.FlurryEvent;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 27/06/17.
 */

public class FirstLaunchAnalytics {

  private static final String FIRST_LAUNCH = "Aptoide_First_Launch";
  private static final String UTM_SOURCE = "UTM_Source";
  private static final String UTM_MEDIUM = "UTM_Medium";
  private static final String UTM_CAMPAIGN = "UTM_Campaign";
  private static final String UTM_CONTENT = "UTM_Content";
  private static final String ENTRY_POINT = "Entry_Point";
  private final AppEventsLogger facebook;
  private final Analytics analytics;

  public FirstLaunchAnalytics(AppEventsLogger facebook, Analytics analytics) {
    this.facebook = facebook;
    this.analytics = analytics;
  }

  public void sendFirstLaunchEvent(String utmSource, String utmMedium, String utmCampaign,
      String utmContent, String entryPoint) {
    analytics.sendEvent(new FacebookEvent(facebook, FIRST_LAUNCH,
        createFacebookFirstLaunchDataBundle(utmSource, utmMedium, utmCampaign, utmContent,
            entryPoint)));
    analytics.sendEvent(new FlurryEvent(FIRST_LAUNCH,
        createFlurryFirstLaunchDataMap(utmSource, utmMedium, utmCampaign, utmContent, entryPoint)));
  }

  private Map<String, String> createFlurryFirstLaunchDataMap(String utmSource, String utmMedium,
      String utmCampaign, String utmContent, String entryPoint) {
    Map<String, String> map = new HashMap<>();
    map.put(UTM_SOURCE, utmSource);
    map.put(UTM_MEDIUM, utmMedium);
    map.put(UTM_CAMPAIGN, utmCampaign);
    map.put(UTM_CONTENT, utmContent);
    map.put(ENTRY_POINT, entryPoint);
    return map;
  }

  private Bundle createFacebookFirstLaunchDataBundle(String utmSource, String utmMedium,
      String utmCampaign, String utmContent, String entryPoint) {
    Bundle data = new Bundle();
    data.putString(UTM_SOURCE, utmSource);
    data.putString(UTM_MEDIUM, utmMedium);
    data.putString(UTM_CAMPAIGN, utmCampaign);
    data.putString(UTM_CONTENT, utmContent);
    data.putString(ENTRY_POINT, entryPoint);
    return data;
  }
}
