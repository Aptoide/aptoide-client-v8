/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppBrickDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 09-05-2016.
 */
@Displayables({AppBrickDisplayable.class})
public class AppBrickWidget extends Widget<AppBrickDisplayable> {

	private ImageView graphic;

	public AppBrickWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		graphic = (ImageView) itemView.findViewById(R.id.featured_graphic);
	}

	@Override
	public void bindView(AppBrickDisplayable displayable) {
		Glide.with(itemView.getContext())
				.load(displayable.getPojo().getGraphic())
				.placeholder(R.drawable.placeholder_705x345)
				.into(graphic);


//		itemView.setOnClickListener(new BaseAdapter.AppItemOnClickListener(appItem));
	}
}
