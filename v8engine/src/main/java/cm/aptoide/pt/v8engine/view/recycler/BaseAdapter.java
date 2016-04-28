/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/04/2016.
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

	@Getter private final Displayables displayables = new Displayables();

	public BaseAdapter() {
	}

	public BaseAdapter(List<Displayable> displayables) {
		this.displayables.add(displayables);
	}

	@Override
	public Widget onCreateViewHolder(ViewGroup parent, int viewType) {
		return WidgetFactory.newBaseViewHolder(parent, viewType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onBindViewHolder(Widget holder, int position) {
		holder.bindView(displayables.get(position));
	}

	@Override
	public int getItemViewType(int position) {
		return displayables.get(position).getViewType();
	}

	@Override
	public int getItemCount() {
		return displayables.size();
	}
}
