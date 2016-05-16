/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.base;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;

/**
 * Created by neuro on 16-04-2016.
 */
public class BaseGridLayoutManager extends GridLayoutManager {

	public BaseGridLayoutManager(Context context, BaseAdapter baseAdapter) {
		super(context, WidgetFactory.getColumnSize());
		setSpanSizeLookup(new SpanSizeLookup(baseAdapter));
	}

	private static class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

		//private final List<Displayable> displayables;
		private Displayables displayables;

		public SpanSizeLookup(BaseAdapter baseAdapter) {
			this.displayables = baseAdapter.getDisplayables();
		}

		@Override
		public int getSpanSize(int position) {
			return displayables!=null ? displayables.get(position).getSpanSize() : 0;
		}
	}
}
