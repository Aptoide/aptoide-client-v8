package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.StoreLatestAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class StoreLatestAppsWidget extends Widget<StoreLatestAppsDisplayable> {

	private final LayoutInflater inflater;
	private TextView title;
	private TextView subtitle;
	private LinearLayout appsContaner;
	private ImageView image;

	public StoreLatestAppsWidget(View itemView) {
		super(itemView);
		inflater = LayoutInflater.from(itemView.getContext());
	}

	@Override
	protected void assignViews(View itemView) {
		title = (TextView)itemView.findViewById(R.id.card_title);
		image = (ImageView)itemView.findViewById(R.id.card_image);
		subtitle = (TextView)itemView.findViewById(R.id.card_subtitle);
		appsContaner = (LinearLayout)itemView.findViewById(R.id.displayable_social_timeline_store_latest_apps_container);
	}

	@Override
	public void bindView(StoreLatestAppsDisplayable displayable) {
		title.setText(displayable.getTitle());
		subtitle.setText(displayable.getHoursSinceLastUpdate(getContext()));
		ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarUrl(), image);

		View latestAppView;
		ImageView latestAppIcon;
		for (StoreLatestAppsDisplayable.LatestApp latestApp: displayable.getLatestApps()) {
			latestAppView = inflater.inflate(R.layout.social_timeline_latest_app, appsContaner, false);
			latestAppIcon = (ImageView) latestAppView.findViewById(R.id.social_timeline_latest_app);
			latestAppIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((FragmentShower) getContext()).pushFragmentV4(AppViewFragment.newInstance(latestApp.getAppId()));
				}
			});
			ImageLoader.load(latestApp.getIconUrl(), latestAppIcon);
			appsContaner.addView(latestAppView);
		}
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {
		appsContaner.removeAllViews();
	}
}
