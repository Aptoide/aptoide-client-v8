/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.interfaces.LifecycleSchim;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;

/**
 * Created by neuro on 16-04-2016.
 */
public class BaseAdapter extends RecyclerView.Adapter<Widget> implements LifecycleSchim {

	private final Displayables displayables = new Displayables();

	public BaseAdapter() { }

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
		holder.internalBindView(displayables.get(position));
	}

	@Override
	public int getItemViewType(int position) {
		return displayables.get(position).getViewLayout();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return displayables.size();
	}

	@Override
	public void onViewAttachedToWindow(Widget holder) {
		super.onViewAttachedToWindow(holder);
		holder.onViewAttached();
	}

	@Override
	public void onViewDetachedFromWindow(Widget holder) {
		super.onViewDetachedFromWindow(holder);
		holder.onViewDetached();
	}

	public Displayable popDisplayable() {
		Displayable pop = displayables.pop();
		AptoideUtils.ThreadU.runOnUiThread(() -> notifyItemRemoved(displayables.size()));
		return pop;
	}

	public Displayable getDisplayable(int position) {
		return this.displayables.get(position);
	}

	public void addDisplayable(int position, Displayable displayable) {
		this.displayables.add(position, displayable);
		AptoideUtils.ThreadU.runOnUiThread(this::notifyDataSetChanged);
	}

	public void addDisplayable(Displayable displayable) {
		this.displayables.add(displayable);
		AptoideUtils.ThreadU.runOnUiThread(this::notifyDataSetChanged);
	}

	public void addDisplayables(List<? extends Displayable> displayables) {
		this.displayables.add(displayables);
		AptoideUtils.ThreadU.runOnUiThread(this::notifyDataSetChanged);
	}

	public void addDisplayables(int position, List<? extends Displayable> displayables) {
		this.displayables.add(position, displayables);
		AptoideUtils.ThreadU.runOnUiThread(() -> notifyItemRangeInserted(position, displayables.size()));
	}

	public void removeDisplayables(int startPosition, int endPosition) {
		int numberRemovedItems = this.displayables.remove(startPosition, endPosition);
		AptoideUtils.ThreadU.runOnUiThread(() -> notifyItemRangeRemoved(startPosition, numberRemovedItems));
	}

	public void removeDisplayable(int position) {
		this.displayables.remove(position);
		AptoideUtils.ThreadU.runOnUiThread(() -> notifyItemRemoved(position));
	}

	public void clearDisplayables() {
		clearDisplayables(true);
	}

	public void clearDisplayables(boolean notifyDataSetChanged) {
		displayables.clear();
		if (notifyDataSetChanged) {
			AptoideUtils.ThreadU.runOnUiThread(this::notifyDataSetChanged);
		}
	}

	//
	// LifecycleShim interface
	//

	public void onResume() {
		displayables.onResume();
	}

	public void onPause() {
		displayables.onPause();
	}

	@Override
	public void onViewCreated() {
		displayables.onViewCreated();
	}

	@Override
	public void onDestroyView() {
		displayables.onDestroyView();
	}

	public void onSaveInstanceState(Bundle outState) {
		displayables.onSaveInstanceState(outState);
	}

	public void onViewStateRestored(Bundle savedInstanceState) {
		displayables.onViewStateRestored(savedInstanceState);
	}
}
