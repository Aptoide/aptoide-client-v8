package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialInstall;
import cm.aptoide.pt.dataprovider.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import cm.aptoide.pt.v8engine.timeline.view.navigation.AppsTimelineNavigator;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.Date;
import java.util.List;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AppsTimeline.BLANK;

/**
 * Created by jdandrade on 11/05/2017.
 */

public class AggregatedSocialInstallDisplayable extends CardDisplayable {

  public static final String CARD_TYPE_NAME = "AGGREGATED_SOCIAL_INSTALL";
  private List<MinimalCard> minimalCardList;
  private List<UserSharerTimeline> sharers;
  private SocialRepository socialRepository;
  private long appStoreId;
  private long appId;
  private String appIcon;
  private String appName;
  private String packageName;
  private float appRatingAverage;
  private DateCalculator dateCalculator;
  private Date date;
  private String abTestingURL;
  private TimelineAnalytics timelineAnalytics;
  private SpannableFactory spannableFactory;
  private AppsTimelineNavigator timelineNavigator;

  public AggregatedSocialInstallDisplayable() {
  }

  public AggregatedSocialInstallDisplayable(AggregatedSocialInstall card, long appId,
      String packageName, String appName, String appIcon, String abTestingURL, Date date,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      DateCalculator dateCalculator, SpannableFactory spannableFactory,
      AppsTimelineNavigator timelineNavigator, WindowManager windowManager) {
    super(card, timelineAnalytics, windowManager);
    this.minimalCardList = card.getMinimalCardList();
    this.sharers = card.getSharers();
    this.socialRepository = socialRepository;
    this.spannableFactory = spannableFactory;
    this.timelineAnalytics = timelineAnalytics;
    this.appStoreId = card.getApp()
        .getStore()
        .getId();
    this.dateCalculator = dateCalculator;
    this.date = date;
    this.appIcon = appIcon;
    this.appName = appName;
    this.appRatingAverage = card.getApp()
        .getStats()
        .getRating()
        .getAvg();
    this.abTestingURL = abTestingURL;
    this.packageName = packageName;
    this.appId = appId;
    this.timelineNavigator = timelineNavigator;
  }

  public static Displayable from(AggregatedSocialInstall aggregatedSocialInstall,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      DateCalculator dateCalculator, SpannableFactory spannableFactory,
      AppsTimelineNavigator timelineNavigator, WindowManager windowManager) {

    String abTestingURL = null;

    if (aggregatedSocialInstall.getAb() != null
        && aggregatedSocialInstall.getAb()
        .getConversion() != null
        && aggregatedSocialInstall.getAb()
        .getConversion()
        .getUrl() != null) {
      abTestingURL = aggregatedSocialInstall.getAb()
          .getConversion()
          .getUrl();
    }

    return new AggregatedSocialInstallDisplayable(aggregatedSocialInstall,
        aggregatedSocialInstall.getApp()
            .getId(), aggregatedSocialInstall.getApp()
        .getPackageName(), aggregatedSocialInstall.getApp()
        .getName(), aggregatedSocialInstall.getApp()
        .getIcon(), abTestingURL, aggregatedSocialInstall.getDate(), timelineAnalytics,
        socialRepository, dateCalculator, spannableFactory, timelineNavigator, windowManager);
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback,
      Resources resources) {
    socialRepository.share(cardId, getAppStoreId(), privacyResult, shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(), BLANK,
            BLANK));
  }

  @Override
  public void share(String cardId, ShareCardCallback shareCardCallback, Resources resources) {
    socialRepository.share(cardId, getAppStoreId(), shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, getPackageName(), BLANK,
            BLANK));
  }

  @Override public void like(Context context, String cardType, int rating, Resources resources) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, getPackageName(), BLANK, BLANK));
  }

  @Override public void like(Context context, String cardId, String cardType, int rating,
      Resources resources) {
    socialRepository.like(cardId, cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, getPackageName(), BLANK, BLANK));
  }

  public List<MinimalCard> getMinimalCardList() {
    return minimalCardList;
  }

  public List<UserSharerTimeline> getSharers() {
    return sharers;
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_aggregated_social_install;
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public void sendOpenAppEvent() {
    timelineAnalytics.sendOpenAppEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        getPackageName());
  }

  public String getTimeSinceLastUpdate(Context context, Date date) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public String getAppIcon() {
    return appIcon;
  }

  public String getAppName() {
    return appName;
  }

  public float getAppRatingAverage() {
    return appRatingAverage;
  }

  public String getAbTestingURL() {
    return abTestingURL;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getAppId() {
    return appId;
  }

  public long getAppStoreId() {
    return appStoreId;
  }

  public void likesPreviewClick(long numberOfLikes, String cardId) {
    timelineNavigator.navigateToLikesView(cardId, numberOfLikes);
  }

  public Spannable getBlackHighlightedLike(Context context, String string) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_short_like_present_singular, string),
        ContextCompat.getColor(context, R.color.black_87_alpha), string);
  }

  public String getCardHeaderNames() {
    StringBuilder headerNamesStringBuilder = new StringBuilder();
    List<UserSharerTimeline> firstSharers = getSharers().subList(0, 2);
    for (UserSharerTimeline user : firstSharers) {
      headerNamesStringBuilder.append(user.getStore()
          .getName())
          .append(", ");
    }
    headerNamesStringBuilder.setLength(headerNamesStringBuilder.length() - 2);
    return headerNamesStringBuilder.toString();
  }
}
