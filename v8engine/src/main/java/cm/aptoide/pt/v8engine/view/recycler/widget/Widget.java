/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 16/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Class that represents a generic Widget. All widgets should extend this class.
 */
public abstract class Widget<T extends Displayable> extends RecyclerView.ViewHolder {

	public Widget(View itemView) {
		super(itemView);
	}

	public abstract void bindView(T displayable);
}
