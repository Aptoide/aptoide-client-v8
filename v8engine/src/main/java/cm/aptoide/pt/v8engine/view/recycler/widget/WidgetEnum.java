/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 19/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import cm.aptoide.pt.v8engine.Aptoide;
import cm.aptoide.pt.v8engine.view.recycler.widget.annotations.Displayables;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

/**
 * Singleton used to dynamically load classes that extend {@link Widget}
 *
 * @author Neurophobic Animal
 * @author SithEngineer
 */
public enum WidgetEnum {
	INSTANCE;

	private static final String TAG = WidgetEnum.class.getName();

	/**
	 * Meta class to hold a {@link Widget} class reference, a {@link Displayable} class reference and a {@link Displayable} instance generated from the previous class.
	 */
	private static final class WidgetMeta {

		private final Class<? extends Widget> widgetClass;
		private final Class<? extends Displayable> displayableClass;
		private final Displayable displayable;

		WidgetMeta(Class<? extends Widget> widgetClass, Class<? extends Displayable> displayableClass) {
			this.widgetClass = widgetClass;
			this.displayableClass = displayableClass;
			displayable = newDisplayable();
		}

		public Displayable newDisplayable() {
			try {
				return displayableClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Error instantiating Displayable!");
			}
		}
	}

	// map of a view type to a WidgetMeta class
	private HashMap<Integer, WidgetMeta> widgets;
	// cache of a view type to a WidgetMeta class
	private LruCache<Integer, WidgetMeta> widgetLruCache;

	/**
	 * loads all {@link Widget} classes that have a {@link Displayables} annotation in the current module
	 */
	private synchronized void loadWidgets() {
		widgets = new HashMap<>();
//		long nanos = System.nanoTime();
		try {
			// get the current class loader
			PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();
			// current package name for filtering purposes
			String packageName = Aptoide.class.getPackage().getName();
			// current dex file
			DexFile dex = new DexFile(Aptoide.getContext().getPackageCodePath());
			// iterate over all classes declared in the dex
			Enumeration<String> classNameEnumeration = dex.entries();
			while (classNameEnumeration.hasMoreElements()) {
				String className = classNameEnumeration.nextElement();
				// if the class doesn't belong in the current project we discard it
				// useful for speeding this method
				if (!className.startsWith(packageName)) continue;
				Class<?> widgetClass = dex.loadClass(className, classLoader);
				if (widgetClass != null && Widget.class.isAssignableFrom(widgetClass) && widgetClass.isAnnotationPresent(Displayables.class)) {
					Displayables annotation = widgetClass.getAnnotation(Displayables.class);
					Class<? extends Displayable>[] displayableClasses = annotation.value();
					WidgetMeta wMeta;
					for (Class<? extends Displayable> displayableClass : displayableClasses) {
						wMeta = new WidgetMeta(((Class<? extends Widget>) widgetClass), displayableClass);
						widgets.put(wMeta.displayable.getViewType(), wMeta);
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}

//		nanos -= System.nanoTime();
//		nanos *= -1;
//		Log.v(TAG, String.format("loadWidgets() took %d millis", nanos / 1000000));

		if (widgets.size() == 0) {
			throw new IllegalStateException("Unable to load Widgets");
		}
		int cacheSize = widgets.size() / 4;
		widgetLruCache = new LruCache<>(cacheSize == 0 ? 2 : cacheSize); // a quarter of the total, or 2
	}

	/**
	 * @param view     to pass the {@link Widget} constructor
	 * @param viewType to find which {@link Widget} to use
	 * @return freshly created {@link Widget} or a previously created one
	 */
	@NonNull
	public Widget newWidget(@NonNull View view, int viewType) {
//		long nanos = System.nanoTime();

		// lazy loading Widgets
		if (widgets == null) {
			loadWidgets();
		}

		// check if WidgetMeta instance is in cache
		WidgetMeta widgetMeta = widgetLruCache.get(viewType);
		if (widgetMeta == null) {
			widgetMeta = widgets.get(viewType);
			widgetLruCache.put(viewType, widgetMeta);
		}

		Class[] cArg = new Class[1];
		cArg[0] = View.class;

		Widget resultWidget;
		try {
			// instantiate Widget passing the view as param to the constructor
			// and return it
			resultWidget = widgetMeta.widgetClass.getDeclaredConstructor(cArg).newInstance(view);
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating widget!");
		}

//		nanos -= System.nanoTime();
//		nanos *= -1;
//		Log.v(TAG, String.format("newWidget(View, int) took %d millis", nanos / 1000000));

		return resultWidget;
	}

	public List<Displayable> getDisplayables() {
		if (widgets == null) {
			loadWidgets();
		}
		ArrayList<Displayable> displayables = new ArrayList<>(widgets.size());
		for (WidgetMeta widgetMeta : widgets.values()) {
			displayables.add(widgetMeta.displayable);
		}
		return displayables;
	}

}
