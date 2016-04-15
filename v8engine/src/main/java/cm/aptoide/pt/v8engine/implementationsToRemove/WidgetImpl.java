/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 16/04/2016.
 */

package cm.aptoide.pt.v8engine.implementationsToRemove;

import android.view.View;

import cm.aptoide.pt.v8engine.view.recycler.widget.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetEnum;

/**
 * Created by neuro on 14-04-2016.
 */
public class WidgetImpl extends Widget {

	public WidgetImpl(View view) {
		super(view);
	}

	@Override
	public void bindView(Displayable displayable) {

	}

	@Override
	public WidgetEnum getEnum() {
		return WidgetEnum.WIDGET_IMPL;
	}
}
