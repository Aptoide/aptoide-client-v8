/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;

import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRatingDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 04/05/16.
 */
@Displayables({AppViewRatingDisplayable.class})
public class AppViewRatingWidget extends Widget<AppViewRatingDisplayable> {

	public AppViewRatingWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {

	}

	@Override
	public void bindView(AppViewRatingDisplayable displayable) {
		final Object pojo = displayable.getPojo();
		// TODO
	}
}
