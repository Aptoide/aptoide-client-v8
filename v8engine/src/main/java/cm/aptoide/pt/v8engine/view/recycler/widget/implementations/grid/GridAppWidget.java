/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.utils.StringUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 28/04/16.
 */
@Displayables({GridAppDisplayable.class})
public class GridAppWidget extends Widget<GridAppDisplayable> {

	private TextView name;
	private ImageView icon;
	private TextView downloads;
	private RatingBar ratingBar;
	private TextView tvStoreName;
	private TextView tvAddedTime;

	//private static final SimpleDateFormat dateFormatter =
	//		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

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

		Glide.with(V8Engine.getContext()).load(pojo.getIcon()).into(icon);

		name.setText(pojo.getName());
		downloads.setText(StringUtils.withSuffix(pojo.getStats()
				.getDownloads()) + V8Engine.getContext().getString(R.string._downloads));
		ratingBar.setRating(pojo.getStats().getRating().getAvg());
		tvStoreName.setText(pojo.getStore().getName());
		tvAddedTime.setText(pojo.getAdded());
	}
}
