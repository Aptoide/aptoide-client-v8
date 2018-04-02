package cm.aptoide.pt.timeline;

import android.support.annotation.NonNull;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.social.data.AggregatedRecommendation;
import cm.aptoide.pt.social.data.AppUpdate;
import cm.aptoide.pt.social.data.AppUpdateCardTouchEvent;
import cm.aptoide.pt.social.data.CardTouchEvent;
import cm.aptoide.pt.social.data.CardType;
import cm.aptoide.pt.social.data.Media;
import cm.aptoide.pt.social.data.PopularApp;
import cm.aptoide.pt.social.data.PopularAppTouchEvent;
import cm.aptoide.pt.social.data.Post;
import cm.aptoide.pt.social.data.RatedRecommendation;
import cm.aptoide.pt.social.data.ReadPostsPersistence;
import cm.aptoide.pt.social.data.Recommendation;
import cm.aptoide.pt.social.data.SocialHeaderCardTouchEvent;
import cm.aptoide.pt.social.data.StoreAppCardTouchEvent;
import cm.aptoide.pt.social.data.StoreCardTouchEvent;
import cm.aptoide.pt.social.data.StoreLatestApps;
import cm.aptoide.pt.social.data.analytics.EventErrorHandler;
import cm.aptoide.pt.social.data.share.ShareEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Completable;

/**
 * Created by jdandrade on 27/10/2016.
 */
public class TimelineAnalytics {

  public static final String SOURCE_APTOIDE = "APTOIDE";

  public static final String SOCIAL_CARD_ACTION_SHARE_CONTINUE = "Continue";
  public static final String SOCIAL_CARD_ACTION_SHARE_CANCEL = "Cancel";
  public static final String PREVIOUS_CONTEXT = "previous_context";
  public static final String STORE = "store";
  public static final String APPS_TIMELINE_EVENT = "Apps Timeline";
  public static final String OPEN_ARTICLE = "OPEN_ARTICLE";
  public static final String OPEN_ARTICLE_HEADER = "OPEN_ARTICLE_HEADER";
  public static final String OPEN_BLOG = "OPEN_BLOG";
  public static final String OPEN_VIDEO = "OPEN_VIDEO";
  public static final String OPEN_VIDEO_HEADER = "OPEN_VIDEO_HEADER";
  public static final String OPEN_CHANNEL = "OPEN_CHANNEL";
  public static final String OPEN_STORE = "OPEN_STORE";
  public static final String OPEN_STORE_PROFILE = "OPEN_STORE_PROFILE";
  public static final String OPEN_APP = "OPEN_APP";
  public static final String OPEN_APP_VIEW = "OPEN_APP_VIEW";
  public static final String OPEN_TIMELINE_EVENT = "OPEN_TIMELINE";
  public static final String UPDATE_APP = "UPDATE_APP";
  public static final String FOLLOW_FRIENDS = "Apps_Timeline_Follow_Friends";
  public static final String LIKE = "LIKE";
  public static final String COMMENT = "COMMENT";
  public static final String SHARE = "SHARE";
  public static final String SHARE_SEND = "SHARE_SEND";
  public static final String COMMENT_SEND = "COMMENT_SEND";
  public static final String FAB = "FAB";
  public static final String SCROLLING_EVENT = "SCROLLING";
  public static final String TIMELINE_OPENED = "Apps_Timeline_Open";
  public static final String SOCIAL_CARD_PREVIEW = "Apps_Timeline_Social_Card_Preview";
  public static final String CARD_ACTION = "Apps_Timeline_Card_Action";
  public static final String MESSAGE_IMPRESSION = "Message_Impression";
  public static final String MESSAGE_INTERACT = "Message_Interact";
  private static final String CARD_TYPE = "card_type";
  private static final String ACTION = "action";
  private static final String SOCIAL_ACTION = "social_action";
  private static final String PACKAGE = "package_name";
  private static final String PUBLISHER = "publisher";
  private static final String TITLE = "title";
  private static final String BLANK = "(blank)";
  private static final String TIMELINE_VERSION = "timeline_version";
  private static final String SOURCE = "source";
  private static final String APPS_SHORTCUTS = "apps_shortcuts";
  private static final String EXTERNAL = "EXTERNAL";
  private final NotificationAnalytics notificationAnalytics;
  private final NavigationTracker navigationTracker;
  private final ReadPostsPersistence readPostsPersistence;
  private final List<Map<String, Object>> openTimelineEventsData;
  private final AnalyticsManager analyticsManager;
  private String version;

