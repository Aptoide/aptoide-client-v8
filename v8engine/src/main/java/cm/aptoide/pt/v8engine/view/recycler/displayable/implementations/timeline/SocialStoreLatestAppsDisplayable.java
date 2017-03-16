package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline;

import android.content.Context;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.SocialStoreLatestApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ShareCardCallback;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineAnalytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.DateCalculator;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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
      StoreCredentialsProvider storeCredentialsProvider) {
    super(socialStoreLatestApps, likes, comments, socialStoreLatestApps.getOwnerStore(),
        socialStoreLatestApps.getUser(), socialStoreLatestApps.getUserSharer(),
        socialStoreLatestApps.getMy().isLiked(), socialStoreLatestApps.getLikes(),
        socialStoreLatestApps.getDate(), spannableFactory, dateCalculator, abTestingUrl);
    this.storeName = storeName;
    //socialStoreLatestApps.getSharedStore().getId();
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
      StoreCredentialsProvider storeCredentialsProvider) {
    final List<SocialStoreLatestAppsDisplayable.LatestApp> latestApps = new ArrayList<>();
    for (App app : socialStoreLatestApps.getApps()) {
      latestApps.add(new SocialStoreLatestAppsDisplayable.LatestApp(app.getId(), app.getIcon(),
          app.getPackageName()));
    }
    String abTestingURL = null;

    if (socialStoreLatestApps.getAb() != null
        && socialStoreLatestApps.getAb().getConversion() != null
        && socialStoreLatestApps.getAb().getConversion().getUrl() != null) {
      abTestingURL = socialStoreLatestApps.getAb().getConversion().getUrl();
    }

    String ownerStoreName = "";
    String ownerStoreAvatar = "";
    if (socialStoreLatestApps.getOwnerStore() != null) {
      ownerStoreName = socialStoreLatestApps.getOwnerStore().getName();
      ownerStoreAvatar = socialStoreLatestApps.getOwnerStore().getAvatar();
    }

    // TODO: 22/12/2016 socialStoreLatestApps.getLatestUpdate() 
    return new SocialStoreLatestAppsDisplayable(socialStoreLatestApps, ownerStoreName,
        ownerStoreAvatar, latestApps, abTestingURL, socialStoreLatestApps.getStats().getLikes(),
        socialStoreLatestApps.getStats().getComments(), dateCalculator, timelineAnalytics,
        socialRepository, spannableFactory, storeCredentialsProvider);
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

  public StoreCredentialsProvider getStoreCredentialsProvider() {
    return storeCredentialsProvider;
  }

  @EqualsAndHashCode public static class LatestApp {

    @Getter private final long appId;
    @Getter private final String iconUrl;
    @Getter private final String packageName;

    public LatestApp(long appId, String iconUrl, String packageName) {
      this.appId = appId;
      this.iconUrl = iconUrl;
      this.packageName = packageName;
    }
  }
}
