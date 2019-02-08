package cm.aptoide.pt.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

public class HorizontalHeaderItemDecoration extends RecyclerView.ItemDecoration {

  private static final int VERTICAL_OFFSET = 20;
  private View layout;
  private int headerSize;
  private int margin;

  public HorizontalHeaderItemDecoration(final Context context, RecyclerView parent,
      @LayoutRes int resId, int headerSize, int margin) {
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
    parent.setLayoutManager(linearLayoutManager);
    this.layout = LayoutInflater.from(context)
        .inflate(resId, parent, false);
    this.layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    this.headerSize = headerSize;
    this.margin = margin;

    layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
  }

  @Override public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    super.onDraw(c, parent, state);
    for (int i = 0; i < parent.getChildCount(); i++) {
      View view = parent.getChildAt(i);
      if (parent.getChildAdapterPosition(view) == 0) {
        c.save();
        final float left =
            view.getLeft() - (headerSize / 2f) - (layout.getMeasuredWidth() / 2f) - (margin / 2f);
        final float viewVerticalCenter =
            view.getTop() + (view.getMeasuredHeight() / 2f) - (layout.getMeasuredWidth() / 2f);
        c.translate(left, viewVerticalCenter - VERTICAL_OFFSET);
        layout.draw(c);
        c.restore();
        break;
      }
    }
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    outRect.set(margin, margin, 0, margin);
    if (parent.getChildAdapterPosition(view) == 0) {
      outRect.left = headerSize;
    }
  }
}
