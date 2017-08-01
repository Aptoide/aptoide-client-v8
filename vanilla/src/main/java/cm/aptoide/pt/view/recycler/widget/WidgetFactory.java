/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.view.recycler.widget;

import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.List;

/**
 * Class responsible for mapping the widgets and creating on demand through they view id.
 *
 * @author Neurophobic Animal
 * @author SithEngineer
 */
public class WidgetFactory {

  private static final String TAG = WidgetFactory.class.getName();

  private static int orientation = -1;
  private static int columnSize = -1;

  private WidgetFactory() {
  }

  public static Widget newBaseViewHolder(ViewGroup parent, @LayoutRes int viewType) {
    //long nanoTime = System.nanoTime();
    View view = LayoutInflater.from(parent.getContext())
        .inflate(viewType, parent, false);
    Widget w = V8Engine.getDisplayableWidgetMapping()
        .newWidget(view, viewType);
    //Logger.d(TAG, "newBaseViewHolder = " + ((System.nanoTime() - nanoTime) / 1000000) );
    return w;
  }

  public static int getColumnSize(Resources resources, WindowManager windowManager) {
    computeColumnSize(resources, windowManager);
    return columnSize;
  }

  private static void computeColumnSize(Resources resources, WindowManager windowManager) {

    if (orientation != AptoideUtils.ScreenU.getCurrentOrientation(resources)
        || columnSize == -1
        || orientation == -1) {
      columnSize =
          AptoideUtils.MathU.leastCommonMultiple(getDisplayablesSizes(windowManager, resources));
      orientation = AptoideUtils.ScreenU.getCurrentOrientation(resources);
    }
  }

  private static int[] getDisplayablesSizes(WindowManager windowManager, Resources resources) {

    List<Displayable> displayableList = V8Engine.getDisplayableWidgetMapping()
        .getCachedDisplayables();

    int[] arr = new int[displayableList.size()];
    int i = 0;

    for (Displayable displayable : displayableList) {
      arr[i++] = displayable.getPerLineCount(windowManager, resources);
    }

    return arr;
  }
}
