package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.view.timeline.displayable.StoreLatestAppsDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class StoreLatestAppsWidget extends CardWidget<StoreLatestAppsDisplayable> {

  private final LayoutInflater inflater;
  private TextView title;
  private TextView subtitle;
  private LinearLayout appsContainer;
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

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
    store = itemView.findViewById(R.id.displayable_social_timeline_store_latest_apps_header);
    title = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_card_title);
    image = (ImageView) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_card_image);
    subtitle = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_card_subtitle);
    appsContainer = (LinearLayout) itemView.findViewById(
        R.id.displayable_social_timeline_store_latest_apps_container);
    cardView = (CardView) itemView.findViewById(R.id.card);
  }

  @Override public void bindView(StoreLatestAppsDisplayable displayable) {
    super.bindView(displayable);
    final FragmentActivity context = getContext();
    title.setText(displayable.getStyledTitle(context));
    subtitle.setText(displayable.getTimeSinceLastUpdate(context));
    setCardViewMargin(displayable, cardView);
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(displayable.getAvatarUrl(), image);

    appsContainer.removeAllViews();
    apps.clear();
    View latestAppView;
    ImageView latestAppIcon;
    TextView latestAppName;
    for (StoreLatestAppsDisplayable.LatestApp latestApp : displayable.getLatestApps()) {
      latestAppView = inflater.inflate(R.layout.social_timeline_latest_app, appsContainer, false);
      latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app_icon);
      latestAppName = (TextView) latestAppView.findViewById(R.id.social_timeline_latest_app_name);
      ImageLoader.with(context)
          .load(latestApp.getIconUrl(), latestAppIcon);
      latestAppName.setText(latestApp.getName());
      appsContainer.addView(latestAppView);
      apps.put(latestAppView, latestApp.getAppId());
      appsPackages.put(latestApp.getAppId(), latestApp.getPackageName());
    }

    for (View app : apps.keySet()) {
      compositeSubscription.add(RxView.clicks(app)
          .subscribe(click -> {
            knockWithSixpackCredentials(displayable.getAbUrl());
            String packageName = appsPackages.get(apps.get(app));
            Analytics.AppsTimeline.clickOnCard(StoreLatestAppsDisplayable.CARD_TYPE_NAME,
                packageName, Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
                Analytics.AppsTimeline.OPEN_APP_VIEW);
            displayable.sendOpenAppEvent(packageName);
            getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
                .newAppViewFragment(apps.get(app), packageName));
          }));
    }

    compositeSubscription.add(RxView.clicks(store)
        .subscribe(click -> {
          knockWithSixpackCredentials(displayable.getAbUrl());
          Analytics.AppsTimeline.clickOnCard(StoreLatestAppsDisplayable.CARD_TYPE_NAME,
              Analytics.AppsTimeline.BLANK, Analytics.AppsTimeline.BLANK,
              displayable.getStoreName(), Analytics.AppsTimeline.OPEN_STORE);
          displayable.sendOpenStoreEvent();
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newStoreFragment(displayable.getStoreName(), displayable.getStoreTheme()));
        }));
  }

  @Override String getCardTypeName() {
    return StoreLatestAppsDisplayable.CARD_TYPE_NAME;
  }
}
