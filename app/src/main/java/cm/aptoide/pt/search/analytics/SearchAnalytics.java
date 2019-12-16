package cm.aptoide.pt.search.analytics;

import androidx.annotation.NonNull;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.search.model.SearchQueryModel;
import cm.aptoide.pt.search.model.Source;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pedroribeiro on 04/05/17.
 */

public class SearchAnalytics {
  public static final String SEARCH = "Search";
  public static final String NO_RESULTS = "Search_No_Results";
  public static final String APP_CLICK = "Search_Results_App_View_Click";
  public static final String SEARCH_RESULT_CLICK = "Search_Result_Click";
  public static final String SEARCH_START = "Search_Start";
  public static final String AB_SEARCH_ACTION = "AB_Search_Action";
  public static final String AB_SEARCH_IMPRESSION = "AB_Search_Impression";
  private final AnalyticsManager analyticsManager;
  private final NavigationTracker navigationTracker;

  public SearchAnalytics(AnalyticsManager analyticsManager, NavigationTracker navigationTracker) {
    this.analyticsManager = analyticsManager;
    this.navigationTracker = navigationTracker;
  }

  public void searchFromSuggestion(SearchQueryModel searchQueryModel, int suggestionPosition) {
    search(searchQueryModel, suggestionPosition);
  }

  public void search(SearchQueryModel searchQueryModel) {
    search(searchQueryModel, 0);
  }

  private void search(SearchQueryModel searchQueryModel, int suggestionPosition) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, searchQueryModel.getFinalQuery());
    map.put(AttributeKey.SEARCH_TERM_SOURCE, parseSource(searchQueryModel.getSource()));
    map.put(AttributeKey.KEYWORD_INPUT, searchQueryModel.getUserQuery());
    if (searchQueryModel.getSource() != Source.MANUAL) {
      map.put(AttributeKey.SEARCH_TERM_POSITION, Integer.toString(suggestionPosition));
    }
    analyticsManager.logEvent(map, SEARCH, AnalyticsManager.Action.CLICK, getViewName(false));
  }

  public void searchNoResults(SearchQueryModel searchQueryModel) {
    analyticsManager.logEvent(createMapData(AttributeKey.QUERY, searchQueryModel.getFinalQuery()),
        NO_RESULTS, AnalyticsManager.Action.CLICK, getViewName(false));
    sendRakkamSearchResults(searchQueryModel, true, null, false, false, 0);
  }

  public void searchAppClick(SearchQueryModel searchQueryModel, String packageName, int position,
      boolean hasAppc) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, searchQueryModel.getFinalQuery());
    map.put(AttributeKey.PACKAGE_NAME, packageName);
    map.put(AttributeKey.IS_AD, false);
    map.put(AttributeKey.POSITION, position);
    analyticsManager.logEvent(map, APP_CLICK, AnalyticsManager.Action.CLICK, getViewName(true));
    sendRakkamSearchResults(searchQueryModel, false, packageName, false, hasAppc, position);
  }

  public void searchAdClick(SearchQueryModel searchQueryModel, String packageName, int position,
      boolean hasAppc) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, searchQueryModel.getFinalQuery());
    map.put(AttributeKey.PACKAGE_NAME, packageName);
    map.put(AttributeKey.IS_AD, true);
    map.put(AttributeKey.POSITION, position);
    analyticsManager.logEvent(map, APP_CLICK, AnalyticsManager.Action.CLICK, getViewName(true));
    sendRakkamSearchResults(searchQueryModel, false, packageName, true, hasAppc, position);
  }

  public void sendRakkamSearchResults(SearchQueryModel searchQueryModel, boolean empty,
      String packageName, boolean isAd, boolean isAppc, int position) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, searchQueryModel.getFinalQuery());
    map.put(AttributeKey.KEYWORD_INPUT, searchQueryModel.getUserQuery());
    map.put(AttributeKey.SEARCH_TERM_SOURCE, parseSource(searchQueryModel.getSource()));
    if (!empty) {
      map.put(AttributeKey.PACKAGE_NAME, packageName);
      map.put(AttributeKey.POSITION, position);
      map.put(AttributeKey.IS_AD, isAd);
      map.put(AttributeKey.IS_APPC, isAppc);
    } else {
      map.put(AttributeKey.POSITION, "empty");
    }
    analyticsManager.logEvent(map, SEARCH_RESULT_CLICK, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  private String parseSource(Source source) {
    switch (source) {
      case FROM_TRENDING:
        return "trending";
      case FROM_AUTOCOMPLETE:
        return "autocomplete";
      case DEEPLINK:
        return "deeplink";
      case MANUAL:
      default:
        return "manual";
    }
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

  private static final class AttributeKey {
    private static final String QUERY = "search_term";
    private static final String SOURCE = "source";
    private static final String PACKAGE_NAME = "package_name";
    private static final String SEARCH_TERM_SOURCE = "search_term_source";
    private static final String SEARCH_TERM_POSITION = "search_term_position";
    private static final String IS_AD = "is_ad";
    private static final String IS_APPC = "is_appc";
    private static final String POSITION = "position";
    private static final String KEYWORD_INPUT = "inserted_keyword";
    private static final String AB_TEST_ID = "ab_test_uid";
    private static final String AB_TEST_GROUP = "ab_test_group";
  }
}
