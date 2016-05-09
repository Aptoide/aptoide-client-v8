/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewActivity extends AptoideSimpleFragmentActivity {

	@Override
	protected void loadExtras(Bundle extras) {
	}

	@Override
	protected int getContentViewId() {
		return R.layout.frame_layout;
	}

	@Override
	protected String getAnalyticsScreenName() {
		return null;
	}

	@Override
	protected Fragment createFragment() {
		// FIXME for debug only
		return AppViewFragment.newInstance(18696152);
	}
}
