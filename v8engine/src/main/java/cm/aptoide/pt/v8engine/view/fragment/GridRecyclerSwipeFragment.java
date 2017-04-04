/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.v8engine.view.fragment;

import android.support.annotation.NonNull;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ReloadInterface;
import cm.aptoide.pt.v8engine.view.swipe.LoaderLayoutHandler;
import cm.aptoide.pt.v8engine.view.swipe.SwipeLoaderLayoutHandler;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;

/**
 * Created by neuro on 05-05-2016.
 */
public abstract class GridRecyclerSwipeFragment<T extends BaseAdapter>
    extends GridRecyclerFragmentWithDecorator<T> implements ReloadInterface {

  protected String storeTheme;

  @NonNull @Override protected LoaderLayoutHandler createLoaderLayoutHandler() {
    if (getViewsToShowAfterLoadingId().length > 0) {
      return new SwipeLoaderLayoutHandler(getViewsToShowAfterLoadingId(), this);
    }
    return new SwipeLoaderLayoutHandler(getViewToShowAfterLoadingId(), this);
  }

  @Override public void reload() {
    load(false, true, null);
  }

  @Override public int getContentViewId() {
    return R.layout.recycler_swipe_fragment;
  }

  protected static class BundleCons {

    public static final String STORE_THEME = "storeTheme";
  }
}
