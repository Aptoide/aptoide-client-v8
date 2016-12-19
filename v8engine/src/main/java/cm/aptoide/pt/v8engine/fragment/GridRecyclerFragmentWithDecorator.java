/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 07/07/2016.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import lombok.Getter;

/**
 * Created by neuro on 25-05-2016.
 */
public abstract class GridRecyclerFragmentWithDecorator<T extends BaseAdapter>
    extends GridRecyclerFragment<T> {

  @Getter private final RecyclerView.ItemDecoration defaultItemDecoration =
      new RecyclerView.ItemDecoration() {
        @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
            RecyclerView.State state) {

          int offset = 0;
          FragmentActivity activity = getActivity();
          if (activity != null) {
            offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                activity.getResources().getDisplayMetrics());
          }

          if (view.getId() == R.id.brick_app_item) {
            offset /= 2;
          }

          if (view.getId() == R.id.timeline_stats_layout) {
            offset = 0;
          }

          outRect.set(offset, offset, offset, offset);
        }
      };

  public GridRecyclerFragmentWithDecorator() {
  }

  public GridRecyclerFragmentWithDecorator(Class<T> adapterClass) {
    super(adapterClass);
  }

  @Override public void setupViews() {
    super.setupViews();
    recyclerView.addItemDecoration(defaultItemDecoration);
  }
}
