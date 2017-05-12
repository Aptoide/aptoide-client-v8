/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;

/**
 * Created by neuro on 16-04-2016.
 */
public class BaseGridLayoutManager extends GridLayoutManager {

  public BaseGridLayoutManager(Context context, BaseAdapter baseAdapter) {
    super(context, WidgetFactory.getColumnSize());
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
        if (displayable.getSpanSize() <= getSpanCount()) {
          return displayable.getSpanSize();
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
