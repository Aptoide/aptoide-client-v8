/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 25/05/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.ReloadInterface;
import cm.aptoide.pt.v8engine.layouthandler.LoaderLayoutHandler;
import cm.aptoide.pt.v8engine.layouthandler.SwipeLoaderLayoutHandler;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.util.ThemeUtils;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;

/**
 * Created by neuro on 05-05-2016.
 */
public abstract class GridRecyclerSwipeFragment<T extends BaseAdapter>
    extends GridRecyclerFragmentWithDecorator<T> implements ReloadInterface {

  protected String storeTheme;

  @Partners @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    if (storeTheme == null) {
      storeTheme = "default";
    }
    ThemeUtils.setStoreTheme(getActivity(), storeTheme);
    ThemeUtils.setStatusBarThemeColor(getActivity(), StoreThemeEnum.get(storeTheme));
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @NonNull @Override protected LoaderLayoutHandler createLoaderLayoutHandler() {
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
