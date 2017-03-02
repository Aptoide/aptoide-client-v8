package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v7.AnalyticsEventRequest;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.TimelineEvent;

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

  public void sendEvent(TimelineEvent event) {
    analytics.sendEvent(event);
  }

  public void sendOpenAppEvent(String cardType, String source, String packageName) {
    String event = OPEN_APP;
    AnalyticsEventRequest.Body.Specific specificData =
        AnalyticsEventRequest.Body.Specific.builder().app(packageName).build();
    analytics.sendEvent(createTimelineEvent(cardType, source, event, specificData));
  }

  public void sendStoreOpenAppEvent(String cardType, String source, String packageName,
      String store) {
    analytics.sendEvent(createTimelineEvent(cardType, source, OPEN_APP,
        AnalyticsEventRequest.Body.Specific.builder().app(packageName).store(store).build()));
  }

  public void sendSimilarOpenAppEvent(String cardType, String source, String packageName,
      String similarPackageName) {
    analytics.sendEvent(createTimelineEvent(source, cardType, OPEN_APP,
        AnalyticsEventRequest.Body.Specific.builder()
            .app(packageName)
            .similarTo(similarPackageName)
            .build()));
  }

  public void sendRecommendedOpenAppEvent(String cardType, String source, String similarPackageName,
      String packageName) {
    analytics.sendEvent(createTimelineEvent(cardType, source, OPEN_APP,
        AnalyticsEventRequest.Body.Specific.builder()
            .basedOn(similarPackageName)
            .app(packageName)
            .build()));
  }

  public void sendUpdateAppEvent(String cardType, String source, String packageName) {
    analytics.sendEvent(createTimelineEvent(cardType, source, UPDATE_APP,
        AnalyticsEventRequest.Body.Specific.builder().app(packageName).build()));
  }

  public void sendAppUpdateOpenStoreEvent(String cardType, String source, String packageName,
      String store) {
    analytics.sendEvent(createTimelineEvent(cardType, source, OPEN_STORE,
        AnalyticsEventRequest.Body.Specific.builder().app(packageName).store(store).build()));
  }

  public void sendOpenStoreEvent(String cardType, String source, String store) {
    analytics.sendEvent(createTimelineEvent(cardType, source, OPEN_STORE,
        AnalyticsEventRequest.Body.Specific.builder().store(store).build()));
  }

  public void sendOpenArticleEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(createTimelineEvent(cardType, source, OPEN_ARTICLE,
        AnalyticsEventRequest.Body.Specific.builder().url(url).app(packageName).build()));
  }

  public void sendOpenBlogEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(createTimelineEvent(cardType, source, OPEN_BLOG,
        AnalyticsEventRequest.Body.Specific.builder().url(url).app(packageName).build()));
  }

  public void sendOpenVideoEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(createTimelineEvent(cardType, source, OPEN_VIDEO,
        AnalyticsEventRequest.Body.Specific.builder().url(url).app(packageName).build()));
  }

  public void sendOpenChannelEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(createTimelineEvent(cardType, source, OPEN_CHANNEL,
        AnalyticsEventRequest.Body.Specific.builder().url(url).app(packageName).build()));
  }

  private TimelineEvent createTimelineEvent(String cardType, String source, String event,
      AnalyticsEventRequest.Body.Specific specificData) {
    return new TimelineEvent(source, cardType, event, accountManager, uniqueIdentifier,
        specificData);
  }
}