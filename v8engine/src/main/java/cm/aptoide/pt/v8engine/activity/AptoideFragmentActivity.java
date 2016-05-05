/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.support.v4.app.Fragment;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 05-05-2016.
 */
public abstract class AptoideFragmentActivity extends AptoideBaseActivity {

	private Fragment fragment;

	protected abstract Fragment createFragment();

	@Override
	protected void onStop() {
		super.onStop();
		getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
		fragment = null;
	}

	@Override
	protected void onStart() {
		super.onStart();
		fragment = createFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, fragment).commit();
	}
}
