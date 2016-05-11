/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 11/05/2016.
 */

package cm.aptoide.pt.model.v7;

import cm.aptoide.pt.model.Model;
import cm.aptoide.pt.utils.ScreenUtils;
import lombok.Getter;

/**
 * Created by neuro on 06-05-2016.
 */
public enum Type {
	_EMPTY(1), // FIXME for tests only

	// Server
	APPS_GROUP(3),
	APP_BRICK(2),
	STORES_GROUP(2),
	DISPLAYS(2, true),

	// Server Complement
	HEADER_ROW(1),
	FOOTER_ROW(1),

	// Client
	SUBSCRIBED_STORE(2),
	ADD_MORE_STORES(1, true);

	@Getter private int defaultPerLineCount;
	@Getter private boolean fixedPerLineCount;

	Type(int defaultPerLineCount) {
		this(defaultPerLineCount, false);
	}

	Type(int defaultPerLineCount, boolean fixedPerLineCount) {
		this.defaultPerLineCount = defaultPerLineCount;
		this.fixedPerLineCount = fixedPerLineCount;
	}

	public int getPerLineCount() {
		return fixedPerLineCount ? getDefaultPerLineCount() : (int) (ScreenUtils
				.getScreenWidthInDip(Model
				.getContext()) / ScreenUtils.REFERENCE_WIDTH_DPI * getDefaultPerLineCount());
	}
}
