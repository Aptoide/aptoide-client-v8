/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 11/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.Locale;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.EmptyDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AddMoreStoresDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppBrickDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FooterDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridDisplayDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.SubscribedStoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.EmptyWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.AddMoreStoresWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.AppBrickWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.FooterWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridAppWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.GridDisplayWidget;
import cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid.SubscribedStoreWidget;

/**
 * Created by sithengineer on 11/05/16.
 */
public enum DisplayableType {

	// empty widget
	EMPTY(EmptyWidget.class, EmptyDisplayable.class),

	// common widgets / displayables
	ADD_MORE_STORES(AddMoreStoresWidget.class, AddMoreStoresDisplayable.class),
	APP_BRICK(AppBrickWidget.class, AppBrickDisplayable.class),
	FOOTER(FooterWidget.class, FooterDisplayable.class),
	UNSUBSCRIBE_STORE(SubscribedStoreWidget.class, SubscribedStoreDisplayable.class),

	// grid widgets / displayables
	GRID_APP(GridAppWidget.class, GridAppDisplayable.class),
	GRID_DISPLAY(GridDisplayWidget.class, GridDisplayDisplayable.class),
	GRID_HEADER(GridAppWidget.class, GridAppDisplayable.class),
	GRID_STORE(GridAppWidget.class, GridAppDisplayable.class),

	// appView widgets / displayables
	/*
	APP_VIEW_COMMENTS(AppViewCommentsWidget.class, AppViewCommentsDisplayable.class),
	APP_VIEW_DEVELOPER(AppViewDeveloperWidget.class, AppViewDeveloperDisplayable.class),
	APP_VIEW_OTHER_VERSIONS(AppViewOtherVersionsWidget.class,
							AppViewOtherVersionsDisplayable.class),
	APP_VIEW_RATE_RESULTS(AppViewRateResultsWidget.class, AppViewRateResultsDisplayable.class),

	APP_VIEW_RATE_THIS(AppViewRateThisWidget.class, AppViewRateThisDisplayable.class),
	APP_VIEW_SUGGESTED_APPS(AppViewSuggestedAppsWidget.class,
							AppViewSuggestedAppsDisplayable.class),
	*/

	;

	private static final View DUMMY = new View(V8Engine.getContext());
	private static View getDummyView() {
		return DUMMY;
	}

	private static final String TAG = DisplayableType.class.getName();

	private Class<? extends Displayable> displayableClass;
	private Class<? extends Widget> widgetClass;
	private Type type;
	private int viewType;

	DisplayableType(
			Class<? extends Widget> widgetClass,
			Class<? extends Displayable> displayableClass
	) {
		this.displayableClass = displayableClass;
		this.widgetClass = widgetClass;

		Displayable displayableInstance = newDisplayable();
		Widget widgetInstance = newWidget(getDummyView());

		this.type = displayableInstance.getType();
		this.viewType = widgetInstance.getItemViewType();

		if(type==null) throw new IllegalStateException(
				String.format("Missing type in Displayable %s", displayableClass.getName()));

		if(viewType==0) throw new IllegalStateException(
				String.format("Missing view type in Widget %s", widgetClass.getName()));
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

	@Nullable
	private Widget newWidget(View view) {

		Class[] cArg = new Class[1];
		cArg[0] = View.class;

		Widget resultWidget = null;
		try {
			// instantiate Widget passing the view as param to the constructor
			// and return it
			resultWidget = mapFrom(viewType).widgetClass.getDeclaredConstructor(cArg).newInstance
					(view);
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating widget!");
		}

		return resultWidget;
	}

	private DisplayableType mapFrom(Type type) {
		for(DisplayableType d : DisplayableType.values()) {
			if(d.type==type) return d;
		}

		throw new IllegalArgumentException(
				String.format(Locale.ROOT, "Unknown type %s for Displayable", type.name())
		);
	}

	private DisplayableType mapFrom(int viewType) {
		for(DisplayableType d : DisplayableType.values()) {
			if(d.viewType==viewType) return d;
		}

		throw new IllegalArgumentException(
				String.format(Locale.ROOT, "Unknown view type %d for Displayable", viewType)
		);
	}

	@Nullable
	public Displayable newDisplayable(@NonNull Type type) {
		Class<? extends Displayable> displayableClass =
				mapFrom(type).displayableClass;

		try {
			return displayableClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Nullable
	public <T> DisplayablePojo<T> newDisplayable(
			@NonNull Type type, T pojo) {
		Displayable displayable = newDisplayable(type);

		if (displayable != null && displayable instanceof DisplayablePojo<?>) {
			try {
				return ((DisplayablePojo<T>) displayable).setPojo(pojo);
			} catch (ClassCastException e) {
				Logger.e(TAG, "Trying to instantiate a DisplayablePojo with a wrong type!");
			}
		} else {
			Logger.e(TAG, "Trying to instantiate a standard Displayable with a pojo!");
		}

		return null;
	}

	public Widget newWidget(View view, @LayoutRes int viewType) {

		Class[] cArg = new Class[1];
		cArg[0] = View.class;

		Widget resultWidget;
		try {
			// instantiate Widget passing the view as param to the constructor
			// and return it
			resultWidget = mapFrom(viewType).widgetClass.getDeclaredConstructor(cArg).newInstance
					(view);
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating widget!");
		}

		return resultWidget;
	}

}
