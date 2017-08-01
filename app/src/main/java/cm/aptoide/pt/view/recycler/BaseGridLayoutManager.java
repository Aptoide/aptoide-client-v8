/*
 * Copyright (c) 2016.
 * Modified on 04/05/2016.
 */

package cm.aptoide.pt.view.recycler;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayoutManager;
import android.view.WindowManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.widget.WidgetFactory;

/**
 * Created by neuro on 16-04-2016.
 */
public class BaseGridLayoutManager extends GridLayoutManager {

  private final Resources resources;
  private final WindowManager windowManager;

  public BaseGridLayoutManager(Context context, BaseAdapter baseAdapter, Resources resources,
      WindowManager windowManager) {
    super(context, WidgetFactory.getColumnSize(resources, windowManager));
    this.resources = resources;
    this.windowManager = windowManager;
    setSpanSizeLookup(new SpanSizeLookup(baseAdapter));
  }

  private class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private final BaseAdapter baseAdapter;

    public SpanSizeLookup(BaseAdapter baseAdapter) {
      this.baseAdapter = baseAdapter;
    }

    @Override public int getSpanSize(int position) {
      final Displayable displayable = baseAdapter.getDisplayable(position);

      if (displayable == null) {
        return 1;
      } else {
        if (displayable.getSpanSize(windowManager, resources) <= getSpanCount()) {
          return displayable.getSpanSize(windowManager, resources);
        } else {
          CrashReport.getInstance()
              .log(new IllegalArgumentException("Displayable " + displayable.getClass()
                  .getSimpleName() + " at position " + position + " spanSize > getSpanCount()! "));
          return getSpanCount();
        }
      }
    }
  }
}
