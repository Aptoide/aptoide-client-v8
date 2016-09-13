/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 28/04/16.
 */
@Displayables({GridAppDisplayable.class})
public class GridAppWidget extends Widget<GridAppDisplayable> {

	private static final AptoideUtils.DateTimeU DATE_TIME_U = AptoideUtils.DateTimeU.getInstance();

	private TextView name;
	private ImageView icon;
	private TextView downloads;
	private RatingBar ratingBar;
	private TextView tvStoreName;
	private TextView tvAddedTime;
	private String storeTheme;

	public GridAppWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(@NonNull View view) {
		name = (TextView) itemView.findViewById(R.id.name);
		icon = (ImageView) itemView.findViewById(R.id.icon);
		downloads = (TextView) itemView.findViewById(R.id.downloads);
		ratingBar = (RatingBar) itemView.findViewById(R.id.ratingbar);
		tvStoreName = (TextView) itemView.findViewById(R.id.store_name);
		tvAddedTime = (TextView) itemView.findViewById(R.id.added_time);
	}

	@Override
	public void bindView(GridAppDisplayable displayable) {
		final App pojo = displayable.getPojo();
		final long appId = pojo.getId();

		ImageLoader.load(pojo.getIcon(), icon);

		name.setText(pojo.getName());
		downloads.setText(AptoideUtils.StringU.withSuffix(pojo.getStats()
				.getDownloads()) + V8Engine.getContext().getString(R.string._downloads));
		ratingBar.setRating(pojo.getStats().getRating().getAvg());
		tvStoreName.setText(pojo.getStore().getName());
		tvAddedTime.setText(DATE_TIME_U.getTimeDiffString(getContext(), pojo.getAdded().getTime()));
		/*try {
			storeTheme = pojo.getStore().getAppearance().getTheme();
		} catch (NullPointerException e) {
			storeTheme = "none";
		}*/

		itemView.setOnClickListener(
				v -> {
					// FIXME
					((FragmentShower) v.getContext()).pushFragmentV4(
							AppViewFragment.newInstance(appId, pojo.getStore().getAppearance().getTheme(), tvStoreName.getText().toString())
					);
				}
		);
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
