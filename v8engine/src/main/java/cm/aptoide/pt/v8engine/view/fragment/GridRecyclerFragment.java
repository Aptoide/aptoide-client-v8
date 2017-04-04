/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 28/06/2016.
 */

package cm.aptoide.pt.v8engine.view.fragment;

import android.support.v7.widget.RecyclerView;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.BaseGridLayoutManager;

/**
 * Created by neuro on 15-04-2016.
 *
 * @author neuro
 */
public abstract class GridRecyclerFragment<T extends BaseAdapter>
    extends BaseRecyclerViewFragment<T> {

  private final Class<? extends BaseAdapter> adapterClass;

  public GridRecyclerFragment() {
    this.adapterClass = BaseAdapter.class;
  }

  public GridRecyclerFragment(Class<T> adapterClass) {
    this.adapterClass = adapterClass;
  }

  @Override protected int[] getViewsToShowAfterLoadingId() {
    return new int[] {};
  }

  @Override protected int getViewToShowAfterLoadingId() {
    return R.id.recycler_view;
  }

  @Override protected RecyclerView.LayoutManager createLayoutManager() {
    return new BaseGridLayoutManager(getActivity(), getAdapter());
  }

  @Override protected T createAdapter() {
    try {
      return (T) adapterClass.getConstructor().newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      CrashReport.getInstance().log(e);
    }

    // default case. code should never reach here
    return null;
  }
}
