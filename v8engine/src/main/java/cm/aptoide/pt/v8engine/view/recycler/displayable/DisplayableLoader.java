/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;
import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.utils.CrashReports;
import cm.aptoide.pt.utils.MultiDexHelper;
import cm.aptoide.pt.v8engine.V8Engine;
import dalvik.system.DexFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sithengineer
 */
public enum DisplayableLoader {
  INSTANCE;

  private static final String TAG = DisplayableLoader.class.getName();

  private HashMap<Type, Class<? extends Displayable>> displayableHashMap;
  private LruCache<Type, Class<? extends Displayable>> displayableLruCache;

  DisplayableLoader() {
    final String TAG = DisplayableLoader.class.getName();

    //		long nanos = System.
    displayableHashMap = new HashMap<>();
    try {
      // get the current class loader
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      // current package name for filtering purposes
      String packageName = getClass().getPackage().getName();

      List<Map.Entry<String, String>> classNames =
          MultiDexHelper.getAllClasses(V8Engine.getContext());

      DexFile dexFile = null;
      for (Map.Entry<String, String> className : classNames) {
        try {
          // if the class doesn't belong in the current project we discard it
          // useful for speeding this method
          if (!className.getKey().startsWith(packageName)) continue;

          String dexFilePath = className.getValue();

          if (dexFilePath.endsWith(MultiDexHelper.EXTRACTED_SUFFIX)) {
            dexFile = DexFile.loadDex(dexFilePath, dexFilePath + ".tmp", 0);
          } else {
            dexFile = new DexFile(dexFilePath);
          }

          Class<?> displayableClass = dexFile.loadClass(className.getKey(), classLoader);

          if (displayableClass != null
              && Displayable.class.isAssignableFrom(displayableClass)
              && !displayableClass.isAnnotationPresent(Ignore.class)) {
            try {
              Displayable d = (Displayable) displayableClass.newInstance();
              displayableHashMap.put(d.getType(), (Class<? extends Displayable>) displayableClass);
            } catch (Exception e) {
              CrashReports.logException(e);
              Log.e(TAG, "", e);
            }
          }
        } catch (Exception e) {
          CrashReports.logException(e);
          Log.e(TAG, "", e);
        } finally {
          if (dexFile != null) {
            dexFile.close();
          }
        }
      }
    } catch (Exception e) {
      CrashReports.logException(e);
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

  @Nullable public Displayable newDisplayable(@NonNull Type type) {
    Class<? extends Displayable> displayableClass = displayableLruCache.get(type);

    if (displayableClass == null) {

      displayableClass = displayableHashMap.get(type);
      displayableLruCache.put(type, displayableClass);
    }

    try {
      return displayableClass.newInstance();
    } catch (InstantiationException e) {
      CrashReports.logException(e);
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      CrashReports.logException(e);
      e.printStackTrace();
    }

    return null;
  }

  @Nullable public synchronized <T> DisplayablePojo<T> newDisplayable(@NonNull Type type, T pojo) {
    Displayable displayable = newDisplayable(type);

    if (displayable != null && displayable instanceof DisplayablePojo<?>) {
      try {
        return ((DisplayablePojo<T>) displayable).setPojo(pojo);
      } catch (ClassCastException e) {
        CrashReports.logException(e);
        Logger.e(TAG, "Trying to instantiate a DisplayablePojo with a wrong type!");
      }
    } else {
      Logger.e(TAG, "Trying to instantiate a standard Displayable with a pojo!");
    }

    return null;
  }

}
