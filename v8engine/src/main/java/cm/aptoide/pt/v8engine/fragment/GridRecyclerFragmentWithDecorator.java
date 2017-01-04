/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 07/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;

import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;

/**
 * Created by neuro on 25-05-2016.
 */
public abstract class GridRecyclerFragmentWithDecorator<T extends BaseAdapter>
    extends GridRecyclerFragment<T> {

  public GridRecyclerFragmentWithDecorator() {
  }

  protected RecyclerView.ItemDecoration getItemDecoration() {
    return null;
  }

  //public GridRecyclerFragmentWithDecorator(Class<T> adapterClass) {
  //  super(adapterClass);
  //}

  @CallSuper @Override public void setupViews() {
    super.setupViews();
    RecyclerView.ItemDecoration itemDecoration = getItemDecoration();
    if(itemDecoration!=null) {
      recyclerView.addItemDecoration(itemDecoration);
    }
  }
}
