/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 07/07/2016.
 */

package cm.aptoide.pt.v8engine.view.fragment;

import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;

/**
 * Created by neuro on 25-05-2016.
 */
public abstract class GridRecyclerFragmentWithDecorator<T extends BaseAdapter>
    extends AptoideBaseFragment<T> {

  public GridRecyclerFragmentWithDecorator() {
  }

  @CallSuper @Override public void setupViews() {
    super.setupViews();
    RecyclerView.ItemDecoration itemDecoration = getItemDecoration();
    if (itemDecoration != null) {
      getRecyclerView().addItemDecoration(itemDecoration);
    }
  }

  //public GridRecyclerFragmentWithDecorator(Class<T> adapterClass) {
  //  super(adapterClass);
  //}

  protected RecyclerView.ItemDecoration getItemDecoration() {
    return new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {

        int offset = 0;
        int top = 0;
        int bottom = 0;
        int left = 0;
        int right = 0;
        FragmentActivity activity = getActivity();
        if (activity != null) {
          offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
              activity.getResources()
                  .getDisplayMetrics());
          top = offset;
          bottom = offset;
          left = offset;
          right = offset;
        }

        if (view.getId() == R.id.brick_app_item) {
          top = offset / 2;
          bottom = offset / 2;
          left = offset / 2;
          right = offset / 2;
        }

        if (view.getId() == R.id.timeline_stats_layout) {
          top = 0;
          left = 0;
          right = 0;
          bottom = offset;
        }

        if (view.getId() == R.id.timeline_login_layout) {
          top = 0;
          left = 0;
          right = 0;
          bottom = offset;
        }

        if (view.getId() == R.id.message_white_bg) {
          top = 0;
          left = offset;
          right = offset;
          bottom = offset;
        }

        outRect.set(left, top, right, bottom);
      }
    };
  }
}
