/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.timeline.Recommendation;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AppsTimeline.BLANK;

/**
 * Created by marcelobenites on 7/8/16.
 */
public class RecommendationDisplayable extends CardDisplayable {

  public static final String CARD_TYPE_NAME = "RECOMMENDATION";
  @Getter private int avatarResource;
  @Getter private int titleResource;
  @Getter private long appId;
  @Getter private String packageName;
  @Getter private String appName;
  @Getter private String appIcon;
  @Getter private String abUrl;

  private List<String> similarAppsNames;
  private List<String> similarPackageNames;
  private Date date;
  private Date timestamp;
  private DateCalculator dateCalculator;
  private SpannableFactory spannableFactory;
  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;
  @Getter private float appRating;
  @Getter private Long appStoreId;

  public RecommendationDisplayable() {
  }

  public RecommendationDisplayable(Recommendation recommendation, int avatarResource,
      int titleResource, long appId, String packageName, String appName, String appIcon,
      String abUrl, List<String> similarAppsNames, List<String> similarPackageNames, Date date,
      Date timestamp, DateCalculator dateCalculator, SpannableFactory spannableFactory,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      WindowManager windowManager) {
    super(recommendation, timelineAnalytics, windowManager);
    this.avatarResource = avatarResource;
    this.titleResource = titleResource;
    this.appId = appId;
    this.packageName = packageName;
    this.appName = appName;
    this.appIcon = appIcon;
    this.abUrl = abUrl;
    this.similarAppsNames = similarAppsNames;
    this.similarPackageNames = similarPackageNames;
    this.date = date;
    this.timestamp = timestamp;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
    this.appRating = recommendation.getRecommendedApp()
        .getStats()
        .getRating()
        .getAvg();
    this.appStoreId = recommendation.getRecommendedApp()
        .getStore()
        .getId();
  }

  public static Displayable from(Recommendation recommendation, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, WindowManager windowManager) {
    final List<String> similarAppsNames = new ArrayList<>();
    final List<String> similarPackageNames = new ArrayList<>();

    for (App similarApp : recommendation.getSimilarApps()) {
      similarAppsNames.add(similarApp.getName());
      similarPackageNames.add(similarApp.getPackageName());
    }

    String abTestingURL = null;

    if (recommendation.getAb() != null
        && recommendation.getAb()
        .getConversion() != null
        && recommendation.getAb()
        .getConversion()
        .getUrl() != null) {
      abTestingURL = recommendation.getAb()
          .getConversion()
          .getUrl();
    }

    return new RecommendationDisplayable(recommendation, Application.getConfiguration()
        .getIcon(), R.string.timeline_title_card_title_recommend_present_singular,
        recommendation.getRecommendedApp()
            .getId(), recommendation.getRecommendedApp()
        .getPackageName(), recommendation.getRecommendedApp()
        .getName(), recommendation.getRecommendedApp()
        .getIcon(), abTestingURL, similarAppsNames, similarPackageNames,
        recommendation.getRecommendedApp()
            .getUpdated(), recommendation.getTimestamp(), dateCalculator, spannableFactory,
        timelineAnalytics, socialRepository, windowManager);
  }

  public Spannable getStyledTitle(Context context) {
    String aptoide = Application.getConfiguration()
        .getMarketName();
    return spannableFactory.createColorSpan(getTitle(context.getResources()),
        ContextCompat.getColor(context, R.color.appstimeline_recommends_title), aptoide);
  }

  public String getTitle(Resources resources) {
    return AptoideUtils.StringU.getFormattedString(titleResource, resources,
        Application.getConfiguration()
            .getMarketName());
  }

  public Spannable getAppText(Context context) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
        ContextCompat.getColor(context, R.color.appstimeline_grey), "");
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public String getTimeSinceRecommendation(Context context) {
    return dateCalculator.getTimeSinceDate(context, timestamp);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_recommendation;
  }

  public void sendRecommendedOpenAppEvent() {
    timelineAnalytics.sendRecommendedOpenAppEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        getSimilarAppPackageName(), getPackageName());
  }

  public String getSimilarAppPackageName() {
    if (similarPackageNames != null && similarPackageNames.size() != 0) {
      return similarPackageNames.get(0);
    }
    return "";
  }

  public void sendRecommendationCardClickEvent(String action, String socialAction,
      Resources resources) {
    timelineAnalytics.sendRecommendationCardClickEvent(CARD_TYPE_NAME, action, socialAction,
        getPackageName(), getTitle(resources));
  }

  public String getSimilarAppName() {
    if (similarPackageNames != null && similarAppsNames.size() != 0) {
      return similarAppsNames.get(0);
    }
    return "";
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback,
      Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), getAppStoreId(), privacyResult,
        shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(),
            getTitle(resources), BLANK));
  }

  @Override
  public void share(String cardId, ShareCardCallback shareCardCallback, Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), getAppStoreId(), shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(),
            getTitle(resources), BLANK));
  }

  @Override public void like(Context context, String cardType, int rating, Resources resources) {
    like(null, getTimelineCard().getCardId(), cardType, rating, resources);
  }

  @Override public void like(Context context, String cardId, String cardType, int rating,
      Resources resources) {
    socialRepository.like(cardId, cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, getPackageName(),
            getTitle(resources), BLANK));
  }
}
