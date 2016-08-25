package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.HashMap;
import java.util.Map;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.timeline.StoreLatestApps;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FeatureDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class StoreLatestAppsWidget extends Widget<StoreLatestAppsDisplayable> {

	private final LayoutInflater inflater;
	private TextView title;
	private TextView subtitle;
	private LinearLayout appsContaner;
	private ImageView image;
	private View store;
	private StoreLatestAppsDisplayable displayable;
	private Map<View, Long> apps;
	private Map<Long, String> appsPackages;
	private CompositeSubscription subscriptions;
	private CardView cardView;

	public StoreLatestAppsWidget(View itemView) {
		super(itemView);
		inflater = LayoutInflater.from(itemView.getContext());
		apps = new HashMap<>();
		appsPackages = new HashMap<>();
	}

	@Override
	protected void assignViews(View itemView) {
		store = itemView.findViewById(R.id.displayable_social_timeline_store_latest_apps_header);
		title = (TextView)itemView.findViewById(R.id.displayable_social_timeline_store_latest_apps_card_title);
		image = (ImageView)itemView.findViewById(R.id.displayable_social_timeline_store_latest_apps_card_image);
		subtitle = (TextView)itemView.findViewById(R.id.displayable_social_timeline_store_latest_apps_card_subtitle);
		appsContaner = (LinearLayout)itemView.findViewById(R.id.displayable_social_timeline_store_latest_apps_container);
		cardView = (CardView) itemView.findViewById(R.id.displayable_social_timeline_store_latest_apps_card);
	}

	@Override
	public void bindView(StoreLatestAppsDisplayable displayable) {
		this.displayable = displayable;
		title.setText(displayable.getStoreName());
		subtitle.setText(displayable.getTimeSinceLastUpdate(getContext()));
		setCardviewMargin(displayable);
		ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarUrl(), image);

		appsContaner.removeAllViews();
		apps.clear();
		View latestAppView;
		ImageView latestAppIcon;
		for (StoreLatestAppsDisplayable.LatestApp latestApp: displayable.getLatestApps()) {
			latestAppView = inflater.inflate(R.layout.social_timeline_latest_app, appsContaner, false);
			latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app);
			ImageLoader.load(latestApp.getIconUrl(), latestAppIcon);
			appsContaner.addView(latestAppView);
			apps.put(latestAppView, latestApp.getAppId());
			appsPackages.put(latestApp.getAppId(), latestApp.getPackageName());
		}
	}

	private void setCardviewMargin(StoreLatestAppsDisplayable displayable) {
		CardView.LayoutParams layoutParams = new CardView.LayoutParams(
				CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(displayable.getMarginWidth(getContext(), getContext().getResources().getConfiguration().orientation),0,displayable
				.getMarginWidth
						(getContext(), getContext().getResources().getConfiguration().orientation),30);
		cardView.setLayoutParams(layoutParams);
	}

	@Override
	public void onViewAttached() {
		if (subscriptions == null) {
			subscriptions = new CompositeSubscription();

			for (View app : apps.keySet()) {
				subscriptions.add(RxView.clicks(app)
						.subscribe(click -> {
							Analytics.AppsTimeline.clickOnCard("Latest Apps", appsPackages.get(apps.get(app)), Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
									Analytics
									.AppsTimeline.OPEN_APP_VIEW);
							((FragmentShower) getContext()).pushFragmentV4(AppViewFragment.newInstance(apps.get(app)));
						}));
			}

			subscriptions.add(RxView.clicks(store)
					.subscribe(click -> {
						Analytics.AppsTimeline.clickOnCard("Latest Apps", Analytics.AppsTimeline.BLANK, Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
								Analytics
								.AppsTimeline.OPEN_STORE);
						((FragmentShower) getContext()).pushFragmentV4(StoreFragment.newInstance(displayable.getStoreName()));
					}));
		}
	}

	@Override
	public void onViewDetached() {
		if (subscriptions != null) {
			subscriptions.unsubscribe();
			subscriptions = null;
		}
	}
}
