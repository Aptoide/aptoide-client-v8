/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 16/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.annotation.LayoutRes;

import cm.aptoide.pt.v8engine.util.ScreenUtils;

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

	@LayoutRes
	public abstract int getViewType();

	public int getPerLineCount() {
		return fixedPerLineCount ? getDefaultPerLineCount() : (int) (ScreenUtils.getScreenWidthInDip() / REFERENCE_WIDTH_DPI * getDefaultPerLineCount());
	}

	public abstract int getDefaultPerLineCount();

	/**
	 * Only to enforce the DisplayableEnum definition.
	 *
	 * @return the DisplayableEnum which identifies this widget.
	 */
	public abstract WidgetEnum getEnum();
}
