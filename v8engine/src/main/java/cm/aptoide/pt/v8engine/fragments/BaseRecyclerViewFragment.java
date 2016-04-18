/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 16/04/2016.
 */

package cm.aptoide.pt.v8engine.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class BaseRecyclerViewFragment<T extends RecyclerView.Adapter> extends BaseLoaderFragment<RecyclerView> {

	protected T adapter;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		baseView.setAdapter(adapter = createAdapter());
		baseView.setLayoutManager(createLayoutManager());
	}

	@Override
	public int getRootViewId() {
		return R.layout.recycler_fragment;
	}

	protected abstract T createAdapter();

	protected abstract RecyclerView.LayoutManager createLayoutManager();
}
