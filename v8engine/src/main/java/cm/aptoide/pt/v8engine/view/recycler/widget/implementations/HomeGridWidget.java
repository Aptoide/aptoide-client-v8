/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations;

import android.view.View;

import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.HomeGridDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 28/04/16.
 */
@Displayables({HomeGridDisplayable.class})
public class HomeGridWidget extends Widget<HomeGridDisplayable> {

	public HomeGridWidget(View itemView) {
		super(itemView);
	}

	@Override
	public void bindView(HomeGridDisplayable displayable) {
		// TODO
	}
}
