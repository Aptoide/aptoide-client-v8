/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 16/04/2016.
 */

package cm.aptoide.pt.v8engine.fragments;

import android.support.v7.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.implementationsToRemove.DisplayableImp;
import cm.aptoide.pt.v8engine.util.SystemUtils;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.BaseGridLayoutManager;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayable;

/**
 * Created by neuro on 15-04-2016.
 */
public class GridRecyclerFragment extends BaseRecyclerViewFragment<BaseAdapter> {

	// TODO: Remove
	@Deprecated
	private static List<Displayable> makeDisp() {
		LinkedList<Displayable> displayables = new LinkedList<>();
		Displayable displayable = new DisplayableImp(null);

		for (int i = 0; i < 20; i++) {
			displayables.add(displayable);
		}

		return displayables;
	}

	@Override
	public void onResume() {
		super.onResume();
		new Thread(() -> {
			SystemUtils.sleep(1000);
			finishLoading();
			System.out.println("");
		}).start();
	}

	@Override
	protected BaseAdapter createAdapter() {
		return new BaseAdapter(makeDisp());
	}

	@Override
	protected RecyclerView.LayoutManager createLayoutManager() {
		return new BaseGridLayoutManager(getContext(), adapter);
	}

	@Override
	protected int getBaseViewId() {
		return R.id.recycler_view;
	}
}
