/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cm.aptoide.pt.v8engine.R;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class BaseRecyclerViewFragment<T extends RecyclerView.Adapter> extends
		BaseLoaderToolbarFragment {

	protected T adapter;
	protected RecyclerView recyclerView;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		adapter = createAdapter();

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public int getContentViewId() {
		return R.layout.recycler_fragment;
	}

	@Override
	public void setupViews() {
		super.setupViews();
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(createLayoutManager());
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		recyclerView = null;
		adapter = null;
	}

	protected abstract T createAdapter();

	protected abstract RecyclerView.LayoutManager createLayoutManager();
}
