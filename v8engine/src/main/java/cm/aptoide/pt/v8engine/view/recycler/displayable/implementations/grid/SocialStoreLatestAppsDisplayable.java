package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.SocialStoreLatestApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.repository.SocialRepository;
import cm.aptoide.pt.v8engine.repository.TimelineMetricsManager;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.SocialCardDisplayable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by jdandrade on 29/11/2016.
 */
@AllArgsConstructor public class SocialStoreLatestAppsDisplayable extends SocialCardDisplayable {
  private SocialStoreLatestApps socialStoreLatestApps;
  @Getter private String storeName;
  @Getter private String avatarUrl;
  @Getter private List<SocialStoreLatestAppsDisplayable.LatestApp> latestApps;
  @Getter private String abUrl;
  @Getter private long numberOfLikes;
  @Getter private long numberOfComments;

  private DateCalculator dateCalculator;
  private Date date;
  private TimelineMetricsManager timelineMetricsManager;
  private SocialRepository socialRepository;

  public SocialStoreLatestAppsDisplayable() {
  }

  public static SocialStoreLatestAppsDisplayable from(SocialStoreLatestApps socialStoreLatestApps,
      DateCalculator dateCalculator, TimelineMetricsManager timelineMetricsManager,
      SocialRepository socialRepository) {
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
    return new SocialStoreLatestAppsDisplayable(socialStoreLatestApps,
        socialStoreLatestApps.getStore().getName(), socialStoreLatestApps.getStore().getAvatar(),
        latestApps, abTestingURL, socialStoreLatestApps.getLikes(),
        socialStoreLatestApps.getComments(), dateCalculator,
        socialStoreLatestApps.getLatestUpdate(), timelineMetricsManager, socialRepository);
  }

  public String getTimeSinceLastUpdate(Context context) {
    return dateCalculator.getTimeSinceDate(context, date);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_social_timeline_social_store_latest_apps;
  }

  public void sendClickEvent(SendEventRequest.Body.Data data, String eventName) {
    timelineMetricsManager.sendEvent(data, eventName);
  }

  @Override public void share(Context context, boolean privacyResult) {
    socialRepository.share(socialStoreLatestApps, context, privacyResult);
  }

  @Override public void like(Context context, String cardType, int rating) {
    socialRepository.like(socialStoreLatestApps, cardType, "", rating);
  }

  @EqualsAndHashCode @AllArgsConstructor public static class LatestApp {

    @Getter private final long appId;
    @Getter private final String iconUrl;
    @Getter private final String packageName;
  }
}
