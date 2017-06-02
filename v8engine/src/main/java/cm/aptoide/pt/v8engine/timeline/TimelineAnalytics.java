package cm.aptoide.pt.v8engine.timeline;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.AptoideAnalytics;
import cm.aptoide.pt.v8engine.analytics.events.AptoideEvent;
import cm.aptoide.pt.v8engine.analytics.events.FacebookEvent;
import com.facebook.appevents.AppEventsLogger;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by jdandrade on 27/10/2016.
 */
public class TimelineAnalytics extends AptoideAnalytics {

  public static final String SOURCE_APTOIDE = "APTOIDE";

  public static final String SOCIAL_CARD_ACTION_SHARE_CONTINUE = "Continue";
  public static final String SOCIAL_CARD_ACTION_SHARE_CANCEL = "Cancel";
  private static final String CARD_TYPE = "card_type";
  private static final String ACTION = "action";
  private static final String SOCIAL_ACTION = "social_action";
  private static final String PACKAGE = "package_name";
  private static final String PUBLISHER = "publisher";
  private static final String TITLE = "title";
  private static final String OPEN_ARTICLE = "OPEN_ARTICLE";
  private static final String OPEN_BLOG = "OPEN_BLOG";
  private static final String OPEN_VIDEO = "OPEN_VIDEO";
  private static final String OPEN_CHANNEL = "OPEN_CHANNEL";
  private static final String OPEN_STORE = "OPEN_STORE";
  private static final String OPEN_APP = "OPEN_APP";
  private static final String UPDATE_APP = "UPDATE_APP";
  private static final String FOLLOW_FRIENDS = "Apps_Timeline_Follow_Friends";
  private static final String TIMELINE_OPENED = "Apps_Timeline_Open";
  private static final String SOCIAL_CARD_PREVIEW = "Apps_Timeline_Social_Card_Preview";
  private static final String CARD_ACTION = "Apps_Timeline_Card_Action";
  private static final String BLANK = "(blank)";
  private final Analytics analytics;
  private final AppEventsLogger facebook;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;

  public TimelineAnalytics(Analytics analytics, AppEventsLogger facebook,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    this.analytics = analytics;
    this.facebook = facebook;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
  }

  public void sendSocialCardPreviewActionEvent(String value) {
    analytics.sendEvent(
        new FacebookEvent(facebook, SOCIAL_CARD_PREVIEW, createBundleData(ACTION, value)));
  }

  public void sendSocialActionEvent(TimelineSocialActionData timelineSocialActionData) {
    analytics.sendEvent(new FacebookEvent(facebook, CARD_ACTION,
        createSocialActionEventData(timelineSocialActionData)));
  }

  private Bundle createSocialActionEventData(TimelineSocialActionData timelineSocialActionData) {
    Bundle bundle = new Bundle();
    bundle.putString(CARD_TYPE, timelineSocialActionData.getCardType());
    bundle.putString(ACTION, timelineSocialActionData.getAction());
    bundle.putString(SOCIAL_ACTION, timelineSocialActionData.getSocialAction());
    bundle.putString(PACKAGE, timelineSocialActionData.getPackageName());
    bundle.putString(PUBLISHER, timelineSocialActionData.getPublisher());
    bundle.putString(TITLE, timelineSocialActionData.getTitle());
    return bundle;
  }

  public void sendAppUpdateCardClickEvent(String cardType, String action, String socialAction,
      String packageName, String publisher) {
    analytics.sendEvent(new FacebookEvent(facebook, CARD_ACTION,
        createAppUpdateCardData(cardType, action, socialAction, packageName, publisher)));
  }

  @NonNull
  private Bundle createAppUpdateCardData(String cardType, String action, String socialAction,
      String packageName, String publisher) {
    Bundle bundle = new Bundle();
    bundle.putString(CARD_TYPE, cardType);
    bundle.putString(ACTION, action);
    bundle.putString(SOCIAL_ACTION, socialAction);
    bundle.putString(PACKAGE, packageName);
    bundle.putString(PUBLISHER, publisher);
    bundle.putString(TITLE, BLANK);
    return bundle;
  }

  public void sendArticleWidgetCardClickEvent(String cardType, String title, String publisher,
      String action, String socialAction) {
    analytics.sendEvent(new FacebookEvent(facebook, CARD_ACTION,
        createArticleCardData(cardType, title, publisher, action, socialAction)));
  }