  public TimelineAnalytics(NotificationAnalytics notificationAnalytics,
      NavigationTracker navigationTracker, ReadPostsPersistence readPostsPersistence,
      AnalyticsManager analyticsManager) {
    this.notificationAnalytics = notificationAnalytics;
    this.navigationTracker = navigationTracker;
    this.readPostsPersistence = readPostsPersistence;
    this.analyticsManager = analyticsManager;
    this.openTimelineEventsData = new ArrayList<>();
  }

  public void sendSocialCardPreviewActionEvent(String value) {
    analyticsManager.logEvent(createMapData(ACTION, value), SOCIAL_CARD_PREVIEW,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendSocialActionEvent(TimelineSocialActionData timelineSocialActionData) {
    analyticsManager.logEvent(createSocialActionEventData(timelineSocialActionData), CARD_ACTION,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void notificationShown(String url) {
    notificationAnalytics.sendNotificationTouchEvent(url);
  }

  private Map<String, Object> createSocialActionEventData(
      TimelineSocialActionData timelineSocialActionData) {
    Map<String, Object> data = new HashMap<>();
    data.put(CARD_TYPE, timelineSocialActionData.getCardType());
    data.put(ACTION, timelineSocialActionData.getAction());
    data.put(SOCIAL_ACTION, timelineSocialActionData.getSocialAction());
    data.put(PACKAGE, timelineSocialActionData.getPackageName());
    data.put(PUBLISHER, timelineSocialActionData.getPublisher());
    data.put(TITLE, timelineSocialActionData.getTitle());
    return data;
  }

  public void sendAppUpdateCardClickEvent(String cardType, String action, String socialAction,
      String packageName, String publisher) {
    analyticsManager.logEvent(
        createAppUpdateCardData(cardType, action, socialAction, packageName, publisher),
        CARD_ACTION, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  @NonNull private Map<String, Object> createAppUpdateCardData(String cardType, String action,
      String socialAction, String packageName, String publisher) {
    Map<String, Object> data = new HashMap<>();
    data.put(CARD_TYPE, cardType);
    data.put(ACTION, action);
    data.put(SOCIAL_ACTION, socialAction);
    data.put(PACKAGE, packageName);
    data.put(PUBLISHER, publisher);
    data.put(TITLE, BLANK);
    return data;
  }

  public void sendMediaCardClickEvent(String cardType, String title, String publisher,
      String action, String socialAction) {
    analyticsManager.logEvent(
        createArticleCardData(cardType, title, publisher, action, socialAction), CARD_ACTION,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  @NonNull
  private Map<String, Object> createArticleCardData(String cardType, String title, String publisher,
      String action, String socialAction) {
    Map<String, Object> data = new HashMap<>();
    data.put(CARD_TYPE, cardType);
    data.put(ACTION, action);
    data.put(SOCIAL_ACTION, socialAction);
    data.put(PACKAGE, BLANK);
    data.put(PUBLISHER, publisher);
    data.put(TITLE, title);
    return data;
  }

  public void sendRecommendationCardClickEvent(String cardType, String action, String socialAction,
      String packageName, String publisher) {
    analyticsManager.logEvent(
        createRecommendationCardData(cardType, action, socialAction, packageName, publisher),
        CARD_ACTION, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  @NonNull private Map<String, Object> createRecommendationCardData(String cardType, String action,
      String socialAction, String packageName, String publisher) {
    Map<String, Object> data = new HashMap<>();
    data.put(CARD_TYPE, cardType);
    data.put(ACTION, action);
    data.put(SOCIAL_ACTION, socialAction);
    data.put(PACKAGE, packageName);
    data.put(PUBLISHER, publisher);
    data.put(TITLE, BLANK);
    return data;
  }

  @NonNull private Map<String, Object> createNoTitleCardData(String cardType, String action,
      String socialAction, String packageName, String publisher) {
    Map<String, Object> data = new HashMap<>();
    data.put(CARD_TYPE, cardType);
    data.put(ACTION, action);
    data.put(SOCIAL_ACTION, socialAction);
    data.put(PACKAGE, packageName);
    data.put(PUBLISHER, publisher);
    data.put(TITLE, BLANK);
    return data;
  }

  public void sendSocialRecommendationClickEvent(String cardType, String action,
      String socialAction, String packageName, String publisher) {
    analyticsManager.logEvent(
        createNoTitleCardData(cardType, action, socialAction, packageName, publisher), CARD_ACTION,
        AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendStoreLatestAppsClickEvent(String cardType, String action, String socialAction,
      String packageName, String publisher) {
    analyticsManager.logEvent(
        createStoreLatestAppsData(cardType, action, socialAction, packageName, publisher),
        CARD_ACTION, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createStoreLatestAppsData(String cardType, String action,
      String socialAction, String packageName, String publisher) {
    Map<String, Object> data = new HashMap<>();
    data.put(CARD_TYPE, cardType);
    data.put(ACTION, action);
    data.put(SOCIAL_ACTION, socialAction);
    data.put(PACKAGE, packageName);
    data.put(PUBLISHER, publisher);
    data.put(TITLE, BLANK);
    return data;
  }

  public void sendTimelineTabOpened() {
    Map<String, Object> map = new HashMap<>();
    analyticsManager.logEvent(map, TIMELINE_OPENED, AnalyticsManager.Action.CLICK,
        getViewName(false));
    map.put(PREVIOUS_CONTEXT, getViewName(false));
    map.put(SOURCE, navigationTracker.getPreviousScreen());
    if (version != null) {
      map.put(TIMELINE_VERSION, version);
      flushTimelineTabOpenEvents(map);
    } else {
      openTimelineEventsData.add(map);
    }
  }

  public void sendTimelineTabOpenedFromShortcut() {
    Map<String, Object> map = new HashMap<>();
    analyticsManager.logEvent(map, TIMELINE_OPENED, AnalyticsManager.Action.CLICK, EXTERNAL);
    map.put(SOURCE, APPS_SHORTCUTS);
    map.put(PREVIOUS_CONTEXT, EXTERNAL);
    if (version != null) {
      map.put(TIMELINE_VERSION, version);
      flushTimelineTabOpenEvents(map);
    } else {
      openTimelineEventsData.add(map);
    }
  }

  public void sendFollowFriendsEvent() {
    analyticsManager.logEvent(null, FOLLOW_FRIENDS, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void sendOpenAppEvent(String cardType, String source, String packageName) {
    Map<String, Object> data = createAppData(cardType, source, packageName);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, OPEN_APP, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createAppData(String cardType, String source, String packageName) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    return createTimelineCardData(cardType, source, specific);
  }

  private Map<String, Object> createTimelineCardData(String cardType, String source,
      Map<String, String> specific) {
    final Map<String, Object> result = new HashMap<>();
    result.put("card_type", cardType);
    result.put("source", source);
    result.put("specific", specific);
    return decorateWithScreenHistory(result);
  }

  private Map<String, Object> decorateWithScreenHistory(Map<String, Object> result) {
    result.put(PREVIOUS_CONTEXT, navigationTracker.getPreviousViewName());
    ScreenTagHistory previousScreen = navigationTracker.getPreviousScreen();
    if (previousScreen != null && !previousScreen.getStore()
        .isEmpty()) {
      result.put(STORE, previousScreen.getStore());
    }
    return result;
  }

  private Map<String, Object> createStoreAppData(String cardType, String source, String packageName,
      String store) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    specific.put("store", store);
    return createTimelineCardData(cardType, source, specific);
  }

  public void sendRecommendedOpenAppEvent(String cardType, String source, String basedOnPackageName,
      String packageName) {
    Map<String, Object> data =
        createBasedOnAppData(cardType, source, packageName, basedOnPackageName);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, OPEN_APP, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createBasedOnAppData(String cardType, String source,
      String packageName, String basedOnPackageName) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    specific.put("based_on", basedOnPackageName);
    return createTimelineCardData(cardType, source, specific);
  }

  public void sendUpdateAppEvent(String cardType, String source, String packageName) {
    Map<String, Object> data = createAppData(cardType, source, packageName);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, UPDATE_APP, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendAppUpdateOpenStoreEvent(String cardType, String source, String packageName,
      String store) {
    Map<String, Object> data = createStoreAppData(cardType, source, packageName, store);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, OPEN_STORE, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendOpenStoreEvent(String cardType, String source, String store) {
    Map<String, Object> data = createStoreData(cardType, source, store);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, OPEN_STORE, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createStoreData(String cardType, String source, String store) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("store", store);
    return createTimelineCardData(cardType, source, specific);
  }

  public void sendOpenArticleEvent(String cardType, String source, String url, String packageName) {
    Map<String, Object> data = createArticleData(cardType, source, url, packageName);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, OPEN_ARTICLE, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createArticleData(String cardType, String source, String url,
      String packageName) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("url", url);
    specific.put("app", packageName);
    return createTimelineCardData(cardType, source, specific);
  }

  public void sendLikeEvent(CardTouchEvent event) {
    HashMap<String, Object> data =
        parseEventData(event, true, EventErrorHandler.GenericErrorEvent.OK);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, LIKE, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendErrorLikeEvent(CardTouchEvent event, EventErrorHandler.GenericErrorEvent error) {
    HashMap<String, Object> data = parseEventData(event, false, error);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, LIKE, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendOpenBlogEvent(String cardType, String source, String url, String packageName) {
    Map<String, Object> data = createArticleData(cardType, source, url, packageName);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, OPEN_BLOG, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendOpenVideoEvent(String cardType, String source, String url, String packageName) {
    Map<String, Object> data = createVideoAppData(cardType, source, url, packageName);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, OPEN_VIDEO, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createVideoAppData(String cardType, String source, String url,
      String packageName) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    specific.put("url", url);
    return createTimelineCardData(cardType, source, specific);
  }

  public void sendOpenChannelEvent(String cardType, String source, String url, String packageName) {
    Map<String, Object> data = createVideoAppData(cardType, source, url, packageName);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, OPEN_CHANNEL, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  private Map<String, Object> createMapData(String key, String value) {
    final Map<String, Object> data = new HashMap<>();
    data.put(key, value);
    return data;
  }

  public void sendPopularAppOpenUserStoreEvent(String cardType, String source, String packageName,
      String store) {
    Map<String, Object> data = createStoreAppData(cardType, source, packageName, store);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, OPEN_STORE, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendOpenStoreProfileEvent(CardTouchEvent touchEvent) {
    HashMap<String, Object> data =
        parseEventData(touchEvent, true, EventErrorHandler.GenericErrorEvent.OK);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, OPEN_STORE_PROFILE, AnalyticsManager.Action.CLICK,
        getViewName(true));
  }

  public void sendClickOnPostHeaderEvent(CardTouchEvent cardTouchEvent) {
    final Post post = cardTouchEvent.getCard();
    final CardType postType = post.getType();

    if (postType.isSocial()) {
      SocialHeaderCardTouchEvent socialHeaderCardTouchEvent =
          ((SocialHeaderCardTouchEvent) cardTouchEvent);
      logFlurryEvent(socialHeaderCardTouchEvent.getCard()
          .getType()
          .name(), BLANK, BLANK, socialHeaderCardTouchEvent.getStoreName(), OPEN_STORE);
      sendOpenStoreProfileEvent(socialHeaderCardTouchEvent);
    } else if (postType.equals(CardType.ARTICLE)) {
      Media card = (Media) post;
      sendOpenBlogEvent(postType.name(), card.getPublisherName(), card.getPublisherLink()
          .getUrl(), card.getRelatedApp()
          .getPackageName());
      sendMediaCardClickEvent(postType.name(), card.getMediaTitle(), card.getPublisherName(),
          OPEN_ARTICLE_HEADER, "(blank)");
      logFlurryEvent(postType.name(), BLANK, card.getMediaTitle(), card.getPublisherName(),
          OPEN_ARTICLE_HEADER);
    } else if (postType.equals(CardType.VIDEO)) {
      Media card = (Media) post;
      sendOpenChannelEvent(postType.name(), card.getPublisherName(), card.getPublisherLink()
          .getUrl(), card.getRelatedApp()
          .getPackageName());
      sendMediaCardClickEvent(postType.name(), card.getMediaTitle(), card.getPublisherName(),
          OPEN_VIDEO_HEADER, BLANK);
      logFlurryEvent(postType.name(), BLANK, card.getMediaTitle(), card.getPublisherName(),
          OPEN_VIDEO_HEADER);
    } else if (postType.equals(CardType.STORE)) {
      StoreLatestApps card = ((StoreLatestApps) post);
      logFlurryEvent(postType.name(), BLANK, BLANK, card.getStoreName(), OPEN_STORE);
      sendStoreLatestAppsClickEvent(postType.name(), OPEN_STORE, BLANK, BLANK, card.getStoreName());
    } else if (postType.equals(CardType.UPDATE)) {
      AppUpdate card = ((AppUpdate) post);
      logFlurryEvent(postType.name(), card.getPackageName(), BLANK, card.getStoreName(),
          OPEN_STORE);
      sendAppUpdateCardClickEvent(postType.name(), OPEN_STORE, BLANK, card.getPackageName(),
          card.getStoreName());
      sendAppUpdateOpenStoreEvent(postType.name(), SOURCE_APTOIDE, card.getPackageName(),
          card.getStoreName());
    } else if (postType.equals(CardType.POPULAR_APP)) {
      PopularAppTouchEvent popularAppTouchEvent = (PopularAppTouchEvent) cardTouchEvent;
      logFlurryEvent(popularAppTouchEvent.getCard()
              .getType()
              .name(), ((PopularApp) popularAppTouchEvent.getCard()).getPackageName(), BLANK,
          String.valueOf(popularAppTouchEvent.getUserId()), OPEN_STORE);
      sendPopularAppOpenUserStoreEvent(postType.name(), SOURCE_APTOIDE,
          ((PopularApp) popularAppTouchEvent.getCard()).getPackageName(),
          String.valueOf(popularAppTouchEvent.getUserId()));
    }
  }

  public void logFlurryEvent(String cardType, String packageName, String title, String publisher,
      String action) {
    HashMap<String, Object> map = new HashMap<>();

    map.put(ACTION, action);
    map.put(PACKAGE, packageName);
    map.put(TITLE, title);
    map.put(PUBLISHER, publisher);
    String eventName = cardType + "_" + APPS_TIMELINE_EVENT;
    analyticsManager.logEvent(map, eventName, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendClickOnPostBodyEvent(CardTouchEvent cardTouchEvent) {
    final Post post = cardTouchEvent.getCard();
    final CardType postType = post.getType();

    if (postType.isMedia()) {
      if (postType.isArticle()) {
        Media media = (Media) post;
        logFlurryEvent(media.getType()
            .name(), BLANK, media.getMediaTitle(), media.getPublisherName(), OPEN_ARTICLE);
        sendOpenArticleEvent(media.getType()
            .name(), media.getPublisherName(), media.getMediaLink()
            .getUrl(), media.getRelatedApp()
            .getPackageName());
        sendMediaCardClickEvent(media.getType()
            .name(), media.getMediaTitle(), media.getPublisherName(), OPEN_ARTICLE, BLANK);
      } else if (postType.isVideo()) {
        Media media = (Media) post;
        logFlurryEvent(media.getType()
            .name(), BLANK, media.getMediaTitle(), media.getPublisherName(), OPEN_VIDEO);
        sendOpenVideoEvent(media.getType()
            .name(), media.getPublisherName(), media.getMediaLink()
            .getUrl(), media.getRelatedApp()
            .getPackageName());
        sendMediaCardClickEvent(media.getType()
            .name(), media.getMediaTitle(), media.getPublisherName(), OPEN_VIDEO, BLANK);
      }
    } else if (postType.equals(CardType.RECOMMENDATION) || postType.equals(CardType.SIMILAR)) {
      Recommendation card = (Recommendation) post;
      logFlurryEvent(card.getType()
          .name(), card.getPackageName(), BLANK, card.getPublisherName(), OPEN_APP_VIEW);
      sendRecommendationCardClickEvent(card.getType()
          .name(), OPEN_APP_VIEW, BLANK, card.getPackageName(), card.getPublisherName());
      sendRecommendedOpenAppEvent(card.getType()
          .name(), SOURCE_APTOIDE, card.getRelatedToPackageName(), card.getPackageName());
    } else if (postType.equals(CardType.STORE)) {
      StoreAppCardTouchEvent storeAppCardTouchEvent = (StoreAppCardTouchEvent) cardTouchEvent;
      if (storeAppCardTouchEvent.getCard() instanceof StoreLatestApps) {
        logFlurryEvent(storeAppCardTouchEvent.getCard()
                .getType()
                .name(), storeAppCardTouchEvent.getPackageName(), BLANK,
            ((StoreLatestApps) storeAppCardTouchEvent.getCard()).getStoreName(), OPEN_APP_VIEW);
        sendOpenAppEvent(postType.name(), SOURCE_APTOIDE,
            ((StoreAppCardTouchEvent) cardTouchEvent).getPackageName());
      }
      sendStoreLatestAppsClickEvent(postType.name(), OPEN_APP_VIEW, BLANK,
          storeAppCardTouchEvent.getPackageName(), ((StoreLatestApps) post).getStoreName());
    } else if (postType.equals(CardType.SOCIAL_STORE) || postType.equals(
        CardType.AGGREGATED_SOCIAL_STORE)) {
      if (cardTouchEvent instanceof StoreAppCardTouchEvent) {
        logFlurryEvent(postType.name(), ((StoreAppCardTouchEvent) cardTouchEvent).getPackageName(),
            BLANK, ((StoreLatestApps) post).getStoreName(), OPEN_APP_VIEW);
        sendOpenAppEvent(postType.name(), SOURCE_APTOIDE,
            ((StoreAppCardTouchEvent) cardTouchEvent).getPackageName());
      } else if (cardTouchEvent instanceof StoreCardTouchEvent) {
        if (post instanceof StoreLatestApps) {
          logFlurryEvent(postType.name(), BLANK, BLANK, ((StoreLatestApps) post).getStoreName(),
              OPEN_STORE);
          sendOpenStoreEvent(postType.name(), SOURCE_APTOIDE,
              ((StoreLatestApps) post).getStoreName());
        }
      }
    } else if (postType.equals(CardType.UPDATE)) {
      AppUpdate card = (AppUpdate) post;
      if (cardTouchEvent instanceof AppUpdateCardTouchEvent) {
        logFlurryEvent(postType.name(), card.getPackageName(), BLANK, card.getStoreName(),
            UPDATE_APP);
        sendAppUpdateCardClickEvent(card.getType()
            .name(), UPDATE_APP, BLANK, card.getPackageName(), card.getStoreName());
        sendUpdateAppEvent(card.getType()
            .name(), SOURCE_APTOIDE, card.getPackageName());
      } else {
        logFlurryEvent(card.getType()
            .name(), card.getPackageName(), BLANK, card.getStoreName(), OPEN_APP_VIEW);
        sendRecommendationCardClickEvent(card.getType()
            .name(), OPEN_APP_VIEW, BLANK, card.getPackageName(), card.getStoreName());
        sendRecommendedOpenAppEvent(card.getType()
            .name(), SOURCE_APTOIDE, BLANK, card.getPackageName());
      }
    } else if (postType.equals(CardType.POPULAR_APP)) {
      PopularApp card = (PopularApp) post;
      logFlurryEvent(postType.name(), card.getPackageName(), BLANK, BLANK, OPEN_APP_VIEW);
      sendOpenAppEvent(card.getType()
          .name(), SOURCE_APTOIDE, card.getPackageName());
    } else if (postType.equals(CardType.SOCIAL_RECOMMENDATION) || postType.equals(
        CardType.SOCIAL_INSTALL) || postType.equals(CardType.SOCIAL_POST_RECOMMENDATION)) {
      RatedRecommendation card = (RatedRecommendation) post;
      logFlurryEvent(postType.name(), card.getPackageName(), BLANK, BLANK, OPEN_APP_VIEW);
      sendSocialRecommendationClickEvent(card.getType()
          .name(), OPEN_APP_VIEW, BLANK, card.getPackageName(), card.getPoster()
          .getPrimaryName());
      sendOpenAppEvent(card.getType()
          .name(), SOURCE_APTOIDE, card.getPackageName());
    } else if (postType.equals(CardType.AGGREGATED_SOCIAL_INSTALL) || postType.equals(
        CardType.AGGREGATED_SOCIAL_APP)) {
      AggregatedRecommendation card = (AggregatedRecommendation) post;
      logFlurryEvent(postType.name(), card.getPackageName(), BLANK, BLANK, OPEN_APP_VIEW);
      sendOpenAppEvent(card.getType()
          .name(), SOURCE_APTOIDE, card.getPackageName());
    }
  }

  private Map<String, Object> createScrollingEventData(int position) {
    final Map<String, Object> eventMap = new HashMap<>();
    eventMap.put(TIMELINE_VERSION, version);
    eventMap.put("position", position);
    return eventMap;
  }

  public void sendCommentEvent(CardTouchEvent event) {
    HashMap<String, Object> data =
        parseEventData(event, true, EventErrorHandler.GenericErrorEvent.OK);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, COMMENT, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendErrorCommentEvent(CardTouchEvent event,
      EventErrorHandler.GenericErrorEvent error) {
    HashMap<String, Object> data = parseEventData(event, false, error);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, COMMENT, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendShareEvent(CardTouchEvent event) {
    HashMap<String, Object> data =
        parseEventData(event, true, EventErrorHandler.GenericErrorEvent.OK);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, SHARE, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendErrorShareEvent(CardTouchEvent event, EventErrorHandler.GenericErrorEvent error) {
    HashMap<String, Object> data = parseEventData(event, false, error);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, SHARE, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendShareCompleted(ShareEvent event) {
    HashMap<String, Object> data =
        parseShareCompletedEventData(event, true, EventErrorHandler.ShareErrorEvent.OK);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, SHARE_SEND, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendErrorShareCompleted(ShareEvent event, EventErrorHandler.ShareErrorEvent error) {
    HashMap<String, Object> data = parseShareCompletedEventData(event, false, error);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, SHARE_SEND, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendCommentCompletedSuccess(Post post, int position) {
    HashMap<String, Object> data = parseCommentCompleted(post, position, true);
    analyticsManager.logEvent(data, COMMENT_SEND, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendCommentCompletedError(Post post, int position) {
    HashMap<String, Object> data = parseCommentCompleted(post, position, false);
    analyticsManager.logEvent(data, COMMENT_SEND, AnalyticsManager.Action.CLICK, getViewName(true));
  }

  public void sendFabClicked() {
    HashMap<String, Object> data = new HashMap<>();
    String previousContext = null;
    String store = null;
    if (navigationTracker.getPreviousScreen() != null) {
      previousContext = navigationTracker.getPreviousScreen()
          .getFragment();
      store = navigationTracker.getPreviousScreen()
          .getStore();
    }
    data.put("previous_context", previousContext);
    data.put("store", store);
    data.put(TIMELINE_VERSION, version);
    analyticsManager.logEvent(data, FAB, AnalyticsManager.Action.CLICK, getViewName(false));
  }

  public void scrollToPosition(int position) {
    analyticsManager.logEvent(createScrollingEventData(position), SCROLLING_EVENT,
        AnalyticsManager.Action.SCROLL, getViewName(true));
  }

  public Completable setPostRead(String cardId, String name) {
    return readPostsPersistence.addPost(cardId, name);
  }

  public HashMap<String, Object> parseCommentCompleted(Post post, int position, boolean status) {

    final CardType postType = post.getType();

    HashMap<String, Object> data = new HashMap<>();
    HashMap<String, Object> result = new HashMap<>();
    String previousContext = null;
    String store = null;
    data.put("card_type", post.getType());
    data.put("position", position);

    if (navigationTracker.getPreviousScreen() != null) {
      previousContext = navigationTracker.getPreviousScreen()
          .getFragment();
      store = navigationTracker.getPreviousScreen()
          .getStore();
    }

    data.put("previous_context", previousContext);
    data.put("store", store);

    result.put("status", status ? "success" : "fail");

    data = handleCardType(postType, data, post);
    data.put("result", result);

    return data;
  }

  public HashMap<String, Object> parseEventData(CardTouchEvent event, boolean status,
      EventErrorHandler.GenericErrorEvent errorCode) {
    final Post post = event.getCard();
    final CardType postType = post.getType();
    EventErrorHandler errorHandler = new EventErrorHandler();
    HashMap<String, Object> data = new HashMap<>();
    HashMap<String, Object> result = new HashMap<>();
    HashMap<String, Object> error = new HashMap<>();
    String previousContext = null;
    String store = null;
    data.put("card_type", post.getType());
    data.put("position", event.getPosition());

    result.put("status", status ? "success" : "fail");

    if (result.get("status")
        .equals("fail")) {
      error = errorHandler.handleGenericErrorParsing(errorCode);
      result.put("error", error);
    }

    data = handleCardType(postType, data, post);
    data.put("result", result);
    return data;
  }

  public HashMap<String, Object> parseShareCompletedEventData(ShareEvent event, boolean status,
      EventErrorHandler.ShareErrorEvent errorCode) {
    final Post post = event.getPost();
    final CardType postType = post.getType();
    HashMap<String, Object> data = new HashMap<>();
    HashMap<String, Object> error = new HashMap<>();
    HashMap<String, Object> result = new HashMap<>();
    EventErrorHandler errorHandler = new EventErrorHandler();
    String previousContext = null;
    String store = null;
    data.put("card_type", post.getType());

    if (navigationTracker.getPreviousScreen() != null) {
      store = navigationTracker.getPreviousScreen()
          .getStore();
      previousContext = navigationTracker.getPreviousViewName();
    }

    data.put("previous_context", previousContext);
    data.put("store", store);

    result.put("status", status ? "success" : "fail");

    if (result.get("status")
        .equals("fail")) {
      error = errorHandler.handleShareErrorParsing(errorCode);
      result.put("error", error);
    }

    data = handleCardType(postType, data, post);

    data.put("result", result);
    return data;
  }

  public HashMap<String, Object> handleCardType(CardType postType, HashMap<String, Object> data,
      Post post) {

    HashMap<String, Object> specific = new HashMap<>();
    if (postType.isMedia()) {
      Media card = (Media) post;
      data.put("source", card.getPublisherName());
      specific.put("app", card.getRelatedApp()
          .getPackageName());
      specific.put("url", card.getMediaLink()
          .getUrl());
      data.put("specific", specific);
    } else if (postType.equals(CardType.RECOMMENDATION)
        || postType.equals(CardType.SOCIAL_POST_RECOMMENDATION)
        || postType.equals(CardType.SOCIAL_RECOMMENDATION)
        || postType.equals(CardType.SIMILAR)
        || postType.equals(CardType.SOCIAL_INSTALL)
        || postType.equals(CardType.AGGREGATED_SOCIAL_INSTALL)) {
      if (post instanceof RatedRecommendation) {
        RatedRecommendation card = (RatedRecommendation) post;
        if (card.getPoster()
            .getStore() != null) {
          data.put("source", card.getPoster()
              .getStore()
              .getName());
        } else {
          data.put("source", card.getPoster()
              .getPrimaryName());
        }
        specific.put("app", card.getPackageName());
        data.put("specific", specific);
      } else {
        Recommendation card = (Recommendation) post;
        data.put("source", card.getPublisherName());
        specific.put("app", card.getPackageName());
        data.put("specific", specific);
      }
    } else if (postType.equals(CardType.UPDATE)) {
      AppUpdate card = (AppUpdate) post;
      data.put("source", SOURCE_APTOIDE);
      specific.put("app", card.getPackageName());
      data.put("specific", specific);
    } else if (postType.equals(CardType.STORE)
        || postType.equals(CardType.SOCIAL_STORE)
        || postType.equals(CardType.AGGREGATED_SOCIAL_STORE)) {
      data.put("source", SOURCE_APTOIDE);
    }
    return data;
  }

  public void setVersion(String version) {
    this.version = version;
    if (openTimelineEventsData.size() > 0) {
      for (Map<String, Object> data : openTimelineEventsData) {
        data.put(TIMELINE_VERSION, version);
        flushTimelineTabOpenEvents(data);
      }
    }
  }

  private void flushTimelineTabOpenEvents(Map<String, Object> data) {
    analyticsManager.logEvent(decorateWithScreenHistory(data), OPEN_TIMELINE_EVENT,
        AnalyticsManager.Action.CLICK, getViewName(false));
  }

  private String getViewName(boolean isCurrent) {
    return navigationTracker.getViewName(isCurrent);
  }

  public void sendRecommendedAppImpressionEvent(String packageName) {
    final Map<String, Object> data = new HashMap<>();
    data.put("type", "recommend app");
    data.put("fragment", getViewName(true));
    data.put("package_name", packageName);

    analyticsManager.logEvent(data, MESSAGE_IMPRESSION, AnalyticsManager.Action.IMPRESSION,
        navigationTracker.getViewName(true));
  }

  public void sendRecommendedAppInteractEvent(String packageName, String action) {
    final Map<String, Object> data = new HashMap<>();
    data.put("type", "recommend app");
    data.put("fragment", getViewName(true));
    data.put("package_name", packageName);
    data.put("action", action);

    analyticsManager.logEvent(data, MESSAGE_INTERACT, AnalyticsManager.Action.CLICK,
        navigationTracker.getViewName(true));
  }
}
