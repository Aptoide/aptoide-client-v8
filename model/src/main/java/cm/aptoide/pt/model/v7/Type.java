/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.model.v7;

import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Getter;

/**
 * Created by neuro on 06-05-2016.
 */
public enum Type {
	_EMPTY, // FIXME for tests only

	// Server
	APPS_GROUP(3),
	APP_BRICK(2, true),
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

	// Client
	SUBSCRIBED_STORE(2),
	ADD_MORE_STORES(true),

	// Updates
	INSTALLED(1),
	UPDATE(1),
	ROLLBACK(1),

	SOCIAL_TIMELINE(1),

	// Search
	SEARCH(1),

	// Progress
	PROGRESS_DISPLAYABLE;

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
		return fixedPerLineCount ? getDefaultPerLineCount() : (int) (AptoideUtils.ScreenU.getScreenWidthInDip() /
				AptoideUtils.ScreenU.REFERENCE_WIDTH_DPI * getDefaultPerLineCount());
	}

}
