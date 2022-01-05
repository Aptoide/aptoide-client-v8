/*
 * Copyright (c) 2016.
 * Modified on 17/08/2016.
 */

package cm.aptoide.pt.view.recycler;

import android.content.Context;
import android.graphics.PointF;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created on 17/08/16.
 * <p>
 * from {@see http://stackoverflow.com/questions/31235183/recyclerview-how-to-smooth-scroll-to-top-of-item-on-a-certain-position/32819067}
 */
public class LinearLayoutManagerWithSmoothScroller extends LinearLayoutManager {

  public LinearLayoutManagerWithSmoothScroller(Context context) {
    super(context, RecyclerView.VERTICAL, false);
  }

  public LinearLayoutManagerWithSmoothScroller(Context context, int orientation,
      boolean reverseLayout) {
    super(context, orientation, reverseLayout);
  }

  @Override public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
      int position) {
    RecyclerView.SmoothScroller smoothScroller =
        new TopSnappedSmoothScroller(recyclerView.getContext());
    smoothScroller.setTargetPosition(position);
    startSmoothScroll(smoothScroller);
  }

  private class TopSnappedSmoothScroller extends LinearSmoothScroller {

    TopSnappedSmoothScroller(Context context) {
      super(context);
    }

    @Override protected int getVerticalSnapPreference() {
      return SNAP_TO_START;
    }

    @Override public PointF computeScrollVectorForPosition(int targetPosition) {
      return LinearLayoutManagerWithSmoothScroller.this.computeScrollVectorForPosition(
          targetPosition);
    }
  }
}
