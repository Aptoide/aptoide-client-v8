package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.model.v7.timeline.AggregatedSocialStoreLatestApps;
import cm.aptoide.pt.dataprovider.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.dataprovider.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
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
 * Created by jdandrade on 18/05/2017.
 */

public class AggregatedSocialStoreLatestAppsDisplayable extends CardDisplayable {
  public static final String CARD_TYPE_NAME = "AGGREGATED_SOCIAL_LATEST_APPS";
  private SpannableFactory spannableFactory;
  private List<App> latestApps;
  private String abTestingUrl;
  private Store ownerStore;
  private Store sharedStore;
  private Date date;
  private List<MinimalCard> minimalCards;
  private List<UserSharerTimeline> sharers;
  private DateCalculator dateCalculator;
  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;
  private StoreCredentialsProvider storeCredentialsProvider;
  private AppsTimelineNavigator timelineNavigator;

  public AggregatedSocialStoreLatestAppsDisplayable() {
  }

  public AggregatedSocialStoreLatestAppsDisplayable(AggregatedSocialStoreLatestApps card,
      Store ownerStore, Store sharedStore, List<App> apps, String abTestingURL,
      DateCalculator dateCalculator, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, SpannableFactory spannableFactory,
      StoreCredentialsProvider storeCredentialsProvider, List<MinimalCard> minimalCards,
      List<UserSharerTimeline> sharers, AppsTimelineNavigator timelineNavigator,
      WindowManager windowManager) {
    super(card, timelineAnalytics, windowManager);
    this.latestApps = apps;
    this.abTestingUrl = abTestingURL;
    this.ownerStore = ownerStore;
    this.sharedStore = sharedStore;
    this.dateCalculator = dateCalculator;
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
    this.spannableFactory = spannableFactory;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.minimalCards = minimalCards;
    this.sharers = sharers;
    this.date = card.getDate();
    this.timelineNavigator = timelineNavigator;
  }

  public static Displayable from(AggregatedSocialStoreLatestApps card,
      DateCalculator dateCalculator, SpannableFactory spannableFactory,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      StoreCredentialsProvider storeCredentialsProvider, AppsTimelineNavigator timelineNavigator,
      WindowManager windowManager) {

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

    return new AggregatedSocialStoreLatestAppsDisplayable(card, card.getOwnerStore(),
        card.getSharedStore(), card.getApps(), abTestingURL, dateCalculator, timelineAnalytics,
        socialRepository, spannableFactory, storeCredentialsProvider, card.getMinimalCardList(),
        card.getSharers(), timelineNavigator, windowManager);
  }

  public List<MinimalCard> getMinimalCards() {
    return minimalCards;
  }

  public Store getOwnerStore() {
    return ownerStore;
  }

  public Store getSharedStore() {
    return sharedStore;
  }

  public String getSharedStoreName() {
    if (sharedStore == null) {
      return "";
    }
    return sharedStore.getName();
  }

  public String getAbTestingURL() {
    return abTestingUrl;
  }

  public List<App> getLatestApps() {
    return latestApps;
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public String getTimeSinceLastUpdate(Context context, Date date) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  public StoreCredentialsProvider getStoreCredentialsProvider() {
    return storeCredentialsProvider;
  }

  public void sendStoreOpenAppEvent(String packageName) {
    timelineAnalytics.sendStoreOpenAppEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        packageName, ownerStore.getName());
  }

  public void sendOpenSharedStoreEvent() {
    timelineAnalytics.sendOpenStoreEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        sharedStore.getName());
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

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_aggregated_social_store;
  }

  public void likesPreviewClick(long numberOfLikes, String cardId) {
    timelineNavigator.navigateToLikesView(cardId, numberOfLikes);
  }

  public Spannable getBlackHighlightedLike(Context context, String string) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_short_like_present_singular, string),
        ContextCompat.getColor(context, R.color.black_87_alpha), string);
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback,
      Resources resources) {
    socialRepository.share(cardId, privacyResult, shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, BLANK, BLANK, BLANK));
  }

  @Override
  public void share(String cardId, ShareCardCallback shareCardCallback, Resources resources) {
    socialRepository.share(cardId, shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, BLANK, BLANK, BLANK));
  }

  @Override public void like(Context context, String cardType, int rating, Resources resources) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, BLANK, BLANK, BLANK));
  }

  @Override public void like(Context context, String cardId, String cardType, int rating,
      Resources resources) {
    socialRepository.like(cardId, cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, BLANK, BLANK, BLANK));
  }
}
