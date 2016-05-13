/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Class that represents a generic Widget. All widgets should extend this class.
 */
public abstract class Widget<T extends Displayable> extends RecyclerView.ViewHolder {

	private static final String TAG = Widget.class.getName();

	public Widget(View itemView) {
		super(itemView);

		try {
			assignViews(itemView);
		} catch (Exception e) {
			Logger.e(TAG, "assignViews(View)", e);
		}
	}

	protected abstract void assignViews(View itemView);

	public abstract void bindView(T displayable);
}
