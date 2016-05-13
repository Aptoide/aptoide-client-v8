/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;

import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewRateResultsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 04/05/16.
 */
@Displayables({AppViewRateResultsDisplayable.class})
public class AppViewRateResultsWidget extends Widget<AppViewRateResultsDisplayable> {

	public AppViewRateResultsWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {

	}

	@Override
	public void bindView(AppViewRateResultsDisplayable displayable) {
		final Object pojo = displayable.getPojo();
		// TODO
	}
}
