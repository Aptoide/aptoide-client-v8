/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 28/06/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.support.v7.widget.RecyclerView;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseGridLayoutManager;

/**
 * Created by neuro on 15-04-2016.
 *
 * @author neuro
 * @author sithengineer
 */
public abstract class GridRecyclerFragment<T extends BaseAdapter>
    extends BaseRecyclerViewFragment<T> {

  private final Class<T> adapterClass;

  public GridRecyclerFragment() {
    this(null);
  }

  public GridRecyclerFragment(Class<T> adapterClass) {
    this.adapterClass = adapterClass;
  }

  @Override protected int getViewToShowAfterLoadingId() {
    return R.id.recycler_view;
  }

  @Override protected T createAdapter() {
    if (adapterClass != null) {
      try {
        return adapterClass.getConstructor().newInstance();
      } catch (Exception e) {
        e.printStackTrace();
        CrashReports.logException(e);
      }
    }
    // default case
    return (T) new BaseAdapter();
  }

  @Override protected RecyclerView.LayoutManager createLayoutManager() {
    return new BaseGridLayoutManager(getActivity(), adapter);
  }
}
