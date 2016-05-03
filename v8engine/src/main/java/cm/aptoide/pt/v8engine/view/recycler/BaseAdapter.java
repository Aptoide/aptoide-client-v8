/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 01/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;
import lombok.Getter;

/**
 * Created by neuro on 16-04-2016.
 */
public class BaseAdapter extends RecyclerView.Adapter<Widget> {

	private static final String TAG = BaseAdapter.class.getName();

	@Getter private final Displayables displayables = new Displayables();

	public BaseAdapter() {
	}

	public BaseAdapter(List<Displayable> displayables) {
		this.displayables.add(displayables);
	}

	@Override
	public Widget onCreateViewHolder(ViewGroup parent, int viewType) {
		//long nanoTime = System.nanoTime();
		Widget w = WidgetFactory.newBaseViewHolder(parent, viewType);
		//Log.d(TAG, "onCreateViewHolder = " + ((System.nanoTime() - nanoTime) / 1000000));
		return w;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onBindViewHolder(Widget holder, int position) {
		//long nanoTime = System.nanoTime();
		holder.bindView(displayables.get(position));
		//Log.d(TAG, "onBindViewHolder = " + ((System.nanoTime() - nanoTime) / 1000000));
	}

	@Override
	public int getItemViewType(int position) {
		//long nanoTime = System.nanoTime();
		int itemViewType =  displayables.get(position).getViewLayout();
		//Log.d(TAG, "getItemViewType = " + ((System.nanoTime() - nanoTime) / 1000000));
		return itemViewType;
	}

	@Override
	public int getItemCount() {
		return displayables.size();
	}

	public void addDisplayables(List<Displayable> displayables) {
		//long nanoTime = System.nanoTime();
		this.displayables.add(displayables);
		//Log.d(TAG, "addDisplayables = " + ((System.nanoTime() - nanoTime) / 1000000));
	}


}
