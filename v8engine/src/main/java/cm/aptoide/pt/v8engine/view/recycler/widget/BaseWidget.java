/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 26/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.view.View;

import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by sithengineer on 26/07/16.
 */
public abstract class BaseWidget<T extends Displayable> extends Widget<T> {
	
	public BaseWidget(View itemView) {
		super(itemView);
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
