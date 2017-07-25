package cm.aptoide.pt.v8engine.view.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by fabio on 22-10-2015.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

  public static final int LEFT = 1;
  public static final int RIGHT = 2;
  public static final int BOTTOM = 4;
  public static final int ALL = 7;

  private final int spacingFlag;
  private final int space;

  /**
   * @param space in pixels for the spacing between items
   */
  public DividerItemDecoration(Context context, int space) {
    this(context, space, ALL);
  }

  /**
   * @param spaceInDips in dips for the spacing between items
   * @param spacingFlag specifies in which part of the item the spacing is applied
   */
  public DividerItemDecoration(Context context, int spaceInDips, int spacingFlag) {
    this.space = getPixelsFromDips(context, spaceInDips);
    this.spacingFlag = spacingFlag;
  }

  private int getPixelsFromDips(Context context, int dipValue) {
    Resources r = context.getResources();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
        r.getDisplayMetrics());
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    if ((spacingFlag & LEFT) == LEFT) {
      outRect.left = space;
    }

    if ((spacingFlag & RIGHT) == RIGHT) {
      outRect.right = space;
    }

    if ((spacingFlag & BOTTOM) == BOTTOM) {
      outRect.bottom = space;
    }

    // Add top margin only for the first item to avoid double space between items
    RecyclerView.LayoutManager parentLayoutManager = parent.getLayoutManager();

    if (parentLayoutManager instanceof LinearLayoutManager) {
      LinearLayoutManager manager = ((LinearLayoutManager) parentLayoutManager);
      if (manager.getOrientation() == LinearLayoutManager.VERTICAL
          && manager.getPosition(view) == 0) {
        outRect.top = space;
      }
    } else if (GridLayoutManager.class.isAssignableFrom(parentLayoutManager.getClass())) {
      GridLayoutManager manager = ((GridLayoutManager) parentLayoutManager);
      if (manager.getOrientation() == LinearLayoutManager.VERTICAL) {
        int colcount = manager.getSpanCount();
        if (parent.getChildPosition(view) < colcount) {
          outRect.top = space;
        }
      }
    } else if (parent.getChildPosition(view) == 0) {
      outRect.top = space;
    }
  }
}
