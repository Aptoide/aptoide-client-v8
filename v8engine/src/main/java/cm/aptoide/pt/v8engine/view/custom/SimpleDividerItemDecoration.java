package cm.aptoide.pt.v8engine.view.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by trinkes on 13/07/2017.
 */

public class SimpleDividerItemDecoration extends ItemDecoration {

  private final int space;

  public SimpleDividerItemDecoration(Context context, int spaceInDips) {
    this.space = getPixelsFromDips(context, spaceInDips);
  }

  private int getPixelsFromDips(Context context, int dipValue) {
    Resources r = context.getResources();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,
        r.getDisplayMetrics());
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    outRect.right = space;
    outRect.left = space;
    outRect.bottom = space;
    outRect.top = space;
  }
}
