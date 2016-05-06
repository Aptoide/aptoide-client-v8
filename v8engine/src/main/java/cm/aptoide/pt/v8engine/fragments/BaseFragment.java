/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.v8engine.fragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class BaseFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {
		return inflater.inflate(getRootViewId(), container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		bindViews(view);
	}

	@Override
	public void onStart() {
		super.onStart();
		loadBundle(getArguments());
	}

	/**
	 * Called after onStart. This is where arguments should be loaded.
	 *
	 * @param args {@link #getArguments()}
	 */
	protected void loadBundle(Bundle args) {
	}

	@LayoutRes
	public abstract int getRootViewId();

	protected abstract void bindViews(View view);
}
