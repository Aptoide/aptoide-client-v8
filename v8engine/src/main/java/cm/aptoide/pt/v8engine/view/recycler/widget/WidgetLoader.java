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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.aptoide.pt.v8engine.Aptoide;
import cm.aptoide.pt.utils.MultiDexHelper;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import dalvik.system.DexFile;

/**
 * Singleton used to dynamically load classes that extend {@link Widget}
 *
 * @author Neurophobic Animal
 * @author SithEngineer
 */
public enum WidgetLoader {
	INSTANCE;

	private static final String TAG = WidgetLoader.class.getName();
	// map of a view type to a WidgetMeta class
	private HashMap<Integer, WidgetMeta> widgetsHashMap;
	// cache of a view type to a WidgetMeta class
	private LruCache<Integer, WidgetMeta> widgetLruCache;

	/**
	 * loads all {@link Widget} classes that have a {@link Displayables} annotation in the current module
	 */
	private synchronized void loadWidgets() {
		widgetsHashMap = new HashMap<>();
//		long nanos = System.nanoTime();
		try {
			// get the current class loader
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			// current package name for filtering purposes
			String packageName = getClass().getPackage().getName();

			List<Map.Entry<String, DexFile>> classNames =
					MultiDexHelper.getAllClasses(Aptoide.getContext());

			for(Map.Entry<String, DexFile> className : classNames ) {

				// if the class doesn't belong in the current project we discard it
				// useful for speeding this method
				if (!className.getKey().startsWith(packageName)) continue;
				Class<?> widgetClass = className.getValue().loadClass(className.getKey(), classLoader);
				if (widgetClass != null && Widget.class.isAssignableFrom(widgetClass) && widgetClass.isAnnotationPresent(Displayables.class)) {
					Displayables annotation = widgetClass.getAnnotation(Displayables.class);
					Class<? extends Displayable>[] displayableClasses = annotation.value();
					WidgetMeta wMeta;
					for (Class<? extends Displayable> displayableClass : displayableClasses) {
						wMeta = new WidgetMeta(((Class<? extends Widget>) widgetClass), displayableClass);
						widgetsHashMap.put(wMeta.displayable.getViewLayout(), wMeta);
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}

//		nanos -= System.nanoTime();
//		nanos *= -1;
//		Log.v(TAG, String.format("loadWidgets() took %d millis", nanos / 1000000));

		if (widgetsHashMap.size() == 0) {
			throw new IllegalStateException("Unable to load Widgets");
		}
		int cacheSize = widgetsHashMap.size() / 4;
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
		if (widgetsHashMap == null) {
			loadWidgets();
		}

		// check if WidgetMeta instance is in cache
		WidgetMeta widgetMeta = widgetLruCache.get(viewType);
		if (widgetMeta == null) {
			widgetMeta = widgetsHashMap.get(viewType);
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
		if (widgetsHashMap == null) {
			loadWidgets();
		}
		ArrayList<Displayable> displayables = new ArrayList<>(widgetsHashMap.size());
		for (WidgetMeta widgetMeta : widgetsHashMap.values()) {
			displayables.add(widgetMeta.displayable);
		}
		return displayables;
	}

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

}
