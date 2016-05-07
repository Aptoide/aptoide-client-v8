/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/05/2016.
 */

package cm.aptoide.pt.v8engine.activities;

import android.os.Bundle;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.activity.AptoideFragmentActivity;

/**
 * Created by neuro on 06-05-2016.
 */
public abstract class AptoideSimpleFragmentActivity extends AptoideFragmentActivity {

	@Override
	protected void loadExtras(Bundle extras) {

	}

	@Override
	protected void setupViews() {

	}

	@Override
	protected void setupToolbar() {

	}

	@Override
	protected void bindViews() {

	}

	@Override
	protected int getContentViewId() {
		return R.layout.frame_layout;
	}

	@Override
	protected String getAnalyticsScreenName() {
		return null;
	}
}
