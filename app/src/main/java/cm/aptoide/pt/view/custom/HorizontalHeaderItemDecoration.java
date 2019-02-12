package cm.aptoide.pt.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

public class HorizontalHeaderItemDecoration extends RecyclerView.ItemDecoration {

  private static final int VERTICAL_OFFSET = 20;
  private static final float PARALLAX_RATIO = 0.3f;

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

    boolean foundFirstChild = false;
    float viewVerticalCenter = 0f;
    float left = 0f;
    float limit = -(layout.getMeasuredWidth() / 2f);
    float initialPosition =
        ((headerSize / 2f) - (layout.getMeasuredWidth() / 2f) - (margin / 2f)) * PARALLAX_RATIO;

    for (int i = 0; i < parent.getChildCount(); i++) {
      View view = parent.getChildAt(i);
      if (viewVerticalCenter == 0f) {
        viewVerticalCenter =
            view.getTop() + (view.getMeasuredHeight() / 2f) - (layout.getMeasuredWidth() / 2f);
      }
      if (parent.getChildAdapterPosition(view) == 0) {
        left =
            view.getLeft() - (headerSize / 2f) - (layout.getMeasuredWidth() / 2f) - (margin / 2f);
        left *= PARALLAX_RATIO;
        if (left < limit) {
          left = limit;
        }
        foundFirstChild = true;
        break;
      }
    }
    if (!foundFirstChild) {
      left = limit;
    }
    int movementPercentage = (int) (getPercentage(limit, initialPosition, left) * 255f);
    c.save();
    c.saveLayerAlpha(new RectF(0, 0, headerSize - 2, c.getHeight()), movementPercentage,
        Canvas.ALL_SAVE_FLAG);
    c.translate(left, viewVerticalCenter - VERTICAL_OFFSET);
    layout.draw(c);
    c.restore();
  }

  private float getPercentage(float minimum, float maximum, float actualValue) {
    return (actualValue - minimum) / (maximum - minimum);
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    outRect.set(margin, margin, 0, margin);
    if (parent.getChildAdapterPosition(view) == 0) {
      outRect.left = headerSize;
    }
  }
}
