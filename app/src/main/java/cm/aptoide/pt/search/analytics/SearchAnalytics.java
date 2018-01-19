package cm.aptoide.pt.search.analytics;

import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.NavigationTracker;
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
  private final NavigationTracker navigationTracker;

  public SearchAnalytics(AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void searchFromSuggestion(String query, int suggestionPosition) {
    search(query, true, suggestionPosition);
  }

  public void search(String query) {
    search(query, false, 0);
  }

  private void search(String query, boolean isSuggestion, int suggestionPosition) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, query);
    map.put(AttributeKey.IS_SUGGESTION, Boolean.toString(isSuggestion));
    if (isSuggestion) {
      map.put(AttributeKey.SUGGESTION_POSITION, Integer.toString(suggestionPosition));
    }
    analyticsManager.logEvent(map, SEARCH, AnalyticsManager.Action.CLICK, getViewName(false));

  }

  public void searchNoResults(String query) {
    analyticsManager.logEvent(createMapData(AttributeKey.QUERY,query), NO_RESULTS,
        AnalyticsManager.Action.CLICK, getViewName(false));
  }

  public void searchAppClick(String query, String packageName) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, query);
    map.put(AttributeKey.PACKAGE_NAME, packageName);
    analyticsManager.logEvent(map,APP_CLICK, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void searchStart(@NonNull SearchSource source, boolean isCurrent) {
    analyticsManager.logEvent(createMapData(AttributeKey.SOURCE,source.getIdentifier()),SEARCH_START,
        AnalyticsManager.Action.CLICK, getViewName(isCurrent));
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }

  private String getViewName(boolean isCurrent){
    String viewName = "";
    if(isCurrent){
      viewName = navigationTracker.getCurrentViewName();
    }
    else{
      viewName = navigationTracker.getPreviousViewName();
    }
    if(viewName.equals("")) {
      return "SearchAnalytics"; //Default value, shouldn't get here
    }
    return viewName;
  }

  private static final class AttributeKey {
    private static final String QUERY = "search_term";
    private static final String SOURCE = "source";
    private static final String PACKAGE_NAME = "package_name";
    private static final String IS_SUGGESTION = "is_suggestion";
    private static final String SUGGESTION_POSITION = "suggestion_position";
  }
}
