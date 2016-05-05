/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.v8engine.activity;

import android.support.v7.widget.Toolbar;

import java.lang.ref.WeakReference;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 05-05-2016.
 */
public abstract class AptoideSimpleFragmentActivity extends AptoideFragmentActivity {

	protected WeakReference<Toolbar> weakToolbar;

	@Override
	protected void setupViews() {

	}

	@Override
	protected void setupToolbar() {
		Toolbar toolbar = weakToolbar !=null ? weakToolbar.get() : null;
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			toolbar.setLogo(R.drawable.ic_aptoide_toolbar);
		}
	}

	@Override
	protected void bindViews() {
		if(weakToolbar==null || weakToolbar.get() == null) {
			weakToolbar = new WeakReference<>((Toolbar) findViewById(R.id.toolbar));
		}
	}
}
