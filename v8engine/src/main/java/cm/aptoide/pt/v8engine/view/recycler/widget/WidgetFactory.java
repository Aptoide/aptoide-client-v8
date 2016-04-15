/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 16/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.HashMap;

import cm.aptoide.pt.v8engine.util.MathUtils;
import cm.aptoide.pt.v8engine.util.ScreenUtils;
import rx.Observable;

/**
 * Class responsible for mapping the widgets and creating on demand through they view id.
 */
public class WidgetFactory {

	private static final WidgetsMap widgetMap = new WidgetsMap();
	private static int orientation;
	private static int columnSize;

	static {
		initialize();
		validateMapping();
		warmUp();
		computeColumnSize();
	}

	private WidgetFactory() {
	}

	private static void computeColumnSize() {
		columnSize = MathUtils.LeastCommonMultiple(getDisplayablesSizes());
		orientation = ScreenUtils.getCurrentOrientation();
	}

	private static void initialize() {
		initizalizeMapping();
	}

	private static void validateMapping() {
	}

	private static void warmUp() {
		try {
			Observable.from(widgetMap.values()).forEach(widgetEnum -> {
				widgetEnum.newDisplayable();
				widgetEnum.newWidget(null);
			});
		} catch (Exception e) {
			// Warming up reflection
		}
	}

	private static void initizalizeMapping() {
		for (WidgetEnum widgetEnum : WidgetEnum.values()) {
			widgetMap.put(widgetEnum.newDisplayable(), widgetEnum);
		}
	}

	public static Widget newBaseViewHolder(ViewGroup parent, @LayoutRes int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

		return widgetMap.get(viewType).newWidget(view);
	}

	private static int[] getDisplayablesSizes() {

		int[] arr = new int[WidgetEnum.values().length];
		int i = 0;

		for (WidgetEnum widgetEnum : WidgetEnum.values()) {
			arr[i++] = widgetEnum.getPerLineCount();
		}

		return arr;
	}

	public static int getColumnSize() {
		if (orientation != ScreenUtils.getCurrentOrientation()) {
			computeColumnSize();
		}

		return columnSize;
	}

	private static class WidgetsMap {

		private final HashMap<Integer, WidgetEnum> internalMap = new HashMap<>();

		public void put(Displayable<?> displayableImp, WidgetEnum widgetEnum) {
			internalMap.put(displayableImp.getViewType(), widgetEnum);
		}

		public Collection<WidgetEnum> values() {
			return internalMap.values();
		}

		public WidgetEnum get(@LayoutRes int viewType) {
			return internalMap.get(viewType);
		}
	}
}
