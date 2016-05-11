/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 11/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cm.aptoide.pt.utils.MathUtils;
import cm.aptoide.pt.utils.ScreenUtils;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.recycler.DisplayableType;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Class responsible for mapping the widgets and creating on demand through they view id.
 *
 * @author Neurophobic Animal
 * @author SithEngineer
 */
public class WidgetFactory {

	private static final String TAG = WidgetFactory.class.getName();

	private static int orientation;
	private static int columnSize;

	static {
		computeColumnSize();
	}

	private WidgetFactory() {
	}

	private static void computeColumnSize() {
		columnSize = MathUtils.leastCommonMultiple(getDisplayablesSizes());
		orientation = ScreenUtils.getCurrentOrientation(V8Engine.getContext());
	}

	public static Widget newBaseViewHolder(ViewGroup parent, @LayoutRes int viewType) {
		//long nanoTime = System.nanoTime();
		View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
		Widget w = DisplayableType.newWidget(view, viewType);
		//Log.d(TAG, "newBaseViewHolder = " + ((System.nanoTime() - nanoTime) / 1000000) );
		return w;
	}

	private static int[] getDisplayablesSizes() {

		List<Displayable> displayableList = DisplayableType.getCachedDisplayables();

		int[] arr = new int[displayableList.size()];
		int i = 0;

		for (Displayable displayable : displayableList) {
			arr[i++] = displayable.getPerLineCount();
		}

		return arr;
	}

	public static int getColumnSize() {
		if (orientation != ScreenUtils.getCurrentOrientation(V8Engine.getContext())) {
			computeColumnSize();
		}

		return columnSize;
	}
}
