/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.base;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
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

  private static class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private BaseAdapter baseAdapter;

    public SpanSizeLookup(BaseAdapter baseAdapter) {
      this.baseAdapter = baseAdapter;
    }

    @Override public int getSpanSize(int position) {
      final Displayable displayable = baseAdapter.getDisplayable(position);
      return displayable != null ? displayable.getSpanSize() : 1;
    }
  }
}
