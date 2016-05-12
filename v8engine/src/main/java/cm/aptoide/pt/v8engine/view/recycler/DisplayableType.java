/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler;

import android.support.annotation.Nullable;
import android.view.View;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.EmptyDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewCommentsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewDeveloperDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewOtherVersionsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewRateResultsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewRateThisDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewSuggestedAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid
		.AddMoreStoresDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppBrickDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FooterDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridDisplayDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridHeaderDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid
		.SubscribedStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.EmptyWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewCommentsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewDeveloperWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView
		.AppViewOtherVersionsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewRateResultsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView.AppViewRateThisWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView
		.AppViewSuggestedAppsWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.AddMoreStoresWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.AppBrickWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.FooterWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridAppWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridDisplayWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridHeaderWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridStoreWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.SubscribedStoreWidget;

/**
 * Created by neuro on 11-05-2016.
 */
public enum DisplayableType {

	// empty widget
	EMPTY(EmptyWidget.class, EmptyDisplayable.class),

	// common widgets / displayables
	ADD_MORE_STORES(AddMoreStoresWidget.class, AddMoreStoresDisplayable.class),
	APP_BRICK(AppBrickWidget.class, AppBrickDisplayable.class),
	FOOTER(FooterWidget.class, FooterDisplayable.class),
	SUBSCRIBED_STORE(SubscribedStoreWidget.class, SubscribedStoreDisplayable.class),

	// grid widgets / displayables
	GRID_APP(GridAppWidget.class, GridAppDisplayable.class),
	GRID_DISPLAY(GridDisplayWidget.class, GridDisplayDisplayable.class),
	GRID_HEADER(GridHeaderWidget.class, GridHeaderDisplayable.class),
	GRID_STORE(GridStoreWidget.class, GridStoreDisplayable.class),

	// appView widgets / displayables
	APP_VIEW_COMMENTS(AppViewCommentsWidget.class, AppViewCommentsDisplayable.class),
	APP_VIEW_DEVELOPER(AppViewDeveloperWidget.class, AppViewDeveloperDisplayable.class),
	APP_VIEW_OTHER_VERSIONS(AppViewOtherVersionsWidget.class, AppViewOtherVersionsDisplayable
			.class),
	APP_VIEW_RATE_RESULTS(AppViewRateResultsWidget.class, AppViewRateResultsDisplayable.class),

	APP_VIEW_RATE_THIS(AppViewRateThisWidget.class, AppViewRateThisDisplayable.class),
	APP_VIEW_SUGGESTED_APPS(AppViewSuggestedAppsWidget.class, AppViewSuggestedAppsDisplayable
			.class);

	private static List<Displayable> cachedDisplayables;
	private Displayable displayable;
	private Widget widget;
	private Class<? extends Displayable> displayableClass;
	private Class<? extends Widget> widgetClass;

	DisplayableType(Class<? extends Widget> widgetClass, Class<? extends Displayable>
			displayableClass) {
		this.displayableClass = displayableClass;
		this.widgetClass = widgetClass;

		displayable = newDisplayable();
		widget = newWidget(new View(V8Engine.getContext()));

		if (displayable.getType() == null)
			throw new IllegalStateException(String.format("Missing type in Displayable %s",
					displayableClass
					.getName()));

		if (widget.getItemViewType() == 0)
			throw new IllegalStateException(String.format("Missing view type in Widget %s",
					widgetClass
					.getName()));
	}

	public static Displayable newDisplayable(Type type, App app) {
		return ((DisplayablePojo) newDisplayable(type)).setPojo(app);
	}

	public static Displayable newDisplayable(Type type) {
		for (DisplayableType displayableType2 : values()) {
			if (displayableType2.displayable.getType() == type) {
				return displayableType2.newDisplayable();
			}
		}

		throw new IllegalStateException("There's no displayable for " + type + " type!");
	}

	public static Widget newWidget(View view, int viewType) {
		for (DisplayableType displayableType2 : values()) {
			if (displayableType2.displayable.getViewLayout() == viewType) {
				return displayableType2.newWidget(view);
			}
		}

		throw new IllegalStateException("There's no widget for " + viewType + " viewType!");
	}

	public static List<Displayable> getCachedDisplayables() {
		if (cachedDisplayables == null) {
			List<Displayable> tmp = new LinkedList<>();

			for (DisplayableType displayableType2 : values()) {
				tmp.add(displayableType2.newDisplayable());
			}
			cachedDisplayables = Collections.unmodifiableList(tmp);
		}

		return cachedDisplayables;
	}

	@Nullable
	private Widget newWidget(View view) {

		Class[] cArg = new Class[1];
		cArg[0] = View.class;

		Widget resultWidget = null;
		try {
			resultWidget = widgetClass.getDeclaredConstructor(cArg).newInstance(view);
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating widget!");
		}

		return resultWidget;
	}

	@Nullable
	public Displayable newDisplayable() {
		try {
			return displayableClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
