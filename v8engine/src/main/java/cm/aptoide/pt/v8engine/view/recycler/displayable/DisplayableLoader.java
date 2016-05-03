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

import cm.aptoide.pt.utils.MultiDexHelper;
import cm.aptoide.pt.v8engine.Aptoide;
import dalvik.system.DexFile;

/**
 * @author sithengineer
 */
public enum DisplayableLoader {
	INSTANCE;

	private HashMap<String, Class<? extends Displayable>> displayableHashMap;
	private LruCache<String, Class<? extends Displayable>> displayableLruCache;

	DisplayableLoader() {
		final String TAG = DisplayableLoader.class.getName();

//		long nanos = System.
		displayableHashMap = new HashMap<>();
		try {
			// get the current class loader
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			// current package name for filtering purposes
			String packageName = getClass().getPackage().getName();

			List<Map.Entry<String, String>> classNames = MultiDexHelper.getAllClasses(Aptoide
					.getContext());

			DexFile dexFile = null;
			for (Map.Entry<String, String> className : classNames) {
				try{
					// if the class doesn't belong in the current project we discard it
					// useful for speeding this method
					if (!className.getKey().startsWith(packageName)) continue;

					String dexFilePath = className.getValue();

					if(dexFilePath.endsWith(MultiDexHelper.EXTRACTED_SUFFIX)) {
						dexFile = DexFile.loadDex(dexFilePath, dexFilePath + ".tmp", 0);
					}else{
						dexFile = new DexFile(dexFilePath);
					}

					Class<?> displayableClass = dexFile.loadClass(className.getKey(), classLoader);

					if (displayableClass != null && Displayable.class.isAssignableFrom
							(displayableClass)) {
						try {
							Displayable d = (Displayable) displayableClass.newInstance();
							displayableHashMap.put(d.getName()
									.name(), (Class<? extends Displayable>) displayableClass);
						} catch (Exception e) {
							Log.e(TAG, "", e);
						}
					}
				} catch (Exception e) {
					Log.e(TAG, "", e);
				} finally {
					if(dexFile!=null) {
						dexFile.close();
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
		displayableLruCache = new LruCache<>(cacheSize == 0 ? 2 : cacheSize); // a quarter of the
		// total, or 2

		Log.w(TAG, "Loaded Displayables");
	}

	@Nullable
	public Displayable newDisplayable(@NonNull String type) {
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
