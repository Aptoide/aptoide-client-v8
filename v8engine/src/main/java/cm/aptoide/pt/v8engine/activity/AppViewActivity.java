/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.support.v4.app.Fragment;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewActivity extends AptoideSimpleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return null;
	}

	@Override
	protected int getContentViewId() {
		return 0;
	}

	@Override
	protected String getAnalyticsScreenName() {
		return null;
	}
}
