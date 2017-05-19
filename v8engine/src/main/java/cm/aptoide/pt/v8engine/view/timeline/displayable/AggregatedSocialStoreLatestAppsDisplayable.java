package cm.aptoide.pt.v8engine.view.timeline.displayable;

import android.content.Context;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.AggregatedSocialStoreLatestApps;
import cm.aptoide.pt.model.v7.timeline.MinimalCard;
import cm.aptoide.pt.model.v7.timeline.UserSharerTimeline;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.timeline.ShareCardCallback;
import java.util.Date;
import java.util.List;

/**
 * Created by jdandrade on 18/05/2017.
 */

public class AggregatedSocialStoreLatestAppsDisplayable extends CardDisplayable {
  public static final String CARD_TYPE_NAME = "AGGREGATED_SOCIAL_LATEST_APPS";
  private List<App> latestApps;
  private String abTestingUrl;
  private Store ownerStore;
  private Store sharedStore;
  private Comment.User user;
  private Comment.User userSharer;
  private Date date;
  private List<MinimalCard> minimalCards;
  private List<UserSharerTimeline> sharers;
  private SpannableFactory spannableFactory;
  private DateCalculator dateCalculator;
  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;
  private StoreCredentialsProvider storeCredentialsProvider;

  public AggregatedSocialStoreLatestAppsDisplayable() {
  }

  public AggregatedSocialStoreLatestAppsDisplayable(AggregatedSocialStoreLatestApps card,
      Store ownerStore, Store sharedStore, List<App> apps, String abTestingURL,
      DateCalculator dateCalculator, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, SpannableFactory spannableFactory,
      StoreCredentialsProvider storeCredentialsProvider, Comment.User user, Comment.User userSharer,
      List<MinimalCard> minimalCards, List<UserSharerTimeline> sharers) {
    super(card);
    this.latestApps = apps;
    this.abTestingUrl = abTestingURL;
    this.ownerStore = ownerStore;
    this.sharedStore = sharedStore;
    this.user = user;
    this.userSharer = userSharer;
    this.spannableFactory = spannableFactory;
    this.dateCalculator = dateCalculator;
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.minimalCards = minimalCards;
    this.sharers = sharers;
    this.date = card.getDate();
  }

  public static Displayable from(AggregatedSocialStoreLatestApps card,
      DateCalculator dateCalculator, SpannableFactory spannableFactory,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      StoreCredentialsProvider storeCredentialsProvider) {

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
        socialRepository, spannableFactory, storeCredentialsProvider, card.getUser(),
        card.getUserSharer(), card.getMinimalCardList(), card.getSharers());
  }

  public List<MinimalCard> getMinimalCards() {
    return minimalCards;
  }

  public List<UserSharerTimeline> getSharers() {
    return sharers;
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

  public Comment.User getUser() {
    return user;
  }

  public Comment.User getUserSharer() {
    return userSharer;
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

  public void sendOpenStoreEvent() {
    timelineAnalytics.sendOpenStoreEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        ownerStore.getName());
  }

  public void sendOpenSharedStoreEvent() {
    timelineAnalytics.sendOpenStoreEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        sharedStore.getName());
  }

  public String getCardHeaderNames() {
    StringBuilder cardHeaderNames = new StringBuilder();
    for (UserSharerTimeline user : getSharers()) {
      cardHeaderNames.append(user.getStore()
          .getName())
          .append(" ");
    }
    return cardHeaderNames.toString();
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_aggregated_social_store;
  }

  @Override public void share(boolean privacyResult, ShareCardCallback shareCardCallback) {

  }

  @Override public void share(ShareCardCallback shareCardCallback) {

  }

  @Override public void like(Context context, String cardType, int rating) {

  }

  @Override public void like(Context context, String cardId, String cardType, int rating) {

  }
}
