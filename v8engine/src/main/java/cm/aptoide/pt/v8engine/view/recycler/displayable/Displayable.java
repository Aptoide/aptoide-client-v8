/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.support.annotation.LayoutRes;

import cm.aptoide.pt.v8engine.util.ScreenUtils;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class Displayable {

	private static final float REFERENCE_WIDTH_DPI = 360;
	private boolean fixedPerLineCount = false;

	/**
	 * Needed for reflective {@link Class#newInstance()}.
	 */
	public Displayable() {
	}

	public Displayable(boolean fixedPerLineCount) {
		this.fixedPerLineCount = fixedPerLineCount;
	}

	public abstract String getName();

	// shouldn't this be named "getViewId()" ??
	@LayoutRes
	public abstract int getViewType();

	public int getPerLineCount() {
		return fixedPerLineCount ? getDefaultPerLineCount() : (int) (ScreenUtils.getScreenWidthInDip() / REFERENCE_WIDTH_DPI * getDefaultPerLineCount());
	}

	public abstract int getDefaultPerLineCount();

	public int getSpanSize() {
		return WidgetFactory.getColumnSize() / getPerLineCount();
	}
}
