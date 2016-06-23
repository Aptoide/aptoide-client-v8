package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class AppUpdateWidget extends Widget<AppUpdateDisplayable> {

	private TextView appName;
	private TextView appVersion;
	private ImageView appIcon;
	private TextView appUpdate;

	private Button updateButton;

	public AppUpdateWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		appName = (TextView)itemView.findViewById(R.id.displayable_social_timeline_app_update_name);
		appIcon = (ImageView)itemView.findViewById(R.id.displayable_social_timeline_app_update_icon);
		appVersion = (TextView)itemView.findViewById(R.id.displayable_social_timeline_app_update_version);
		updateButton = (Button) itemView.findViewById(R.id.displayable_social_timeline_app_update_button);
		appUpdate = (TextView)itemView.findViewById(R.id.displayable_social_timeline_app_update);
	}

	@Override
	public void bindView(AppUpdateDisplayable displayable) {

		appName.setText(getContext().getString(R.string.displayable_social_timeline_app_update_name,
				displayable.getAppName()));
		appUpdate.setText(getContext().getString(R.string.displayable_social_timeline_app_has_update,
				getContext().getString(R.string.displayable_social_timeline_app_update)));
		appVersion.setText(getContext().getString(R.string.displayable_social_timeline_app_update_version,
				displayable.getVersion()));
		updateButton.setText(getContext().getString(R.string.displayable_social_timeline_app_update_button,
				getContext().getString(R.string.displayable_social_timeline_app_update_application)));

		ImageLoader.load(displayable.getIconUrl(), appIcon);

		updateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((FragmentShower) getContext()).pushFragmentV4(AppViewFragment.newInstance(displayable.getAppId()));
			}
		});
	}
}
