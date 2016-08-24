/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/08/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.LifecycleSchim;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Getter;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class BaseRecyclerViewFragment<T extends BaseAdapter> extends BaseLoaderToolbarFragment implements LifecycleSchim {

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

		if (adapter != null) {
			adapter.onViewCreated();
		}
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
		// Lifecycle interface
		if (adapter != null) {
			adapter.onDestroyView();
		}

		recyclerView = null;
		adapter = null;

		super.onDestroyView();
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

	public void clearDisplayables() {
		this.displayables.clear();
		adapter.clearDisplayables();
	}

	//
	// Lifecycle interface
	//

	/**
	 * This method will not call "onResume" in the adapter elements because in the first run despite de adapter is not null it is empty. Further calls to this
	 * method will invoke the proper "onRsume" event in the adapters elements.
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (adapter != null) {
			adapter.onResume();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (adapter != null) {
			adapter.onPause();
		}
	}

	@Override
	public void onViewCreated() {
		if (adapter != null) {
			adapter.onViewCreated();
		}
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (adapter != null) {
			adapter.onViewStateRestored(savedInstanceState);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (adapter != null) {
			adapter.onSaveInstanceState(outState);
		}
	}
}
