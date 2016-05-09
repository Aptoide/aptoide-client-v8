/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

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

	private final String TAG = getClass().getSimpleName();

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			loadBundle(getArguments());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {
		return inflater.inflate(getRootViewId(), container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		bindViews(view);
		setupViews();
	}

	/**
	 * Called after onCreate. This is where arguments should be loaded.
	 *
	 * @param args {@link #getArguments()}
	 */
	protected void loadBundle(Bundle args) {
	}

	@LayoutRes
	public abstract int getRootViewId();

	protected abstract void bindViews(View view);

	/**
	 * Setup previously binded views.
	 */
	protected abstract void setupViews();
}
