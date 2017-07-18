package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialStoreLatestApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import cm.aptoide.pt.v8engine.timeline.view.navigation.AppsTimelineNavigator;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AppsTimeline.BLANK;

/**
 * Created by jdandrade on 29/11/2016.
 */
public class SocialStoreLatestAppsDisplayable extends SocialCardDisplayable {
  public static final String CARD_TYPE_NAME = "SOCIAL_LATEST_APPS";
  @Getter private String storeName;
  @Getter private String avatarUrl;
  @Getter private List<SocialStoreLatestAppsDisplayable.LatestApp> latestApps;
  @Getter private String abTestingUrl;
  @Getter private Store sharedStore;
  @Getter private Comment.User user;
  @Getter private Comment.User userSharer;
  private SpannableFactory spannableFactory;

  private DateCalculator dateCalculator;

  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;
  private StoreCredentialsProvider storeCredentialsProvider;

  public SocialStoreLatestAppsDisplayable() {
  }

  // TODO: 22/12/2016 Date latestUpdate,
  private SocialStoreLatestAppsDisplayable(SocialStoreLatestApps socialStoreLatestApps,
      String storeName, String avatarUrl, List<LatestApp> latestApps, String abTestingUrl,
      long likes, long comments, DateCalculator dateCalculator, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, SpannableFactory spannableFactory,
      StoreCredentialsProvider storeCredentialsProvider, AppsTimelineNavigator timelineNavigator,
      WindowManager windowManager) {
    super(socialStoreLatestApps, likes, comments, socialStoreLatestApps.getOwnerStore(),
        socialStoreLatestApps.getUser(), socialStoreLatestApps.getUserSharer(),
        socialStoreLatestApps.getMy()
            .isLiked(), socialStoreLatestApps.getLikes(), socialStoreLatestApps.getComments(),
        socialStoreLatestApps.getDate(), spannableFactory, dateCalculator, abTestingUrl,
        timelineAnalytics, timelineNavigator, windowManager);
    this.storeName = storeName;
    this.avatarUrl = avatarUrl;
    this.latestApps = latestApps;
    this.abTestingUrl = abTestingUrl;
    this.dateCalculator = dateCalculator;
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
    this.sharedStore = socialStoreLatestApps.getSharedStore();
    this.user = socialStoreLatestApps.getUser();
    this.userSharer = socialStoreLatestApps.getUserSharer();
    this.spannableFactory = spannableFactory;
    this.storeCredentialsProvider = storeCredentialsProvider;
  }

  public static SocialStoreLatestAppsDisplayable from(SocialStoreLatestApps socialStoreLatestApps,
      DateCalculator dateCalculator, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, SpannableFactory spannableFactory,
      StoreCredentialsProvider storeCredentialsProvider, AppsTimelineNavigator timelineNavigator,
      WindowManager windowManager) {
    final List<SocialStoreLatestAppsDisplayable.LatestApp> latestApps = new ArrayList<>();
    for (App app : socialStoreLatestApps.getApps()) {
      latestApps.add(
          new SocialStoreLatestAppsDisplayable.LatestApp(app.getId(), app.getName(), app.getIcon(),
              app.getPackageName()));
    }
    String abTestingURL = null;

    if (socialStoreLatestApps.getAb() != null
        && socialStoreLatestApps.getAb()
        .getConversion() != null
        && socialStoreLatestApps.getAb()
        .getConversion()
        .getUrl() != null) {
      abTestingURL = socialStoreLatestApps.getAb()
          .getConversion()
          .getUrl();
    }

    String ownerStoreName = "";
    String ownerStoreAvatar = "";
    if (socialStoreLatestApps.getOwnerStore() != null) {
      ownerStoreName = socialStoreLatestApps.getOwnerStore()
          .getName();
      ownerStoreAvatar = socialStoreLatestApps.getOwnerStore()
          .getAvatar();
    }

    // TODO: 22/12/2016 socialStoreLatestApps.getLatestUpdate()
    return new SocialStoreLatestAppsDisplayable(socialStoreLatestApps, ownerStoreName,
        ownerStoreAvatar, latestApps, abTestingURL, socialStoreLatestApps.getStats()
        .getLikes(), socialStoreLatestApps.getStats()
        .getComments(), dateCalculator, timelineAnalytics, socialRepository, spannableFactory,
        storeCredentialsProvider, timelineNavigator, windowManager);
  }

  public Spannable getStyledTitle(Context context, String title) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_title_card_title_recommend_present_singular, title),
        ContextCompat.getColor(context, R.color.black_87_alpha), title);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_social_store_latest_apps;
  }

  public void sendStoreOpenAppEvent(String packageName) {
    timelineAnalytics.sendStoreOpenAppEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        packageName, storeName);
  }

  public void sendOpenStoreEvent() {
    timelineAnalytics.sendOpenStoreEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        storeName);
  }

  public void sendOpenSharedStoreEvent() {
    timelineAnalytics.sendOpenStoreEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        sharedStore.getName());
  }

  public void sendSocialLatestAppsClickEvent(String action, String packageName, String socialAction,
      String publisher) {
    timelineAnalytics.sendSocialLatestClickEvent(CARD_TYPE_NAME, packageName, action, socialAction,
        publisher);
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback,
      Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), privacyResult, shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, BLANK, BLANK, BLANK));
  }

  @Override
  public void share(String cardId, ShareCardCallback shareCardCallback, Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), shareCardCallback,
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

  public StoreCredentialsProvider getStoreCredentialsProvider() {
    return storeCredentialsProvider;
  }

  @EqualsAndHashCode public static class LatestApp {

    @Getter private final long appId;
    @Getter private final String iconUrl;
    @Getter private final String packageName;
    @Getter private final String appName;

    public LatestApp(long appId, String appName, String iconUrl, String packageName) {
      this.appId = appId;
      this.iconUrl = iconUrl;
      this.packageName = packageName;
      this.appName = appName;
    }
  }
}
