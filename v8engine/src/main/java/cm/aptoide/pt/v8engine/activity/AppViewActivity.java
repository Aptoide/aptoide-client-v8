/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.support.v4.app.Fragment;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragments.implementations.AppViewFragment;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewActivity extends AptoideSimpleFragmentActivity {

	@Override
	protected int getContentViewId() {
		return R.layout.activity_app_view;
	}

	@Override
	protected String getAnalyticsScreenName() {
		return null;
	}

	@Override
	protected Fragment createFragment() {
		// TODO
		return AppViewFragment.newInstance("");
	}
}
