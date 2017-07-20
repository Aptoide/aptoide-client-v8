package cm.aptoide.pt.v8engine.timeline.view.displayable;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AppsTimeline.BLANK;

/**
 * Created by marcelobenites on 6/17/16.
 */
public class StoreLatestAppsDisplayable extends CardDisplayable {

  public static final String CARD_TYPE_NAME = "LATEST_APPS";
  @Getter private String storeName;
  @Getter private String avatarUrl;
  @Getter private List<LatestApp> latestApps;
  @Getter private String abUrl;
  @Getter private String storeTheme;
  private SpannableFactory spannableFactory;
  private DateCalculator dateCalculator;
  private Date date;
  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;

  public StoreLatestAppsDisplayable() {
  }

  public StoreLatestAppsDisplayable(StoreLatestApps storeLatestApps, String storeName,
      String avatarUrl, List<LatestApp> latestApps, String abUrl, SpannableFactory spannableFactory,
      DateCalculator dateCalculator, Date date, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository, String storeTheme, WindowManager windowManager) {
    super(storeLatestApps, timelineAnalytics, windowManager);
    this.storeName = storeName;
    this.avatarUrl = avatarUrl;
    this.latestApps = latestApps;
    this.abUrl = abUrl;
    this.spannableFactory = spannableFactory;
    this.dateCalculator = dateCalculator;
    this.date = date;
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
    this.storeTheme = storeTheme;
  }

  public static StoreLatestAppsDisplayable from(StoreLatestApps storeLatestApps,
      SpannableFactory spannableFactory, DateCalculator dateCalculator,
      TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      WindowManager windowManager) {
    final List<LatestApp> latestApps = new ArrayList<>();
    for (App app : storeLatestApps.getApps()) {
      latestApps.add(
          new LatestApp(app.getId(), app.getName(), app.getIcon(), app.getPackageName()));
    }
    String abTestingURL = null;

    if (storeLatestApps.getAb() != null
        && storeLatestApps.getAb()
        .getConversion() != null
        && storeLatestApps.getAb()
        .getConversion()
        .getUrl() != null) {
      abTestingURL = storeLatestApps.getAb()
          .getConversion()
          .getUrl();
    }
    return new StoreLatestAppsDisplayable(storeLatestApps, storeLatestApps.getStore()
        .getName(), storeLatestApps.getStore()
        .getAvatar(), latestApps, abTestingURL, spannableFactory, dateCalculator,
        storeLatestApps.getLatestUpdate(), timelineAnalytics, socialRepository,
        storeLatestApps.getStore()
            .getAppearance()
            .getTheme(), windowManager);
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_store_latest_apps;
  }

  public void sendOpenStoreEvent() {
    timelineAnalytics.sendOpenStoreEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        getStoreName());
  }

  public void sendOpenAppEvent(String packageName) {
    timelineAnalytics.sendStoreOpenAppEvent(CARD_TYPE_NAME, TimelineAnalytics.SOURCE_APTOIDE,
        packageName, storeName);
  }

  public void sendStoreLatestAppsClickEvent(String action, String socialAction,
      String packageName) {
    timelineAnalytics.sendStoreLatestAppsClickEvent(CARD_TYPE_NAME, packageName, action,
        socialAction, getStoreName());
  }

  @Override
  public void share(String cardId, boolean privacyResult, ShareCardCallback shareCardCallback,
      Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), privacyResult, shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, BLANK, getStoreName(), BLANK));
  }

  @Override
  public void share(String cardId, ShareCardCallback shareCardCallback, Resources resources) {
    socialRepository.share(getTimelineCard().getCardId(), shareCardCallback,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, SHARE, BLANK, getStoreName(), BLANK));
  }

  @Override public void like(Context context, String cardType, int rating, Resources resources) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, BLANK, getStoreName(), BLANK));
  }

  @Override public void like(Context context, String cardId, String cardType, int rating,
      Resources resources) {
    socialRepository.like(cardId, cardType, "", rating,
        getTimelineSocialActionObject(CARD_TYPE_NAME, BLANK, LIKE, BLANK, getStoreName(), BLANK));
  }

  public Spannable getStyledTitle(Context context) {
    return spannableFactory.createColorSpan(
        context.getString(R.string.timeline_title_card_title_has_new_apps_present_singular,
            storeName), ContextCompat.getColor(context, R.color.black_87_alpha), storeName);
  }

  @EqualsAndHashCode public static class LatestApp {

    @Getter private final long appId;
    @Getter private final String iconUrl;
    @Getter private final String packageName;
    @Getter private final String name;

    public LatestApp(long appId, String appName, String iconUrl, String packageName) {
      this.appId = appId;
      this.iconUrl = iconUrl;
      this.packageName = packageName;
      this.name = appName;
    }
  }
}
