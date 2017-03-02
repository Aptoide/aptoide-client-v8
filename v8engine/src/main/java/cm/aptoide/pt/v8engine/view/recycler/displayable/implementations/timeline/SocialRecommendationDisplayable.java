package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.SocialRecommendation;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.TimelineEvent;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineAnalytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import lombok.Getter;

/**
 * Created by jdandrade on 20/12/2016.
 */
public class SocialRecommendationDisplayable extends SocialCardDisplayable {

  @Getter private int avatarResource;
  @Getter private int titleResource;
  @Getter private Comment.User user;
  @Getter private long appId;
  @Getter private String packageName;
  @Getter private String appName;
  @Getter private String appIcon;
  @Getter private String abUrl;

  private TimelineAnalytics timelineAnalytics;
  private SpannableFactory spannableFactory;
  private SocialRepository socialRepository;

  public SocialRecommendationDisplayable() {
  }

  public SocialRecommendationDisplayable(SocialRecommendation socialRecommendation,
      int avatarResource, Store store, int titleResource, Comment.User user, long appId,
      String packageName, String appName, String appIcon, String abUrl, long numberOfLikes,
      long numberOfComments, TimelineAnalytics timelineAnalytics,
      SpannableFactory spannableFactory, SocialRepository socialRepository,
      DateCalculator dateCalculator) {
    super(socialRecommendation, numberOfLikes, numberOfComments, store,
        socialRecommendation.getUser(), socialRecommendation.getUserSharer(),
        socialRecommendation.getMy().isLiked(), socialRecommendation.getLikes(),
        socialRecommendation.getDate(), spannableFactory, dateCalculator);
    this.avatarResource = avatarResource;
    this.titleResource = titleResource;
    this.user = user;
    this.appId = appId;
    this.packageName = packageName;
    this.appName = appName;
    this.appIcon = appIcon;
    this.abUrl = abUrl;
    this.timelineAnalytics = timelineAnalytics;
    this.spannableFactory = spannableFactory;
    this.socialRepository = socialRepository;
  }

  public static Displayable from(SocialRecommendation socialRecommendation,
      TimelineAnalytics timelineAnalytics, SpannableFactory spannableFactory,
      SocialRepository socialRepository, DateCalculator dateCalculator) {

    //for (App similarApp : socialRecommendation.getSimilarApps()) {
    //  similarAppsNames.add(similarApp.getName());
    //  similarPackageNames.add(similarApp.getPackageName());
    //}

    String abTestingURL = null;

    if (socialRecommendation.getAb() != null
        && socialRecommendation.getAb().getConversion() != null
        && socialRecommendation.getAb().getConversion().getUrl() != null) {
      abTestingURL = socialRecommendation.getAb().getConversion().getUrl();
    }

    return new SocialRecommendationDisplayable(socialRecommendation,
        Application.getConfiguration().getIcon(), socialRecommendation.getStore(),
        R.string.displayable_social_timeline_recommendation_atptoide_team_recommends,
        socialRecommendation.getUser(), socialRecommendation.getApp().getId(),
        socialRecommendation.getApp().getPackageName(), socialRecommendation.getApp().getName(),
        socialRecommendation.getApp().getIcon(), abTestingURL,
        socialRecommendation.getStats().getLikes(), socialRecommendation.getStats().getComments(),
        timelineAnalytics, spannableFactory, socialRepository, dateCalculator);
  }

  public String getTitle() {
    return AptoideUtils.StringU.getFormattedString(titleResource,
        Application.getConfiguration().getMarketName());
  }

  public Spannable getAppText(Context context) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
        ContextCompat.getColor(context, R.color.appstimeline_grey), "");
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_social_recommendation;
  }

  public void sendClickEvent(TimelineEvent event) {
    timelineAnalytics.sendEvent(event);
  }

  @Override public void share(Context context, boolean privacyResult) {
    socialRepository.share(getTimelineCard(), context, privacyResult);
  }

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(getTimelineCard(), cardType, "", rating);
  }
}
