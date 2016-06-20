/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewSuggestedAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 04/05/16.
 */
@Displayables({AppViewSuggestedAppsDisplayable.class})
public class AppViewSuggestedAppsWidget extends Widget<AppViewSuggestedAppsDisplayable> {

	private ImageView appIcon;
	private TextView appName;
	private RatingBar ratingBar;
	private TextView size;
	private TextView sponsored;
	private TextView description;
	private Button installButton;

	private SuggestedAppExtraInfo extraInfoLayout;

	public AppViewSuggestedAppsWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
		appName = (TextView) itemView.findViewById(R.id.app_name);
		ratingBar = (RatingBar) itemView.findViewById(R.id.rating_label);
		size = (TextView) itemView.findViewById(R.id.size_value);
		sponsored = (TextView) itemView.findViewById(R.id.sponsored);
		description = (TextView) itemView.findViewById(R.id.description);
		installButton = (Button) itemView.findViewById(R.id.btinstall);

		extraInfoLayout = new SuggestedAppExtraInfo(itemView);
	}

	@Override
	public void bindView(AppViewSuggestedAppsDisplayable displayable) {
		final GetApp pojo = displayable.getPojo();

		if (extraInfoLayout != null) {
			extraInfoLayout.bindView(pojo);
		}

		// TODO
	}

	private static final class SuggestedAppExtraInfo {

		private View extraInfoLayout;
		private ImageView badge;
		private TextView text;

		public SuggestedAppExtraInfo(View view) {
			extraInfoLayout = view.findViewById(R.id.extra_info_layout);
			badge = (ImageView) extraInfoLayout.findViewById(R.id.app_badge);
			text = (TextView) extraInfoLayout.findViewById(R.id.app_badge_text);
		}

		public void bindView(GetApp getApp) {
			// TODO
		}
	}
}
