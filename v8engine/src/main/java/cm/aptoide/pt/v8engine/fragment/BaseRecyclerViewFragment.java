/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 05/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Getter;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class BaseRecyclerViewFragment<T extends BaseAdapter> extends
		BaseLoaderToolbarFragment {

	@Getter protected T adapter;
	@Getter
	protected RecyclerView.LayoutManager layoutManager;
	@Getter
	protected RecyclerView recyclerView;
	private List<Displayable> displayables = new LinkedList<>();

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		adapter = createAdapter();

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void load(boolean refresh, Bundle savedInstanceState) {
		if (refresh) {
			clearDisplayables();
		} else {
			setDisplayables(new LinkedList<>(displayables));
		}
	}

	@Override
	public int getContentViewId() {
		return R.layout.recycler_fragment;
	}

	@Override
	public void setupViews() {
		super.setupViews();
		recyclerView.setAdapter(adapter);
		layoutManager = createLayoutManager();
		recyclerView.setLayoutManager(layoutManager);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		recyclerView = null;
		adapter = null;
	}

	@Override
	public void bindViews(View view) {
		super.bindViews(view);
		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
	}

	protected abstract T createAdapter();

	protected abstract RecyclerView.LayoutManager createLayoutManager();

	public void addDisplayable(int position, Displayable displayable) {
		adapter.addDisplayable(position, displayable);
	}

	public void addDisplayable(Displayable displayable) {
		adapter.addDisplayable(displayable);
		finishLoading();
	}

	public void addDisplayables(List<? extends Displayable> displayables) {
		this.displayables.addAll(displayables);
		adapter.addDisplayables(displayables);
		finishLoading();
	}

	public void setDisplayables(List<? extends Displayable> displayables) {
		clearDisplayables();
		addDisplayables(displayables);
	}

	@Deprecated
	public void addDisplayables(int position, List<? extends Displayable> displayables) {
		adapter.addDisplayables(position, displayables);
		finishLoading();
	}

	private void clearDisplayables() {
		this.displayables.clear();
		adapter.clearDisplayables();
	}
}
