/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 01/05/2016.
 */

package cm.aptoide.pt.v8engine.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.AppGridDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.EmptyDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.grid.BaseGridLayoutManager;

/**
 * Created by neuro on 15-04-2016.
 *
 * @author neuro
 * @author sithengineer
 */
public class GridRecyclerFragment extends BaseRecyclerViewFragment<BaseAdapter> {

	private Handler handler;

	@Deprecated
	private static List<Displayable> makeDisp() {
		LinkedList<Displayable> displayables = new LinkedList<>();
		Displayable displayable = new EmptyDisplayable(1);

		List<Displayable> tmp = new LinkedList<>();
		tmp.add(new AppGridDisplayable(new App()));
		displayables.add(new DisplayableGroup(tmp));

		for (int i = 0; i < 2; i++) {
			displayables.add(new AppGridDisplayable(new App()));
		}

		return displayables;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {

		handler = new Handler(Looper.myLooper());

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		handler.postDelayed(this::finishLoading, 1000);
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
