/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/08/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.os.Bundle;
import android.view.View;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.activity.AptoideFragmentActivity;
import cm.aptoide.pt.v8engine.interfaces.UiComponentBasics;

/**
 * Created by neuro on 06-05-2016.
 */
public abstract class AptoideSimpleFragmentActivity extends AptoideFragmentActivity implements UiComponentBasics {

	@Override
	public void bindViews(View view) {

	}

	@Override
	public void loadExtras(Bundle extras) {

	}

	@Override
	public void setupViews() {

	}

	@Override
	public void setupToolbar() {

	}

	@Override
	public int getContentViewId() {
		return R.layout.frame_layout;
	}

	@Override
	protected String getAnalyticsScreenName() {
		return null;
	}
}
