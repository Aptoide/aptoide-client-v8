/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;

import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewOtherVersionsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 04/05/16.
 */
@Displayables({AppViewOtherVersionsDisplayable.class})
public class AppViewOtherVersionsWidget extends Widget<AppViewOtherVersionsDisplayable> {

	public AppViewOtherVersionsWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {

	}

	@Override
	public void bindView(AppViewOtherVersionsDisplayable displayable) {
		final Object pojo = displayable.getPojo();
		// TODO
	}
}
