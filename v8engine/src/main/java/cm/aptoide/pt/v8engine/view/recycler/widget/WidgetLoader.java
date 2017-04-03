/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.view.View;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.MultiDexHelper;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import dalvik.system.DexFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton used to dynamically load classes that extend {@link Widget}
 *
 * @author Neurophobic Animal
 * @author SithEngineer
 */
public enum WidgetLoader {
  INSTANCE;

  private static final String TAG = WidgetLoader.class.getName();

  private HashMap<Integer, WidgetMeta> widgetsHashMap;
  private LruCache<Integer, WidgetMeta> widgetLruCache;

  /**
   * loads all {@link Widget} classes that have a {@link Displayables} annotation in the current
   * module
   */
  WidgetLoader() {
    final String TAG = WidgetLoader.class.getName();
    //		long nanos = System.nanoTime();
    widgetsHashMap = new HashMap<>();
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

          Class<?> widgetClass = dexFile.loadClass(className.getKey(), classLoader);
          if (widgetClass != null
              && Widget.class.isAssignableFrom(widgetClass)
              && widgetClass.isAnnotationPresent(Displayables.class)) {
            Displayables annotation = widgetClass.getAnnotation(Displayables.class);
            Class<? extends Displayable>[] displayableClasses = annotation.value();
            WidgetMeta wMeta;
            for (Class<? extends Displayable> displayableClass : displayableClasses) {
              wMeta = new WidgetMeta(((Class<? extends Widget>) widgetClass), displayableClass);
              widgetsHashMap.put(wMeta.displayable.getViewLayout(), wMeta);
            }
          }
        } catch (Exception e) {
          CrashReport.getInstance().log(e);
        } finally {
          if (dexFile != null) {
            dexFile.close();
          }
        }
      }
    } catch (Exception e) {
      CrashReport.getInstance().log(e);
    }

    //		nanos -= System.nanoTime();
    //		nanos *= -1;
    //		Logger.v(TAG, String.format("loadWidgets() took %d millis", nanos / 1000000));

    if (widgetsHashMap.size() == 0) {
      throw new IllegalStateException("Unable to load Widgets");
    }
    int cacheSize = widgetsHashMap.size() / 4;
    widgetLruCache = new LruCache<>(cacheSize == 0 ? 2 : cacheSize); // a quarter of the
    // total, or 2

    Logger.w(TAG, "Loaded Widgets");
  }

  /**
   * @param view to pass the {@link Widget} constructor
   * @param viewType to find which {@link Widget} to use
   *
   * @return freshly created {@link Widget} or a previously created one
   */
  @NonNull public Widget newWidget(@NonNull View view, int viewType) {
    long nanos = System.nanoTime();

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

    nanos -= System.nanoTime();
    nanos *= -1;
    Logger.v(TAG, String.format("newWidget(View, int) took %d millis", nanos / 1000000));

    return resultWidget;
  }

  public List<Displayable> getDisplayables() {

    ArrayList<Displayable> displayables = new ArrayList<>(widgetsHashMap.size());
    for (WidgetMeta widgetMeta : widgetsHashMap.values()) {
      displayables.add(widgetMeta.displayable);
    }
    return displayables;
  }

  /**
   * Meta class to hold a {@link Widget} class reference, a {@link Displayable} class reference
   * and a {@link Displayable} instance generated from the previous class.
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
