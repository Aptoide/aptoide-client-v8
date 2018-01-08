package cm.aptoide.pt.search.analytics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 04/05/17.
 */

public class SearchAnalytics {
  private final Analytics analytics;
  private final AppEventsLogger facebook;

  public SearchAnalytics(Analytics analytics, AppEventsLogger facebook) {
    this.analytics = analytics;
    this.facebook = facebook;
  }

  public void searchFromSuggestion(String query, int suggestionPosition) {
    search(query, true, suggestionPosition);
  }

  public void search(String query) {
    search(query, false, 0);
  }

  private void search(String query, boolean isSuggestion, int suggestionPosition) {
    Map<String, String> map = new HashMap<>();
    map.put(AttributeKey.QUERY, query);
    map.put(AttributeKey.IS_SUGGESTION, Boolean.toString(isSuggestion));
    if (isSuggestion) {
      map.put(AttributeKey.SUGGESTION_POSITION, Integer.toString(suggestionPosition));
    }
    analytics.sendEvent(
        new FacebookEvent(facebook, EventName.SEARCH, createComplexBundleData(map)));
  }

  public void searchNoResults(String query) {
    analytics.sendEvent(new FacebookEvent(facebook, EventName.NO_RESULTS,
        createBundleData(AttributeKey.QUERY, query)));
  }

  public void searchAppClick(String query, String packageName) {
    Map<String, String> map = new HashMap<>();
    map.put(AttributeKey.QUERY, query);
    map.put(AttributeKey.PACKAGE_NAME, packageName);
    analytics.sendEvent(
        new FacebookEvent(facebook, EventName.APP_CLICK, createComplexBundleData(map)));
  }

  public void searchStart(@NonNull SearchSource source) {
    analytics.sendEvent(new FacebookEvent(facebook, EventName.SEARCH_START,
        createBundleData(AttributeKey.SOURCE, source.getIdentifier())));
  }

  private Bundle createBundleData(String key, String value) {
    final Bundle data = new Bundle();
    data.putString(key, value);
    return data;
  }

  private Bundle createComplexBundleData(Map<String, String> keyValuePair) {
    Bundle bundle = new Bundle();
    for (Map.Entry<String, String> entry : keyValuePair.entrySet()) {
      bundle.putString(entry.getKey(), entry.getValue());
    }
    return bundle;
  }

  private static final class EventName {
    private static final String SEARCH = "Search";
    private static final String NO_RESULTS = "Search_No_Results";
    private static final String APP_CLICK = "Search_Results_App_View_Click";
    private static final String SEARCH_START = "Search_Start";
  }

  private static final class AttributeKey {
    private static final String QUERY = "search_term";
    private static final String SOURCE = "source";
    private static final String PACKAGE_NAME = "package_name";
    private static final String IS_SUGGESTION = "is_suggestion";
    private static final String SUGGESTION_POSITION = "suggestion_position";
  }
}