  @NonNull private Bundle createArticleCardData(String cardType, String title, String publisher,
      String action, String socialAction) {
    Bundle bundle = new Bundle();
    bundle.putString(CARD_TYPE, cardType);
    bundle.putString(ACTION, action);
    bundle.putString(SOCIAL_ACTION, socialAction);
    bundle.putString(PACKAGE, BLANK);
    bundle.putString(PUBLISHER, publisher);
    bundle.putString(TITLE, title);
    return bundle;
  }

  public void sendRecommendationCardClickEvent(String cardType, String action, String socialAction,
      String packageName, String publisher) {
    analytics.sendEvent(new FacebookEvent(facebook, CARD_ACTION,
        createRecommendationCardData(cardType, action, socialAction, packageName, publisher)));
  }

  @NonNull
  private Bundle createRecommendationCardData(String cardType, String action, String socialAction,
      String packageName, String publisher) {
    Bundle bundle = new Bundle();
    bundle.putString(CARD_TYPE, cardType);
    bundle.putString(ACTION, action);
    bundle.putString(SOCIAL_ACTION, socialAction);
    bundle.putString(PACKAGE, packageName);
    bundle.putString(PUBLISHER, publisher);
    bundle.putString(TITLE, BLANK);
    return bundle;
  }

  public void sendSocialArticleClickEvent(String cardType, String title, String publisher,
      String action, String socialAction) {
    analytics.sendEvent(new FacebookEvent(facebook, CARD_ACTION,
        createArticleCardData(cardType, title, publisher, action, socialAction)));
  }

  public void sendSocialInstallClickEvent(String cardType, String action, String socialAction,
      String packageName, String publisher) {
    analytics.sendEvent(new FacebookEvent(facebook, CARD_ACTION,
        createNoTitleCardData(cardType, action, socialAction, packageName, publisher)));
  }

  @NonNull private Bundle createNoTitleCardData(String cardType, String action, String socialAction,
      String packageName, String publisher) {
    Bundle bundle = new Bundle();
    bundle.putString(CARD_TYPE, cardType);
    bundle.putString(ACTION, action);
    bundle.putString(SOCIAL_ACTION, socialAction);
    bundle.putString(PACKAGE, packageName);
    bundle.putString(PUBLISHER, publisher);
    bundle.putString(TITLE, BLANK);
    return bundle;
  }

  public void sendSocialRecommendationClickEvent(String cardType, String action,
      String socialAction, String packageName, String publisher) {
    analytics.sendEvent(new FacebookEvent(facebook, CARD_ACTION,
        createNoTitleCardData(cardType, action, socialAction, packageName, publisher)));
  }

  public void sendSocialLatestClickEvent(String cardType, String packageName, String action,
      String socialAction, String publisher) {
    analytics.sendEvent(new FacebookEvent(facebook, CARD_ACTION,
        createSocialLatestData(cardType, packageName, action, socialAction, publisher)));
  }

  private Bundle createSocialLatestData(String cardType, String packageName, String action,
      String socialAction, String publisher) {
    Bundle bundle = new Bundle();
    bundle.putString(CARD_TYPE, cardType);
    bundle.putString(ACTION, action);
    bundle.putString(SOCIAL_ACTION, socialAction);
    bundle.putString(PACKAGE, packageName);
    bundle.putString(PUBLISHER, publisher);
    bundle.putString(TITLE, BLANK);
    return bundle;
  }

  public void sendSocialVideoClickEvent(String cardType, String title, String action,
      String socialAction, String publisher) {
    analytics.sendEvent(new FacebookEvent(facebook, CARD_ACTION,
        createSocialVideoData(cardType, title, action, socialAction, publisher)));
  }

  private Bundle createSocialVideoData(String cardType, String title, String action,
      String socialAction, String publisher) {
    Bundle bundle = new Bundle();
    bundle.putString(CARD_TYPE, cardType);
    bundle.putString(ACTION, action);
    bundle.putString(SOCIAL_ACTION, socialAction);
    bundle.putString(PACKAGE, BLANK);
    bundle.putString(PUBLISHER, publisher);
    bundle.putString(TITLE, title);
    return bundle;
  }

  public void sendStoreLatestAppsClickEvent(String cardType, String action, String socialAction,
      String packageName, String publisher) {
    analytics.sendEvent(new FacebookEvent(facebook, CARD_ACTION,
        createStoreLatestAppsData(cardType, action, socialAction, packageName, publisher)));
  }

