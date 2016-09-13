/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 25/08/2016.
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
	ADS(3),
	STORE_META(1, true),

	// Multi Layout
	APPS_GROUP_LIST(1),
	APPS_GROUP_GRAPHIC(1),

	// Server Complement
	HEADER_ROW(true),
	FOOTER_ROW(true),

	// App View
	APP_VIEW_INSTALL(1, true),
	APP_VIEW_RATE_AND_COMMENT(1, true),
	APP_VIEW_IMAGES(1, true),
	APP_VIEW_DESCRIPTION(1, true),
	APP_VIEW_FLAG_THIS(1, true),
	APP_VIEW_SUGGESTED_APPS(1, true),
	APP_VIEW_SUGGESTED_APP(1, true),
	APP_VIEW_DEVELOPER(1, true),

	// other versions
	OTHER_VERSION_ROW(1, true),

	// Reviews screen
	APP_COMMENT_TO_REVIEW(1, true),

	// Client
	SUBSCRIBED_STORE(2),
	ADD_MORE_STORES(true),
	SEARCH_AD(1),
	ADULT_ROW(1, true),
	UPDATES_HEADER(1, true),

	// Updates
	INSTALLED(1),
	UPDATE(1),
	EXCLUDED_UPDATE(1),
	ROLLBACK(1),
	SCHEDULED_DOWNLOAD(1),

	SOCIAL_TIMELINE(1, true),

	// Search
	SEARCH(1),

	// Progress
	PROGRESS_DISPLAYABLE,

	//Download tab
	ACTIVE_DOWNLOAD,
	ACTIVE_DOWNLOAD_HEADER(1, true),
	COMPLETED_DOWNLOAD,

	//Reviews Screen
	REVIEWS_GROUP(1, true),
	READ_MORE_COMMENTS(1, true),

	// un-used types
	APP_VIEW_COMMENTS(1, true),
	APP_VIEW_OTHER_VERSIONS(1, true),
	APP_VIEW_RATE_RESULT(1, true),
	APP_VIEW_RATE_THIS(1, true),
	APP_VIEW_SUBSCRIPTION(1, true),
	RATE_AND_REVIEW(1, true);

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
		int n = isFixedPerLineCount() ? getDefaultPerLineCount() : (int) (AptoideUtils.ScreenU.getScreenWidthInDip() /
				AptoideUtils.ScreenU.REFERENCE_WIDTH_DPI * getDefaultPerLineCount());
		return n > 0 ? n : 1;
	}

}
