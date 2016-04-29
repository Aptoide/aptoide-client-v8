/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.AppGridDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 28/04/16.
 */
@Displayables({AppGridDisplayable.class})
public class AppGridWidget extends Widget<AppGridDisplayable> {

	private TextView name;
	private ImageView icon;
	private TextView downloads;
	private RatingBar ratingBar;
	private TextView tvStoreName;
	private TextView tvAddedTime;

	//private static final SimpleDateFormat dateFormatter =
	//		new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

	public AppGridWidget(View itemView) {
		super(itemView);
	}

	@Override
	public void bindView(AppGridDisplayable displayable) {
		App pojo = displayable.getPojo();
		name.setText(pojo.getName());
	}

	@Override
	protected void assignViews(@NonNull View view) {
		name = (TextView )itemView.findViewById(R.id.name);
		icon = (ImageView )itemView.findViewById(R.id.icon);
		downloads = (TextView )itemView.findViewById(R.id.downloads);
		ratingBar = (RatingBar )itemView.findViewById(R.id.ratingbar);
		tvStoreName = (TextView )itemView.findViewById(R.id.store_name);
		tvAddedTime = (TextView )itemView.findViewById(R.id.added_time);
	}

}
