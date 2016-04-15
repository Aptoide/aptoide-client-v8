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
public abstract class Displayable<T> {

	private static final float REFERENCE_WIDTH_DPI = 360;
	private final T pojo;

	/**
	 * Needed for reflective {@link Class#newInstance()}.
	 */
	public Displayable() {
		pojo = null;
	}

	public Displayable(T pojo) {
		this.pojo = pojo;
	}

	@LayoutRes
	public abstract int getViewType();

	public int getPerLineCount() {
		return (int) (ScreenUtils.getScreenWidthInDip() / REFERENCE_WIDTH_DPI * getDefaultPerLineCount());
	}

	public abstract int getDefaultPerLineCount();

	/**
	 * Only to enforce the DisplayableEnum definition.
	 *
	 * @return the DisplayableEnum which identifies this widget.
	 */
	public abstract WidgetEnum getEnum();
}
