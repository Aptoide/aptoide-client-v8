package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.SocialStoreLatestApps;
import cm.aptoide.pt.v8engine.R;
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
  @Getter private String storeName;
  @Getter private String avatarUrl;
  @Getter private List<SocialStoreLatestAppsDisplayable.LatestApp> latestApps;
  @Getter private String abUrl;

  private DateCalculator dateCalculator;

  private Date date;
  private TimelineMetricsManager timelineMetricsManager;

  public SocialStoreLatestAppsDisplayable() {
  }

  public static SocialStoreLatestAppsDisplayable from(SocialStoreLatestApps storeLatestApps,
      DateCalculator dateCalculator, TimelineMetricsManager timelineMetricsManager) {
    final List<SocialStoreLatestAppsDisplayable.LatestApp> latestApps = new ArrayList<>();
    for (App app : storeLatestApps.getApps()) {
      latestApps.add(new SocialStoreLatestAppsDisplayable.LatestApp(app.getId(), app.getIcon(),
          app.getPackageName()));
    }
    String abTestingURL = null;

    if (storeLatestApps.getAb() != null
        && storeLatestApps.getAb().getConversion() != null
        && storeLatestApps.getAb().getConversion().getUrl() != null) {
      abTestingURL = storeLatestApps.getAb().getConversion().getUrl();
    }
    return new SocialStoreLatestAppsDisplayable(storeLatestApps.getStore().getName(),
        storeLatestApps.getStore().getAvatar(), latestApps, abTestingURL, dateCalculator,
        storeLatestApps.getLatestUpdate(), timelineMetricsManager);
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

  }

  @Override public void like(Context context, String cardType, int rating) {

  }

  @EqualsAndHashCode @AllArgsConstructor public static class LatestApp {

    @Getter private final long appId;
    @Getter private final String iconUrl;
    @Getter private final String packageName;
  }
}
