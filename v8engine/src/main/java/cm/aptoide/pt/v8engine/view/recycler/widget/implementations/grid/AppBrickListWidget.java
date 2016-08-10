/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppGraphicDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 09-05-2016.
 */
public class AppBrickListWidget extends Widget<GridAppGraphicDisplayable> {

	private TextView name;
	private ImageView graphic;
	private RatingBar ratingBar;

	public AppBrickListWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		name = (TextView) itemView.findViewById(R.id.app_name);
		graphic = (ImageView) itemView.findViewById(R.id.featured_graphic);
		ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
	}

	@Override
	public void bindView(GridAppGraphicDisplayable displayable) {
		App app = displayable.getPojo();

		ImageLoader.load(app.getGraphic(), R.drawable.placeholder_705x345, graphic);
		name.setText(app.getName());
		ratingBar.setRating(app.getStats().getRating().getAvg());
		itemView.setOnClickListener(v -> {
			((FragmentShower) v.getContext()).pushFragmentV4(AppViewFragment.newInstance(app.getId()));
			Analytics.HomePageEditorsChoice.clickOnEditorsChoiceItem(getAdapterPosition(),app.getPackageName(),false);
		});
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
