/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.activity.deprecated;

import android.support.v7.widget.Toolbar;
import android.view.View;

import java.lang.ref.WeakReference;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.activity.AptoideFragmentActivity;

/**
 * Created by neuro on 05-05-2016.
 */
public abstract class AptoideSimpleFragmentActivityDeprecated extends AptoideFragmentActivity {

	protected WeakReference<Toolbar> weakToolbar;

	@Override
	public void bindViews(View view) {
		if (weakToolbar == null || weakToolbar.get() == null) {
			weakToolbar = new WeakReference<>((Toolbar) findViewById(R.id.toolbar));
		}
	}

	@Override
	public void setupViews() {

	}

	@Override
	public void setupToolbar() {
		Toolbar toolbar = weakToolbar !=null ? weakToolbar.get() : null;
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			toolbar.setLogo(R.drawable.ic_aptoide_toolbar);
		}
	}
}
