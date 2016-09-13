/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 30/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppListDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 29-06-2016.
 */
public class GridAppListWidget extends Widget<GridAppListDisplayable> {

	public TextView name;
	public ImageView icon;
	public TextView tvTimeSinceModified;
	public TextView tvStoreName;

	public GridAppListWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		name = (TextView) itemView.findViewById(R.id.name);
		icon = (ImageView) itemView.findViewById(R.id.icon);
		tvTimeSinceModified = (TextView) itemView.findViewById(R.id.timeSinceModified);
		tvStoreName = (TextView) itemView.findViewById(R.id.storeName);
	}

	@Override
	public void bindView(GridAppListDisplayable displayable) {

		App app = displayable.getPojo();
		name.setText(app.getName());

		Date modified = app.getUpdated();
		if (modified != null) {
			tvTimeSinceModified.setText(AptoideUtils.DateTimeU.getInstance(itemView.getContext()).getTimeDiffString(itemView.getContext(), modified.getTime
					()));
		}

		name.setText(app.getName());
		name.setTypeface(null, Typeface.BOLD);

		tvStoreName.setText(app.getStore().getName());
		tvStoreName.setTypeface(null, Typeface.BOLD);
		itemView.setOnClickListener(v -> {
			// FIXME
			((FragmentShower) v.getContext()).pushFragmentV4(AppViewFragment.newInstance(app.getId()));
		});
		ImageLoader.load(app.getIcon(), icon);
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
