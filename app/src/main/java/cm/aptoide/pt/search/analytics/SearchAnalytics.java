package cm.aptoide.pt.search.analytics;

import android.support.annotation.NonNull;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
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
  public static final String AB_SEARCH_ACTION = "AB_Search_Action";
  public static final String AB_SEARCH_IMPRESSION = "AB_Search_Impression";
  private static final String FROM_TRENDING = "trending";
  private static final String FROM_AUTOCOMPLETE = "autocomplete";
  private static final String MANUAL = "manual";
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;

  public SearchAnalytics(AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void searchFromSuggestion(String query, int suggestionPosition, String inputQuery) {

    search(query, true, suggestionPosition,
        inputQuery.isEmpty() ? FROM_TRENDING : FROM_AUTOCOMPLETE, inputQuery);
  }

  public void search(String query) {
    search(query, false, 0, MANUAL, query);
  }

  private void search(String query, boolean isSuggestion, int suggestionPosition, String source,
      String inputQuery) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, query);
    map.put(AttributeKey.SEARCH_TERM_SOURCE, source);
    map.put(AttributeKey.KEYWORD_INPUT, inputQuery);
    if (isSuggestion) {
      map.put(AttributeKey.SEARCH_TERM_POSITION, Integer.toString(suggestionPosition));
    }
    analyticsManager.logEvent(map, SEARCH, AnalyticsManager.Action.CLICK, getViewName(false));
  }

  public void searchNoResults(String query) {
    analyticsManager.logEvent(createMapData(AttributeKey.QUERY, query), NO_RESULTS,
        AnalyticsManager.Action.CLICK, getViewName(false));
  }

  public void searchAppClick(String query, String packageName, int position) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, query);
    map.put(AttributeKey.PACKAGE_NAME, packageName);
    map.put(AttributeKey.IS_AD, false);
    map.put(AttributeKey.POSITION, position);
    analyticsManager.logEvent(map, APP_CLICK, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void searchAdClick(String query, String packageName, int position) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, query);
    map.put(AttributeKey.PACKAGE_NAME, packageName);
    map.put(AttributeKey.IS_AD, true);
    map.put(AttributeKey.POSITION, position);
    analyticsManager.logEvent(map, APP_CLICK, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void searchStart(@NonNull SearchSource source, boolean isCurrent) {
    analyticsManager.logEvent(createMapData(AttributeKey.SOURCE, source.getIdentifier()),
        SEARCH_START, AnalyticsManager.Action.CLICK, getViewName(isCurrent));
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }

  public void recordAbTestActionAnalytics(String experimentId, String experimentGroup, String query,
      int position, String packageName) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.AB_TEST_ID, experimentId);
    map.put(AttributeKey.AB_TEST_GROUP, experimentGroup);
    map.put(AttributeKey.QUERY, query);
    map.put(AttributeKey.POSITION, position);
    map.put(AttributeKey.PACKAGE_NAME, packageName);
    analyticsManager.logEvent(map, AB_SEARCH_ACTION, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void recordAbTestImpressionAnalytics(String experimentId, String experimentGroup,
      String query) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.AB_TEST_ID, experimentId);
    map.put(AttributeKey.AB_TEST_GROUP, experimentGroup);
    map.put(AttributeKey.QUERY, query);
    analyticsManager.logEvent(map, AB_SEARCH_IMPRESSION, AnalyticsManager.Action.IMPRESSION,
        getViewName(true));
  }

  private static final class AttributeKey {
    private static final String QUERY = "search_term";
    private static final String SOURCE = "source";
    private static final String PACKAGE_NAME = "package_name";
    private static final String SEARCH_TERM_SOURCE = "search_term_source";
    private static final String SEARCH_TERM_POSITION = "search_term_position";
    private static final String IS_AD = "is_ad";
    private static final String POSITION = "position";
    private static final String KEYWORD_INPUT = "inserted_keyword";
    private static final String AB_TEST_ID = "ab_test_uid";
    private static final String AB_TEST_GROUP = "ab_test_group";
  }
}
