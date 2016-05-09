/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/05/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.activity.deprecated.AptoideSimpleFragmentActivityDeprecated;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewActivity extends AptoideSimpleFragmentActivityDeprecated {

	@Override
	protected Fragment createFragment() {
		return null;
	}

	@Override
	protected void loadExtras(Bundle extras) {

	}

	@Override
	protected int getContentViewId() {
		return R.layout.activity_app_view;
	}

	@Override
	protected String getAnalyticsScreenName() {
		return null;
	}
}
