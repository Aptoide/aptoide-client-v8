/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewActivity extends AptoideSimpleFragmentActivity {

	private long appId;

	public static Intent getIntent(Context ctx, long appId) {
		Intent intent = new Intent(ctx, AppViewActivity.class);
		intent.putExtra(BundleKeys.APP_ID.name(), appId);
		return intent;
	}

	@Override
	protected void loadExtras(Bundle extras) {
		appId = extras.getLong(BundleKeys.APP_ID.name());
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
		return AppViewFragment.newInstance(appId);
	}

	private enum BundleKeys {
		APP_ID
	}
}
