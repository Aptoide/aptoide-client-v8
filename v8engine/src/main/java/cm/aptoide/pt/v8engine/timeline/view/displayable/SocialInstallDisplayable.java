package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialInstall;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import cm.aptoide.pt.v8engine.timeline.view.navigation.AppsTimelineNavigator;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import lombok.Getter;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AppsTimeline.BLANK;

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
  @Getter private float rating;
  @Getter private Long appStoreId;

  private TimelineAnalytics timelineAnalytics;
  private SpannableFactory spannableFactory;
  private SocialRepository socialRepository;
  private Resources resources;
  private String marketName;

  public SocialInstallDisplayable() {
  }

  public SocialInstallDisplayable(SocialInstall socialInstall, int icon, Store store,
      int titleResource, Comment.User user, long appId, String packageName, String appName,
      String appIcon, String abTestingURL, long likes, long comments, Date date,
      TimelineAnalytics timelineAnalytics, SpannableFactory spannableFactory,
      SocialRepository socialRepository, DateCalculator dateCalculator,
      AppsTimelineNavigator timelineNavigator, Resources resources, String marketName,
      WindowManager windowManager) {
    super(socialInstall, likes, comments, store, socialInstall.getUser(),
        socialInstall.getUserSharer(), socialInstall.getMy()
            .isLiked(), socialInstall.getLikes(), socialInstall.getComments(), date,
        spannableFactory, dateCalculator, abTestingURL, timelineAnalytics, timelineNavigator,
        windowManager);
    this.avatarResource = icon;
    this.titleResource = titleResource;
    this.user = user;
    this.appId = appId;
    this.packageName = packageName;
    this.appName = appName;
    this.appIcon = appIcon;
    this.abUrl = abTestingURL;
    this.rating = socialInstall.getApp()
        .getStats()
        .getRating()
        .getAvg();
    this.timelineAnalytics = timelineAnalytics;
    this.spannableFactory = spannableFactory;
    this.socialRepository = socialRepository;
    this.appStoreId = socialInstall.getStore()
        .getId();
    this.resources = resources;
    this.marketName = marketName;
  }

  public static Displayable from(SocialInstall socialInstall, TimelineAnalytics timelineAnalytics,
      SpannableFactory spannableFactory, SocialRepository socialRepository,
      DateCalculator dateCalculator, AppsTimelineNavigator timelineNavigator, Resources resources,
      String marketName, WindowManager windowManager) {

    String abTestingURL = null;

    if (socialInstall.getAb() != null
        && socialInstall.getAb()
        .getConversion() != null
        && socialInstall.getAb()
        .getConversion()
        .getUrl() != null) {
      abTestingURL = socialInstall.getAb()
          .getConversion()
          .getUrl();
    }

    return new SocialInstallDisplayable(socialInstall, Application.getConfiguration()
        .getIcon(), socialInstall.getStore(),
        R.string.timeline_title_card_title_recommend_present_singular, socialInstall.getUser(),
        socialInstall.getApp()
            .getId(), socialInstall.getApp()
        .getPackageName(), socialInstall.getApp()
        .getName(), socialInstall.getApp()
        .getIcon(), abTestingURL, socialInstall.getStats()
        .getLikes(), socialInstall.getStats()
        .getComments(), socialInstall.getDate(), timelineAnalytics, spannableFactory,
        socialRepository, dateCalculator, timelineNavigator, resources, marketName, windowManager);
  }

  public Spannable getAppText(Context context) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, ""),
        ContextCompat.getColor(context, R.color.appstimeline_grey), "");
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_social_install;
  }

  public void sendOpenAppEvent() {
    timelineAnalytics.sendOpenAppEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        getPackageName());
  }

  public Spannable getStyledTitle(Context context, String title) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_title_card_title_install_past_singular, title),
        ContextCompat.getColor(context, R.color.black_87_alpha), title);
  }

  public void sendSocialInstallClickEvent(String action, String socialAction) {
    timelineAnalytics.sendSocialInstallClickEvent(CARD_TYPE_NAME, action, socialAction,
        getPackageName(), getTitle());
  }

  public String getTitle() {
    return AptoideUtils.StringU.getFormattedString(titleResource, resources, marketName);
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback,
      Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), getAppStoreId(), privacyResult,
        shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(), getTitle(),
            BLANK));
  }

  @Override
  public void share(String cardId, ShareCardCallback shareCardCallback, Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), getAppStoreId(), shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(), getTitle(),
            BLANK));
  }

  @Override public void like(Context context, String cardType, int rating, Resources resources) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, getPackageName(), getTitle(),
            BLANK));
  }

  @Override public void like(Context context, String cardId, String cardType, int rating,
      Resources resources) {
    socialRepository.like(cardId, cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, getPackageName(), getTitle(),
            BLANK));
  }
}
