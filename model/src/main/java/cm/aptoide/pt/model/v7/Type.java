/*
 * Copyright (c) 2016.
<<<<<<< HEAD
 * Modified by SithEngineer on 10/05/2016.
=======
 * Modified by Neurophobic Animal on 11/05/2016.
>>>>>>> develop
 */

package cm.aptoide.pt.model.v7;

import cm.aptoide.pt.model.Model;
import cm.aptoide.pt.utils.ScreenUtils;
import lombok.Getter;

/**
 * Created by neuro on 06-05-2016.
 */
public enum Type {
	_EMPTY, // FIXME for tests only

	// Server
	APPS_GROUP(3),
	APP_BRICK(2),
	STORES_GROUP(2),
	DISPLAYS(2, true),

	// Server Complement
	HEADER_ROW(true),
	FOOTER_ROW(true),

	// App View
	APP_VIEW_INSTALL,
	APP_VIEW_COMMENTS,
	APP_VIEW_DEVELOPER,
	APP_VIEW_OTHER_VERSIONS,
	APP_VIEW_RATE_RESULT,
	APP_VIEW_RATE_THIS,
	APP_VIEW_SUGGESTED_APPS,
	APP_VIEW_SUBSCRIPTION,
	APP_VIEW_DESCRIPTION,
	APP_VIEW_IMAGES,

	GROUP,

	// Client
	SUBSCRIBED_STORE(2),
	ADD_MORE_STORES(true);

	private static final int DEFAULT_PER_LINE_COUNT = 1;

	@Getter private int defaultPerLineCount;
	@Getter private boolean fixedPerLineCount;

	Type() {
		this(DEFAULT_PER_LINE_COUNT);
	}

	Type(boolean fixedPerLineCount) {
		this(DEFAULT_PER_LINE_COUNT, fixedPerLineCount);
	}

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
