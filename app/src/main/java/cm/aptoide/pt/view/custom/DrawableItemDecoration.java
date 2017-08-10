package cm.aptoide.pt.view.custom;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class DrawableItemDecoration extends RecyclerView.ItemDecoration {

  private final Drawable dividerDrawable;
  private final int customHorizontalPadding;

  DrawableItemDecoration(Drawable divider, int customHorizontalPadding) {
    this.dividerDrawable = divider;
    this.customHorizontalPadding = customHorizontalPadding;
  }

  @Override public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    final int left = /* parent.getPaddingLeft() + */ customHorizontalPadding;
    final int right = parent.getWidth() /* - parent.getPaddingRight() */ - customHorizontalPadding;

    final int childCount = parent.getChildCount();
    for (int i = 0; i < (childCount - 1); i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int top = child.getBottom() + params.bottomMargin;
      final int bottom = top + dividerDrawable.getIntrinsicHeight();
      dividerDrawable.setBounds(left, top, right, bottom);
      dividerDrawable.draw(canvas);
    }
  }
}
