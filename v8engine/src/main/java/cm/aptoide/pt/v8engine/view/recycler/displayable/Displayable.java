/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.support.annotation.LayoutRes;

import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.utils.ScreenUtils;
import cm.aptoide.pt.v8engine.Aptoide;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;

/**
 * Created by neuro on 14-04-2016.
 */
@Ignore
public abstract class Displayable {

	private static final float REFERENCE_WIDTH_DPI = 360;
	private boolean fixedPerLineCount;

	/**
	 * Needed for reflective {@link Class#newInstance()}.
	 */
	public Displayable() {
		this(false);
	}

	public Displayable(boolean fixedPerLineCount) {
		this.fixedPerLineCount = fixedPerLineCount;
	}

	public abstract Type getType();

	@LayoutRes
	public abstract int getViewLayout();

	public int getPerLineCount() {
		return fixedPerLineCount ? getDefaultPerLineCount() : (int) (ScreenUtils
				.getScreenWidthInDip(Aptoide
				.getContext()) / REFERENCE_WIDTH_DPI * getDefaultPerLineCount());
	}

	public abstract int getDefaultPerLineCount();

	public int getSpanSize() {
		return WidgetFactory.getColumnSize() / getPerLineCount();
	}
}
