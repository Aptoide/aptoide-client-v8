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
  private static final String EMPTY = "empty";
  public static final String GAMES_CATEGORY = "games";
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
    sendRakkamSearchResults(searchQueryModel, true, null, false, false, 0, false, false, false,
        "no_info", 0, false, "");
  }

  public void searchAppClick(SearchQueryModel searchQueryModel, String packageName, int position,
      boolean hasAppc, boolean isMigration, boolean isAppBundle, boolean hasObbs,
      int versionCode, boolean isInCatappult, String category) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, searchQueryModel.getFinalQuery());
    map.put(AttributeKey.PACKAGE_NAME, packageName);
    map.put(AttributeKey.IS_AD, false);
    map.put(AttributeKey.POSITION, position);
    analyticsManager.logEvent(map, APP_CLICK, AnalyticsManager.Action.CLICK, getViewName(true));
    sendRakkamSearchResults(searchQueryModel, false, packageName, false, hasAppc, position,
        isMigration, isAppBundle, hasObbs, "no_info", versionCode, isInCatappult, category);
  }

  public void sendRakkamSearchResults(SearchQueryModel searchQueryModel, boolean empty,
      String packageName, boolean isAd, boolean hasAppc, int position, boolean isMigration,
      boolean isAppBundle, boolean hasObbs, String splitTypes, int versionCode,
      boolean isInCatappult, String category) {
    Map<String, Object> map = new HashMap<>();
    map.put(AttributeKey.QUERY, searchQueryModel.getFinalQuery());
    map.put(AttributeKey.KEYWORD_INPUT, searchQueryModel.getUserQuery());
    map.put(AttributeKey.SEARCH_TERM_SOURCE, parseSource(searchQueryModel.getSource()));
    if (!empty) {
      map.put(AttributeKey.PACKAGE_NAME, packageName);
      map.put(AttributeKey.POSITION, position);
      map.put(AttributeKey.IS_AD, isAd);
      map.put(AttributeKey.APP_APPC, hasAppc);

      map.put(AttributeKey.APP_AAB, isAppBundle);
      map.put(AttributeKey.APP_MIGRATION, isMigration);
      map.put(AttributeKey.APP_AAB_INSTALL_TIME, splitTypes);
      map.put(AttributeKey.APP_VERSION_CODE, versionCode);
      map.put(AttributeKey.APP_OBB, hasObbs);
      map.put(AttributeKey.APP_IN_CATAPPULT, isInCatappult);
      if (!category.isEmpty()) {
        map.put(AttributeKey.APP_IS_GAME, category.equals(GAMES_CATEGORY));
      }
    } else {
      map.put(AttributeKey.PACKAGE_NAME, EMPTY);
      map.put(AttributeKey.POSITION, EMPTY);
      map.put(AttributeKey.IS_AD, EMPTY);
      map.put(AttributeKey.APP_APPC, EMPTY);
      map.put(AttributeKey.APP_AAB, EMPTY);
      map.put(AttributeKey.APP_MIGRATION, EMPTY);
      map.put(AttributeKey.APP_AAB_INSTALL_TIME, EMPTY);
      map.put(AttributeKey.APP_VERSION_CODE, EMPTY);
      map.put(AttributeKey.APP_OBB, EMPTY);
      map.put(AttributeKey.APP_IN_CATAPPULT, EMPTY);
      map.put(AttributeKey.APP_IS_GAME, EMPTY);
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
    private static final String APP_APPC = "app_appc";
    private static final String POSITION = "position";
    private static final String KEYWORD_INPUT = "inserted_keyword";
    private static final String AB_TEST_ID = "ab_test_uid";
    private static final String AB_TEST_GROUP = "ab_test_group";
    private static final String APP_AAB = "app_aab";
    private static final String APP_AAB_INSTALL_TIME = "app_aab_install_time";
    private static final String APP_MIGRATION = "app_migration";
    private static final String APP_VERSION_CODE = "app_version_code";
    private static final String APP_OBB = "app_obb";
    private static final String APP_IN_CATAPPULT = "app_in_catappult";
    private static final String APP_IS_GAME = "app_is_game";
  }
}
