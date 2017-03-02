package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.AptoideEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jdandrade on 27/10/2016.
 */
public class TimelineAnalytics {

  public static final String SOURCE_APTOIDE = "APTOIDE";
  private static final String OPEN_ARTICLE = "OPEN_ARTICLE";
  private static final String OPEN_BLOG = "OPEN_BLOG";
  private static final String OPEN_VIDEO = "OPEN_VIDEO";
  private static final String OPEN_CHANNEL = "OPEN_CHANNEL";
  private static final String OPEN_STORE = "OPEN_STORE";
  private static final String OPEN_APP = "OPEN_APP";
  private static final String UPDATE_APP = "UPDATE_APP";
  private final Analytics analytics;
  private final AptoideAccountManager accountManager;
  private String uniqueIdentifier;

  public TimelineAnalytics(Analytics analytics, AptoideAccountManager accountManager,
      String uniqueIdentifier) {
    this.analytics = analytics;
    this.accountManager = accountManager;
    this.uniqueIdentifier = uniqueIdentifier;
  }

  public void sendOpenAppEvent(String cardType, String source, String packageName) {
    analytics.sendEvent(createEvent(OPEN_APP, createAppData(cardType, source, packageName)));
  }

  public void sendStoreOpenAppEvent(String cardType, String source, String packageName,
      String store) {
    analytics.sendEvent(
        createEvent(OPEN_APP, createStoreAppData(cardType, source, packageName, store)));
  }

  public void sendSimilarOpenAppEvent(String cardType, String source, String packageName,
      String similarPackageName) {
    analytics.sendEvent(createEvent(OPEN_APP,
        createSimilarAppData(cardType, source, packageName, similarPackageName)));
  }

  public void sendRecommendedOpenAppEvent(String cardType, String source, String basedOnPackageName,
      String packageName) {
    analytics.sendEvent(createEvent(OPEN_APP,
        createBasedOnAppData(cardType, source, packageName, basedOnPackageName)));
  }

  public void sendUpdateAppEvent(String cardType, String source, String packageName) {
    analytics.sendEvent(createEvent(UPDATE_APP, createAppData(cardType, source, packageName)));
  }

  public void sendAppUpdateOpenStoreEvent(String cardType, String source, String packageName,
      String store) {
    analytics.sendEvent(
        createEvent(OPEN_STORE, createStoreAppData(cardType, source, packageName, store)));
  }

  public void sendOpenStoreEvent(String cardType, String source, String store) {
    analytics.sendEvent(createEvent(OPEN_STORE, createStoreData(cardType, source, store)));
  }

  public void sendOpenArticleEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(
        createEvent(OPEN_ARTICLE, createArticleData(cardType, source, url, packageName)));
  }

  public void sendOpenBlogEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(
        createEvent(OPEN_BLOG, createArticleData(cardType, source, url, packageName)));
  }

  public void sendOpenVideoEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(
        createEvent(OPEN_VIDEO, createVideoAppData(cardType, source, url, packageName)));
  }

  public void sendOpenChannelEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(
        createEvent(OPEN_CHANNEL, createVideoAppData(cardType, source, url, packageName)));
  }

  private AptoideEvent createEvent(String event, Map<String, Object> data) {
    return new AptoideEvent(accountManager, uniqueIdentifier, data, event, "CLICK", "TIMELINE");
  }

  private Map<String, Object> createAppData(String cardType, String source, String packageName) {
    final HashMap<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    return createTimelineCardData(cardType, source, specific);
  }

  private Map<String, Object> createStoreAppData(String cardType, String source, String packageName,
      String store) {
    final HashMap<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    specific.put("store", store);
    return createTimelineCardData(cardType, source, specific);
  }

  private Map<String, Object> createStoreData(String cardType, String source, String store) {
    final HashMap<String, String> specific = new HashMap<>();
    specific.put("store", store);
    return createTimelineCardData(cardType, source, specific);
  }

  private Map<String, Object> createSimilarAppData(String cardType, String source,
      String packageName, String similarPackageName) {
    final HashMap<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    specific.put("similarTo", similarPackageName);
    return createTimelineCardData(cardType, source, specific);
  }

  private Map<String, Object> createVideoAppData(String cardType, String source, String url,
      String packageName) {
    final HashMap<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    specific.put("url", url);
    return createTimelineCardData(cardType, source, specific);
  }

  private Map<String, Object> createBasedOnAppData(String cardType, String source,
      String packageName, String basedOnPackageName) {
    final HashMap<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    specific.put("basedOn", basedOnPackageName);
    return createTimelineCardData(cardType, source, specific);
  }

  private Map<String, Object> createTimelineCardData(String cardType, String source,
      HashMap<String, String> specific) {
    final Map<String, Object> result = new HashMap<>();
    result.put("cardType", cardType);
    result.put("source", source);
    result.put("specific", specific);
    return result;
  }

  private Map<String, Object> createArticleData(String cardType, String source, String url,
      String packageName) {
    final HashMap<String, String> specific = new HashMap<>();
    specific.put("url", url);
    specific.put("app", packageName);
    return createTimelineCardData(cardType, source, specific);
  }
}