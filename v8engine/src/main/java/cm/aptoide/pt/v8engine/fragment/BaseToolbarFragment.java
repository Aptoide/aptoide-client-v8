/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 06-05-2016.
 */
public abstract class BaseToolbarFragment extends SupportV4BaseFragment {

	protected Toolbar toolbar;

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		toolbar = null;
	}

	@Override
	public void setupViews() {
		setupToolbar();
	}

	/**
	 * Setup the toolbar, if present.
	 */
	public void setupToolbar() {
		if (toolbar != null) {
			((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		}
	}

	@Override
	public void bindViews(View view) {
		toolbar = (Toolbar) view.findViewById(R.id.toolbar);
	}
}
