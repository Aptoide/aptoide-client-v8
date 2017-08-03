package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.WindowManager;
import cm.aptoide.pt.database.accessors.InstalledAccessor;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialVideo;
import cm.aptoide.pt.dataprovider.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.link.Link;
import cm.aptoide.pt.v8engine.link.LinksHandlerFactory;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import cm.aptoide.pt.v8engine.timeline.view.navigation.AppsTimelineNavigator;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AppsTimeline.BLANK;

/**
 * Created by jdandrade on 19/05/2017.
 */

public class AggregatedSocialVideoDisplayable extends CardDisplayable {
  public static final String CARD_TYPE_NAME = "AGGREGATED_SOCIAL_VIDEO";
  private List<UserSharerTimeline> sharers;
  private List<MinimalCard> minimalCards;
  private String title;
  private Link link;
  private String publisherName;
  private String thumbnailUrl;
  private long appId;
  private String packageName;
  private String abTestingURL;
  private List<App> relatedToApps;
  private Date date;
  private DateCalculator dateCalculator;
  private SpannableFactory spannableFactory;
  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;
  private AppsTimelineNavigator timelineNavigator;

  public AggregatedSocialVideoDisplayable() {
  }

  public AggregatedSocialVideoDisplayable(AggregatedSocialVideo card, String title, Link link,
      String publisherName, String thumbnailUrl, long appId, String abTestingURL,
      List<App> relatedToApps, Date date, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, AppsTimelineNavigator timelineNavigator,
      WindowManager windowManager) {
    super(card, timelineAnalytics, windowManager);
    this.title = title;
    this.link = link;
    this.publisherName = publisherName;
    this.thumbnailUrl = thumbnailUrl;
    this.appId = appId;
    this.abTestingURL = abTestingURL;
    this.relatedToApps = relatedToApps;
    this.date = date;
    this.dateCalculator = dateCalculator;
    this.spannableFactory = spannableFactory;
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
    this.minimalCards = card.getMinimalCards();
    this.sharers = card.getSharers();
    this.timelineNavigator = timelineNavigator;
  }

  public static Displayable from(AggregatedSocialVideo card, DateCalculator dateCalculator,
      SpannableFactory spannableFactory, LinksHandlerFactory linksHandlerFactory,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      AppsTimelineNavigator timelineNavigator, WindowManager windowManager) {
    long appId = 0;

    String abTestingURL = null;

    if (card.getAb() != null
        && card.getAb()
        .getConversion() != null
        && card.getAb()
        .getConversion()
        .getUrl() != null) {
      abTestingURL = card.getAb()
          .getConversion()
          .getUrl();
    }
    return new AggregatedSocialVideoDisplayable(card, card.getTitle(),
        linksHandlerFactory.get(LinksHandlerFactory.CUSTOM_TABS_LINK_TYPE, card.getUrl()),
        card.getPublisher()
            .getName(), card.getThumbnailUrl(), appId, abTestingURL, card.getApps(), card.getDate(),
        dateCalculator, spannableFactory, timelineAnalytics, socialRepository, timelineNavigator,
        windowManager);
  }

  public Observable<List<Installed>> getRelatedToApplication(InstalledAccessor installedAccessor) {
    if (relatedToApps != null && relatedToApps.size() > 0) {
      List<String> packageNamesList = new ArrayList<>();

      for (int i = 0; i < relatedToApps.size(); i++) {
        packageNamesList.add(relatedToApps.get(i)
            .getPackageName());
        packageName = relatedToApps.get(i)
            .getPackageName();
      }

      final String[] packageNames = packageNamesList.toArray(new String[packageNamesList.size()]);

      return installedAccessor.getInstalled(packageNames)
          .observeOn(Schedulers.computation());
    }
    return Observable.just(null);
  }

  public Spannable getAppText(Context context, String appName) {
    return spannableFactory.createStyleSpan(
        context.getString(R.string.displayable_social_timeline_article_get_app_button, appName),
        Typeface.BOLD, appName);
  }

  public Spannable getAppRelatedToText(Context context, String appName) {
    return spannableFactory.createStyleSpan(
        context.getString(R.string.displayable_social_timeline_article_related_to, appName),
        Typeface.BOLD, appName);
  }

  public Spannable getStyledTitle(Context context, String title) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_title_card_title_share_past_plural, title),
        ContextCompat.getColor(context, R.color.black_87_alpha), title);
  }

  public void sendOpenVideoEvent() {
    timelineAnalytics.sendOpenArticleEvent(CARD_TYPE_NAME, getTitle(), getLink().getUrl(),
        packageName);
  }

  public String getTitle() {
    return title;
  }

  public Link getLink() {
    return link;
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

  public List<UserSharerTimeline> getSharers() {
    return sharers;
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public String getTimeSinceLastUpdate(Context context, Date date) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public List<MinimalCard> getMinimalCards() {
    return minimalCards;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public long getAppId() {
    return appId;
  }

  public String getAbTestingURL() {
    return abTestingURL;
  }

  public List<App> getRelatedToApps() {
    return relatedToApps;
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback,
      Resources resources) {
    socialRepository.share(cardId, privacyResult, shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, BLANK, getPublisherName(),
            BLANK));
  }

  @Override
  public void share(String cardId, ShareCardCallback shareCardCallback, Resources resources) {
    socialRepository.share(cardId, shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, BLANK, getPublisherName(),
            BLANK));
  }

  @Override public void like(Context context, String cardType, int rating, Resources resources) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, BLANK, getPublisherName(),
            BLANK));
  }

  @Override public void like(Context context, String cardId, String cardType, int rating,
      Resources resources) {
    socialRepository.like(cardId, cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, BLANK, getPublisherName(),
            BLANK));
  }

  public String getPublisherName() {
    return publisherName;
  }

  public Spannable getBlackHighlightedLike(Context context, String string) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_short_like_present_singular, string),
        ContextCompat.getColor(context, R.color.black_87_alpha), string);
  }

  public void likesPreviewClick(long numberOfLikes, String cardId) {
    timelineNavigator.navigateToLikesView(cardId, numberOfLikes);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_aggregated_social_video;
  }
}
