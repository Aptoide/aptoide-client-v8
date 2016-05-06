/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridDisplayDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 02/05/16.
 */
@Displayables({GridDisplayDisplayable.class})
public class GridDisplayWidget extends Widget<GridDisplayDisplayable> {

	private ImageView imageView;

	public GridDisplayWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		imageView = (ImageView) itemView.findViewById(R.id.image_category);
	}

	@Override
	public void bindView(GridDisplayDisplayable displayable) {
		Object pojo = displayable.getPojo();
		// TODO
		Glide.with(itemView.getContext()).load(displayable.getPojo().graphic).into(imageView);
	}
}
