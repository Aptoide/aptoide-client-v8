package cm.aptoide.pt.home;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jdandrade on 15/03/2018.
 */

public class SnapToStartHelper extends LinearSnapHelper {

  private OrientationHelper verticalHelper, horizontalHelper;

  @Override public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
      throws IllegalStateException {
    super.attachToRecyclerView(recyclerView);
  }

  @Override
  public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
      @NonNull View targetView) {
    int[] out = new int[2];

    if (layoutManager.canScrollHorizontally()) {
      out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
    } else {
      out[0] = 0;
    }

    if (layoutManager.canScrollVertically()) {
      out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager));
    } else {
      out[1] = 0;
    }
    return out;
  }

  @Override public View findSnapView(RecyclerView.LayoutManager layoutManager) {

    if (layoutManager instanceof LinearLayoutManager) {

      if (layoutManager.canScrollHorizontally()) {
        return getStartView(layoutManager, getHorizontalHelper(layoutManager));
      } else {
        return getStartView(layoutManager, getVerticalHelper(layoutManager));
      }
    }

    return super.findSnapView(layoutManager);
  }

  private int distanceToStart(View targetView, OrientationHelper helper) {
    return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
  }

  private View getStartView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {

    if (layoutManager instanceof LinearLayoutManager) {
      int firstChild = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

      boolean isLastItem =
          ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition()
              == layoutManager.getItemCount() - 1;

      if (firstChild == RecyclerView.NO_POSITION || isLastItem) {
        return null;
      }

      View child = layoutManager.findViewByPosition(firstChild);

      if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2
          && helper.getDecoratedEnd(child) > 0) {
        return child;
      } else {
        if (((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition()
            == layoutManager.getItemCount() - 1) {
          return null;
        } else {
          return layoutManager.findViewByPosition(firstChild + 1);
        }
      }
    }

    return super.findSnapView(layoutManager);
  }

  private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
    if (verticalHelper == null) {
      verticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
    }
    return verticalHelper;
  }

  private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
    if (horizontalHelper == null) {
      horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
    }
    return horizontalHelper;
  }
}