  private Bundle createStoreLatestAppsData(String cardType, String action, String socialAction,
      String packageName, String publisher) {
    Bundle bundle = new Bundle();
    bundle.putString(CARD_TYPE, cardType);
    bundle.putString(ACTION, action);
    bundle.putString(SOCIAL_ACTION, socialAction);
    bundle.putString(PACKAGE, packageName);
    bundle.putString(PUBLISHER, publisher);
    bundle.putString(TITLE, BLANK);
    return bundle;
  }

  public void sendVideoClickEvent(String cardType, String title, String action, String socialAction,
      String publisher) {
    analytics.sendEvent(new FacebookEvent(facebook, CARD_ACTION,
        createVideoData(cardType, title, action, socialAction, publisher)));
  }

  private Bundle createVideoData(String cardType, String title, String action, String socialAction,
      String publisher) {
    Bundle bundle = new Bundle();
    bundle.putString(CARD_TYPE, cardType);
    bundle.putString(ACTION, action);
    bundle.putString(SOCIAL_ACTION, socialAction);
    bundle.putString(PACKAGE, BLANK);
    bundle.putString(PUBLISHER, publisher);
    bundle.putString(TITLE, title);
    return bundle;
  }

  public void sendTimelineTabOpened() {
    analytics.sendEvent(new FacebookEvent(facebook, TIMELINE_OPENED));
  }

  public void sendFollowFriendsEvent() {
    analytics.sendEvent(new FacebookEvent(facebook, FOLLOW_FRIENDS));
  }

  public void sendOpenAppEvent(String cardType, String source, String packageName) {
    analytics.sendEvent(createEvent(OPEN_APP, createAppData(cardType, source, packageName)));
  }

  private AptoideEvent createEvent(String event, Map<String, Object> data) {
    return new AptoideEvent(data, event, "CLICK", "TIMELINE", bodyInterceptor, httpClient,
        converterFactory);
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
    return result;
  }

  public void sendStoreOpenAppEvent(String cardType, String source, String packageName,
      String store) {
    analytics.sendEvent(
        createEvent(OPEN_APP, createStoreAppData(cardType, source, packageName, store)));
  }

  private Map<String, Object> createStoreAppData(String cardType, String source, String packageName,
      String store) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    specific.put("store", store);
    return createTimelineCardData(cardType, source, specific);
  }

  public void sendSimilarOpenAppEvent(String cardType, String source, String packageName,
      String similarPackageName) {
    analytics.sendEvent(createEvent(OPEN_APP,
        createSimilarAppData(cardType, source, packageName, similarPackageName)));
  }

  private Map<String, Object> createSimilarAppData(String cardType, String source,
      String packageName, String similarPackageName) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    specific.put("similar_to", similarPackageName);
    return createTimelineCardData(cardType, source, specific);
  }

  public void sendRecommendedOpenAppEvent(String cardType, String source, String basedOnPackageName,
      String packageName) {
    analytics.sendEvent(createEvent(OPEN_APP,
        createBasedOnAppData(cardType, source, packageName, basedOnPackageName)));
  }

  private Map<String, Object> createBasedOnAppData(String cardType, String source,
      String packageName, String basedOnPackageName) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    specific.put("based_on", basedOnPackageName);
    return createTimelineCardData(cardType, source, specific);
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

  private Map<String, Object> createStoreData(String cardType, String source, String store) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("store", store);
    return createTimelineCardData(cardType, source, specific);
  }

  public void sendOpenArticleEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(
        createEvent(OPEN_ARTICLE, createArticleData(cardType, source, url, packageName)));
  }

  private Map<String, Object> createArticleData(String cardType, String source, String url,
      String packageName) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("url", url);
    specific.put("app", packageName);
    return createTimelineCardData(cardType, source, specific);
  }

  public void sendOpenBlogEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(
        createEvent(OPEN_BLOG, createArticleData(cardType, source, url, packageName)));
  }

  public void sendOpenVideoEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(
        createEvent(OPEN_VIDEO, createVideoAppData(cardType, source, url, packageName)));
  }

  private Map<String, Object> createVideoAppData(String cardType, String source, String url,
      String packageName) {
    final Map<String, String> specific = new HashMap<>();
    specific.put("app", packageName);
    specific.put("url", url);
    return createTimelineCardData(cardType, source, specific);
  }

  public void sendOpenChannelEvent(String cardType, String source, String url, String packageName) {
    analytics.sendEvent(
        createEvent(OPEN_CHANNEL, createVideoAppData(cardType, source, url, packageName)));
  }
}