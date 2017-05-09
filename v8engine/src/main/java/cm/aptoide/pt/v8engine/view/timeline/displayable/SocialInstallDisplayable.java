package cm.aptoide.pt.v8engine.view.timeline.displayable;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.SocialInstall;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.timeline.ShareCardCallback;
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
  @Getter private float rating;
  @Getter private Long appStoreId;

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
        socialInstall.getComments(), date, spannableFactory, dateCalculator, abTestingURL);
    this.avatarResource = icon;
    this.titleResource = titleResource;
    this.user = user;
    this.appId = appId;
    this.packageName = packageName;
    this.appName = appName;
    this.appIcon = appIcon;
    this.abUrl = abTestingURL;
    this.rating = socialInstall.getApp().getStats().getRating().getAvg();
    this.timelineAnalytics = timelineAnalytics;
    this.spannableFactory = spannableFactory;
    this.socialRepository = socialRepository;
    this.appStoreId = socialInstall.getStore().getId();
  }

  public static Displayable from(SocialInstall socialInstall, TimelineAnalytics timelineAnalytics,
      SpannableFactory spannableFactory, SocialRepository socialRepository,
      DateCalculator dateCalculator) {

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
        R.string.displayable_social_timeline_recommendation_atptoide_team_recommends,
        socialInstall.getUser(), socialInstall.getApp()
        .getId(), socialInstall.getApp()
        .getPackageName(), socialInstall.getApp()
        .getName(), socialInstall.getApp()
        .getIcon(), abTestingURL, socialInstall.getStats()
        .getLikes(), socialInstall.getStats()
        .getComments(), socialInstall.getDate(), timelineAnalytics, spannableFactory,
        socialRepository, dateCalculator);
  }

  public String getTitle() {
    return AptoideUtils.StringU.getFormattedString(titleResource, Application.getConfiguration()
        .getMarketName());
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
        context.getString(R.string.x_installed_and_recommended, title),
        ContextCompat.getColor(context, R.color.black_87_alpha), title);
  }

  @Override public void share(boolean privacyResult, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard().getCardId(), getAppStoreId(), privacyResult,
        shareCardCallback);
  }

  @Override public void share(ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard().getCardId(), getAppStoreId(), shareCardCallback);
  }

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating);
  }

  @Override public void like(Context context, String cardId, String cardType, int rating) {
    socialRepository.like(cardId, cardType, "", rating);
  }
}
