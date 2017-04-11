package cm.aptoide.pt.v8engine.view.timeline.displayable;

import android.content.Context;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ShareCardCallback;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineAnalytics;
import cm.aptoide.pt.v8engine.util.DateCalculator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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
  private DateCalculator dateCalculator;
  private Date date;
  private TimelineAnalytics timelineAnalytics;
  private SocialRepository socialRepository;

  public StoreLatestAppsDisplayable() {
  }

  public StoreLatestAppsDisplayable(StoreLatestApps storeLatestApps, String storeName,
      String avatarUrl, List<LatestApp> latestApps, String abUrl, DateCalculator dateCalculator,
      Date date, TimelineAnalytics timelineAnalytics, SocialRepository socialRepository,
      String storeTheme) {
    super(storeLatestApps);
    this.storeName = storeName;
    this.avatarUrl = avatarUrl;
    this.latestApps = latestApps;
    this.abUrl = abUrl;
    this.dateCalculator = dateCalculator;
    this.date = date;
    this.timelineAnalytics = timelineAnalytics;
    this.socialRepository = socialRepository;
    this.storeTheme = storeTheme;
  }

  public static StoreLatestAppsDisplayable from(StoreLatestApps storeLatestApps,
      DateCalculator dateCalculator, TimelineAnalytics timelineAnalytics,
      SocialRepository socialRepository) {
    final List<LatestApp> latestApps = new ArrayList<>();
    for (App app : storeLatestApps.getApps()) {
      latestApps.add(new LatestApp(app.getId(), app.getIcon(), app.getPackageName()));
    }
    String abTestingURL = null;

    if (storeLatestApps.getAb() != null
        && storeLatestApps.getAb().getConversion() != null
        && storeLatestApps.getAb().getConversion().getUrl() != null) {
      abTestingURL = storeLatestApps.getAb().getConversion().getUrl();
    }
    return new StoreLatestAppsDisplayable(storeLatestApps, storeLatestApps.getStore().getName(),
        storeLatestApps.getStore().getAvatar(), latestApps, abTestingURL, dateCalculator,
        storeLatestApps.getLatestUpdate(), timelineAnalytics, socialRepository,
        storeLatestApps.getStore().getAppearance().getTheme());
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

  @Override
  public void share(Context context, boolean privacyResult, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard(), context, privacyResult, shareCardCallback);
  }

  @Override public void share(Context context, ShareCardCallback shareCardCallback) {
    socialRepository.share(getTimelineCard(), context, shareCardCallback);
  }

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(getTimelineCard().getCardId(), cardType, "", rating);
  }

  @Override public void like(Context context, String cardId, String cardType, int rating) {
    socialRepository.like(cardId, cardType, "", rating);
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
