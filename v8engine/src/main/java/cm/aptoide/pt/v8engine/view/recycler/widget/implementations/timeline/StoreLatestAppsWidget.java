package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline;

import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.ws.v7.SendEventRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.events.TimelineClickEvent;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.StoreLatestAppsDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class StoreLatestAppsWidget extends CardWidget<StoreLatestAppsDisplayable> {

  private static final String CARD_TYPE_NAME = "LATEST_APPS";
  private final LayoutInflater inflater;
  private TextView title;
  private TextView subtitle;
  private LinearLayout appsContaner;
  private ImageView image;
  private View store;
  private Map<View, Long> apps;
  private LongSparseArray<String> appsPackages;
  private CardView cardView;

  public StoreLatestAppsWidget(View itemView) {
    super(itemView);
    inflater = LayoutInflater.from(itemView.getContext());
    apps = new HashMap<>();
    appsPackages = new LongSparseArray<>();
  }

  @Override String getCardTypeName() {
    return CARD_TYPE_NAME;
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
    store = itemView.findViewById(R.id.displayable_social_timeline_store_latest_apps_header);
    title = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_card_title);
    image = (ImageView) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_card_image);
    subtitle = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_card_subtitle);
    appsContaner = (LinearLayout) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_container);
    cardView =
        (CardView) itemView.findViewById(R.id.card);
  }

  @Override public void bindView(StoreLatestAppsDisplayable displayable) {
    super.bindView(displayable);

    title.setText(displayable.getStoreName());
    subtitle.setText(displayable.getTimeSinceLastUpdate(getContext()));
    setCardViewMargin(displayable, cardView);
    ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarUrl(), image);

    appsContaner.removeAllViews();
    apps.clear();
    View latestAppView;
    ImageView latestAppIcon;
    for (StoreLatestAppsDisplayable.LatestApp latestApp : displayable.getLatestApps()) {
      latestAppView = inflater.inflate(R.layout.social_timeline_latest_app, appsContaner, false);
      latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app);
      ImageLoader.load(latestApp.getIconUrl(), latestAppIcon);
      appsContaner.addView(latestAppView);
      apps.put(latestAppView, latestApp.getAppId());
      appsPackages.put(latestApp.getAppId(), latestApp.getPackageName());
    }

    for (View app : apps.keySet()) {
      compositeSubscription.add(RxView.clicks(app).subscribe(click -> {
        knockWithSixpackCredentials(displayable.getAbUrl());
        String packageName = appsPackages.get(apps.get(app));
        Analytics.AppsTimeline.clickOnCard(CARD_TYPE_NAME, packageName,
            Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
            Analytics.AppsTimeline.OPEN_APP_VIEW);
        displayable.sendClickEvent(SendEventRequest.Body.Data.builder()
            .cardType(CARD_TYPE_NAME)
            .source(TimelineClickEvent.SOURCE_APTOIDE)
            .specific(SendEventRequest.Body.Specific.builder()
                .app(packageName)
                .store(displayable.getStoreName())
                .build())
            .build(), TimelineClickEvent.OPEN_APP);
        ((FragmentShower) getContext()).pushFragmentV4(
            V8Engine.getFragmentProvider().newAppViewFragment(apps.get(app), packageName));
      }));
    }

    compositeSubscription.add(RxView.clicks(store).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      Analytics.AppsTimeline.clickOnCard(CARD_TYPE_NAME, Analytics.AppsTimeline.BLANK,
          Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
          Analytics.AppsTimeline.OPEN_STORE);
      displayable.sendClickEvent(SendEventRequest.Body.Data.builder()
          .cardType(CARD_TYPE_NAME)
          .source(TimelineClickEvent.SOURCE_APTOIDE)
          .specific(
              SendEventRequest.Body.Specific.builder().store(displayable.getStoreName()).build())
          .build(), TimelineClickEvent.OPEN_STORE);
      ((FragmentShower) getContext()).pushFragmentV4(
          V8Engine.getFragmentProvider().newStoreFragment(displayable.getStoreName()));
    }));
  }
}
