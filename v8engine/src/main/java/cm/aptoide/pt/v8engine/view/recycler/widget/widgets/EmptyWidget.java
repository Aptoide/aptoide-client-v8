/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 18/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.widgets;

import android.view.View;

import cm.aptoide.pt.v8engine.view.recycler.widget.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetEnum;

/**
 * Created by neuro on 14-04-2016.
 */
public class EmptyWidget extends Widget {

	public EmptyWidget(View view) {
		super(view);
	}

	@Override
	public void bindView(Displayable displayable) {
	}

	@Override
	public WidgetEnum getEnum() {
		return WidgetEnum.EMPTY;
	}
}
