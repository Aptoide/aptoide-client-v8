/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 22/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.aptoide.pt.v8engine.Aptoide;
import cm.aptoide.pt.utils.MultiDexHelper;
import dalvik.system.DexFile;

/**
 * @author sithengineer
 */
public enum DisplayLoader {
	INSTANCE;

	private static final String TAG = DisplayLoader.class.getName();

	private HashMap<String, Class<? extends Displayable>> displayableHashMap;
	private LruCache<String, Class<? extends Displayable>> displayableLruCache;

	private synchronized void loadDisplayables() {
		displayableHashMap = new HashMap<>();
//		long nanos = System.
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
				Class<?> displayableClass = className.getValue().loadClass(
						className.getKey(), classLoader);

				if (displayableClass != null && Displayable.class.isAssignableFrom(displayableClass)) {
					try {
						Displayable d = (Displayable) displayableClass.newInstance();
						displayableHashMap.put(
								d.getName().name(),
								(Class<? extends Displayable>) displayableClass
						);
					} catch (Exception e) {
						Log.e(TAG, "", e);
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}

//		nanos -= System.nanoTime();
//		nanos *= -1;
//		Log.v(TAG, String.format("loadWidgets() took %d millis", nanos / 1000000));

		if (displayableHashMap.size() == 0) {
			throw new IllegalStateException("Unable to load Displayables");
		}
		int cacheSize = displayableHashMap.size() / 4;
		displayableLruCache = new LruCache<>(cacheSize == 0 ? 2 : cacheSize); // a quarter of the total, or 2
	}

	@Nullable
	public synchronized Displayable newDisplayable(@NonNull String type) {
		if (displayableHashMap == null) {
			loadDisplayables();
		}

		Class<? extends Displayable> displayableClass = displayableLruCache.get(type);

		if (displayableClass == null) {

			displayableClass = displayableHashMap.get(type);
			displayableLruCache.put(type, displayableClass);
		}

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
