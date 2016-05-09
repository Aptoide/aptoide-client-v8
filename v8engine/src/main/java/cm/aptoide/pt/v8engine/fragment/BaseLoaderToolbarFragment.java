/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 09/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 06-05-2016.
 */
public abstract class BaseLoaderToolbarFragment extends BaseLoaderFragment {

	protected Toolbar toolbar;

	/**
	 * Setup the toolbar, if present.
	 */
	protected void setupToolbar() {
		if (toolbar != null) {
			((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		}
	}

	@Override
	protected void setupViews() {
		setupToolbar();
	}

	@Override
	protected void bindViews(View view) {
		super.bindViews(view);
		toolbar = (Toolbar) view.findViewById(R.id.toolbar);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		toolbar = null;
	}
}
