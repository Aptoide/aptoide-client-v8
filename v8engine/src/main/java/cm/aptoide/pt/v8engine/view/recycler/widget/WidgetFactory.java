/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 19/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cm.aptoide.pt.v8engine.util.MathUtils;
import cm.aptoide.pt.v8engine.util.ScreenUtils;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Class responsible for mapping the widgets and creating on demand through they view id.
 *
 * @author Neurophobic Animal
 * @author SithEngineer
 */
public class WidgetFactory {

	private static int orientation;
	private static int columnSize;

	static {
		computeColumnSize();
	}

	private WidgetFactory() {
	}

	private static void computeColumnSize() {
		columnSize = MathUtils.LeastCommonMultiple(getDisplayablesSizes());
		orientation = ScreenUtils.getCurrentOrientation();
	}

	public static Widget newBaseViewHolder(ViewGroup parent, @LayoutRes int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
		return WidgetLoader.INSTANCE.newWidget(view, viewType);
	}

	private static int[] getDisplayablesSizes() {

		List<Displayable> displayableList = WidgetLoader.INSTANCE.getDisplayables();

		int[] arr = new int[displayableList.size()];
		int i = 0;

		for (Displayable displayable : displayableList) {
			arr[i++] = displayable.getPerLineCount();
		}

		return arr;
	}

	public static int getColumnSize() {
		if (orientation != ScreenUtils.getCurrentOrientation()) {
			computeColumnSize();
		}

		return columnSize;
	}
}
