/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations;

import android.view.View;
import android.widget.ImageView;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.DisplayGridDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 02/05/16.
 */
@Displayables({DisplayGridDisplayable.class})
public class DisplayGridWidget extends Widget<DisplayGridDisplayable> {

	private ImageView imageView;

	public DisplayGridWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		imageView = (ImageView) itemView.findViewById(R.id.image_category);
	}

	@Override
	public void bindView(DisplayGridDisplayable displayable) {
		Object pojo = displayable.getPojo();
		// TODO
	}
}
