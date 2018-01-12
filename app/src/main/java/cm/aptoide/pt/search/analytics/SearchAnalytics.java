package cm.aptoide.pt.search.analytics;

import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 04/05/17.
 */

public class SearchAnalytics {
  public static final String SEARCH = "Search";
  public static final String NO_RESULTS = "Search_No_Results";
  public static final String APP_CLICK = "Search_Results_App_View_Click";
  public static final String SEARCH_START = "Search_Start";
  private final AnalyticsManager analyticsManager;

  public SearchAnalytics(AnalyticsManager analyticsManager) {
    this.analyticsManager = analyticsManager;
  }

  public void searchFromSuggestion(String query, int suggestionPosition, String context) {
    search(query, true, suggestionPosition,context);
  }

  public void search(String query, String context) {
    search(query, false, 0,context);
  }

  private void search(String query, boolean isSuggestion, int suggestionPosition, String context) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, query);
    map.put(AttributeKey.IS_SUGGESTION, Boolean.toString(isSuggestion));
    if (isSuggestion) {
      map.put(AttributeKey.SUGGESTION_POSITION, Integer.toString(suggestionPosition));
    }
    analyticsManager.logEvent(map, SEARCH, AnalyticsManager.Action.INPUT,context);

  }

  public void searchNoResults(String query, String context) {
    analyticsManager.logEvent(createMapData(AttributeKey.QUERY,query), NO_RESULTS,
        AnalyticsManager.Action.INPUT, context);
  }

  public void searchAppClick(String query, String packageName, String context) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, query);
    map.put(AttributeKey.PACKAGE_NAME, packageName);
    analyticsManager.logEvent(map,APP_CLICK, AnalyticsManager.Action.CLICK,context);
  }

  public void searchStart(@NonNull SearchSource source, String context) {
    analyticsManager.logEvent(createMapData(AttributeKey.SOURCE,source.getIdentifier()),SEARCH_START,
        AnalyticsManager.Action.INPUT,context);
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }

  private static final class AttributeKey {
    private static final String QUERY = "search_term";
    private static final String SOURCE = "source";
    private static final String PACKAGE_NAME = "package_name";
    private static final String IS_SUGGESTION = "is_suggestion";
    private static final String SUGGESTION_POSITION = "suggestion_position";
  }
}
