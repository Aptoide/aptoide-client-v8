/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseGridLayoutManager;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by neuro on 15-04-2016.
 *
 * @author neuro
 * @author sithengineer
 */
public abstract class GridRecyclerFragment extends BaseRecyclerViewFragment<BaseAdapter> {

	@Override
	protected int getViewToShowAfterLoadingId() {
		return R.id.recycler_view;
	}

	@Override
	protected BaseAdapter createAdapter() {
		return new BaseAdapter();
	}

	@Override
	protected RecyclerView.LayoutManager createLayoutManager() {
		return new BaseGridLayoutManager(getContext(), adapter);
	}

	public void addDisplayables(List<Displayable> displayables) {
		adapter.addDisplayables(displayables);
		finishLoading();
	}

	public void setDisplayables(List<Displayable> displayables) {
		adapter.clearDisplayables();
		addDisplayables(displayables);
	}
}
