/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.v8engine.interfaces.Lifecycle;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class BaseFragment extends Fragment implements Lifecycle {

	protected Database database;
	private final String TAG = getClass().getSimpleName();

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			loadExtras(getArguments());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {

		database = new Database(getContext());
		database.open();

		return inflater.inflate(getContentViewId(), container, false);
	}

	@Override
	public void onDestroyView() {

		database.close();
		database = null;

		super.onDestroyView();
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
	@Override
	public void loadExtras(Bundle args) {
		// optional method
	}

	/**
	 * Setup previously binded views.
	 */
	public abstract void setupViews();

	@Override
	public void setupToolbar() {
		// optional method
	}
}
