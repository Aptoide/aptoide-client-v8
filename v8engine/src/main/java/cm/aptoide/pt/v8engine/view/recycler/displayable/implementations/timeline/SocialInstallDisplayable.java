package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.SocialInstall;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ShareCardCallback;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineAnalytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import java.util.Date;
import lombok.Getter;

/**
 * Created by jdandrade on 15/12/2016.
 */
public class SocialInstallDisplayable extends SocialCardDisplayable {

  public static final String CARD_TYPE_NAME = "SOCIAL_INSTALL";
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

  public SocialInstallDisplayable() {
  }

  public SocialInstallDisplayable(SocialInstall socialInstall, int icon, Store store,
      int titleResource, Comment.User user, long appId, String packageName, String appName,
      String appIcon, String abTestingURL, long likes, long comments, Date date,
      TimelineAnalytics timelineAnalytics, SpannableFactory spannableFactory,
      SocialRepository socialRepository, DateCalculator dateCalculator) {
    super(socialInstall, likes, comments, store, socialInstall.getUser(),
        socialInstall.getUserSharer(), socialInstall.getMy().isLiked(), socialInstall.getLikes(),
        date, spannableFactory, dateCalculator, abTestingURL);
    this.avatarResource = icon;
    this.titleResource = titleResource;
    this.user = user;
    this.appId = appId;
    this.packageName = packageName;
    this.appName = appName;
    this.appIcon = appIcon;
    this.abUrl = abTestingURL;
    this.timelineAnalytics = timelineAnalytics;
    this.spannableFactory = spannableFactory;
    this.socialRepository = socialRepository;
  }

  public static Displayable from(SocialInstall socialInstall, TimelineAnalytics timelineAnalytics,
      SpannableFactory spannableFactory, SocialRepository socialRepository,
      DateCalculator dateCalculator) {

    //for (App similarApp : socialInstall.getSimilarApps()) {
    //  similarAppsNames.add(similarApp.getName());
    //  similarPackageNames.add(similarApp.getPackageName());
    //}

    String abTestingURL = null;

    if (socialInstall.getAb() != null
        && socialInstall.getAb().getConversion() != null
        && socialInstall.getAb().getConversion().getUrl() != null) {
      abTestingURL = socialInstall.getAb().getConversion().getUrl();
    }

    return new SocialInstallDisplayable(socialInstall, Application.getConfiguration().getIcon(),
        socialInstall.getStore(),
        R.string.displayable_social_timeline_recommendation_atptoide_team_recommends,
        socialInstall.getUser(), socialInstall.getApp().getId(),
        socialInstall.getApp().getPackageName(), socialInstall.getApp().getName(),
        socialInstall.getApp().getIcon(), abTestingURL, socialInstall.getStats().getLikes(),
        socialInstall.getStats().getComments(), socialInstall.getDate(), timelineAnalytics,
        spannableFactory, socialRepository, dateCalculator);
  }

  public String getTitle() {
    return AptoideUtils.StringU.getFormattedString(titleResource,
        Application.getConfiguration().getMarketName());
  }

  //public String getSimilarAppPackageName() {
  //  if (similarPackageNames.size() != 0) {
  //    return similarPackageNames.get(0);
  //  }
  //  return "";
  //}

  //public Spannable getSimilarAppsText(Context context) {
  //  StringBuilder similarAppsText = new StringBuilder(
  //      context.getString(R.string.displayable_social_timeline_recommendation_similar_to,
  //          similarAppsNames.get(0)));
  //  for (int i = 1; i < similarAppsNames.size() - 1; i++) {
  //    similarAppsText.append(", ");
  //    similarAppsText.append(similarAppsNames.get(i));
  //  }
  //  if (similarAppsNames.size() > 1) {
  //    similarAppsText.append(" ");
  //    similarAppsText.append(
  //        context.getString(R.string.displayable_social_timeline_recommendation_similar_and));
  //    similarAppsText.append(" ");
  //    similarAppsText.append(similarAppsNames.get(similarAppsNames.size() - 1));
  //  }
  //  return spannableFactory.createStyleSpan(similarAppsText.toString(), Typeface.BOLD,
  //      similarAppsNames.toArray(new String[similarAppsNames.size()]));
  //}

  public Spannable getAppText(Context context) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
        ContextCompat.getColor(context, R.color.appstimeline_grey), "");
  }

  //public String getTimeSinceLastUpdate(Context context) {
  //  return dateCalculator.getTimeSinceDate(context, date);
  //}

  //public String getTimeSinceRecommendation(Context context) {
  //  return dateCalculator.getTimeSinceDate(context, timestamp);
  //}

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_social_install;
  }

  public void sendOpenAppEvent() {
    timelineAnalytics.sendOpenAppEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        getPackageName());
  }

  @Override
  public void share(Context context, boolean privacyResult, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard(), context, privacyResult, shareCardCallback);
  }

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating);
  }

  @Override public void like(Context context, String cardId, String cardType, int rating) {
    socialRepository.like(cardId, cardType, "", rating);
  }
}
