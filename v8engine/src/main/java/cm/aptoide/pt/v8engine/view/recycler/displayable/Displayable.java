/*
 * Copyright (c) 2016.
<<<<<<< HEAD
 * Modified by SithEngineer on 09/05/2016.
=======
 * Modified by Neurophobic Animal on 10/05/2016.
>>>>>>> develop
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.support.annotation.LayoutRes;

import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.model.Model;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.utils.ScreenUtils;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 14-04-2016.
 */
@Ignore
@Accessors(chain = true)
public abstract class Displayable {

	@Setter private boolean fixedPerLineCount = false;

	private boolean useTypeDefaultCount = true;
	private int defaultPerLineCount = 1;

	/**
	 * Needed for reflective {@link Class#newInstance()}.
	 */
	public Displayable() { }

	public Displayable(boolean fixedPerLineCount) {
		this.fixedPerLineCount = fixedPerLineCount;
	}

	public abstract Type getType();

	@LayoutRes
	public abstract int getViewLayout();

	/**
	 * Same code as in {@link Type#getPerLineCount()} todo: terminar este doc
	 *
	 * @return
	 */
	public int getPerLineCount() {
		return isFixedPerLineCount() ? getDefaultPerLineCount() : (int) (ScreenUtils
				.getScreenWidthInDip(Model
				.getContext()) / ScreenUtils.REFERENCE_WIDTH_DPI * getDefaultPerLineCount());
	}

	public boolean isFixedPerLineCount() {
		return getType() != null ?  getType().isFixedPerLineCount() : fixedPerLineCount;
	}

	public int getDefaultPerLineCount() {
		if (getType() != null && useTypeDefaultCount) {
			return getType().getDefaultPerLineCount();
		}
		return defaultPerLineCount;
	}

	public int getSpanSize() {
		return WidgetFactory.getColumnSize() / getPerLineCount();
	}

	public Displayable setDefaultPerLineCount(int defaultPerLineCount) {
		this.defaultPerLineCount = defaultPerLineCount;
		this.useTypeDefaultCount = false;
		return this;
	}
}
