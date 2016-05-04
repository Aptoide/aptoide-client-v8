/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.fragments;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import cm.aptoide.pt.utils.ThreadUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.layoutManager.BaseGridLayoutManager;

/**
 * Created by neuro on 15-04-2016.
 *
 * @author neuro
 * @author sithengineer
 */
public class GridRecyclerFragment extends BaseRecyclerViewFragment<BaseAdapter> {

	@Override
	protected BaseAdapter createAdapter() {
		return new BaseAdapter();
	}

	@Override
	protected RecyclerView.LayoutManager createLayoutManager() {
		return new BaseGridLayoutManager(getContext(), adapter);
	}

	@Override
	protected int getBaseViewId() {
		return R.id.recycler_view;
	}

	public void addDisplayables(List<Displayable> displayables) {
		adapter.addDisplayables(displayables);
		ThreadUtils.runOnUiThread(() -> adapter.notifyDataSetChanged());
		finishLoading();
	}
}
