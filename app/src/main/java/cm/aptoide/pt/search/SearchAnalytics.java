package cm.aptoide.pt.search;

import android.os.Bundle;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 04/05/17.
 */

public class SearchAnalytics {

  private static final String QUERY = "search_term";
  private static final String SEARCH = "Search";
  private static final String NO_RESULTS = "Search_No_Results";
  private static final String APP_CLICK = "Search_Results_App_View_Click";
  private static final String SEARCH_FROM_WIDGET = "Search_From_Widget_Click";
  private final Analytics analytics;
  private final AppEventsLogger facebook;

  public SearchAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void search(String query) {
    Map<String, Object> map = new HashMap<>();
    map.put(QUERY, query);
    analytics.sendEvent(new FacebookEvent(facebook, SEARCH, createComplexBundleData(map)));
  }

  public void searchNoResults(String query) {
    analytics.sendEvent(new FacebookEvent(facebook, NO_RESULTS, createBundleData(QUERY, query)));
  }

  public void searchAppClick(String query, String packageName) {
    Map<String, Object> map = new HashMap<>();
    map.put(QUERY, query);
    map.put("package_name", packageName);
    analytics.sendEvent(new FacebookEvent(facebook, APP_CLICK, createComplexBundleData(map)));
  }

  public void searchWidgetClick(){
    analytics.sendEvent(new FacebookEvent(facebook, SEARCH_FROM_WIDGET));
  }

  private Bundle createBundleData(String key, String value) {
    final Bundle data = new Bundle();
    data.putString(key, value);
    return data;
  }

  private Bundle createComplexBundleData(Map<String, Object> keyValuePair) {
    Bundle bundle = new Bundle();
    for (Map.Entry<String, Object> entry : keyValuePair.entrySet()) {
      bundle.putString(entry.getKey(), (String) entry.getValue());
    }
    return bundle;
  }
}
