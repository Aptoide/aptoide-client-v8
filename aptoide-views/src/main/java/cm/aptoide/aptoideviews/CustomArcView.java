package cm.aptoide.aptoideviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

public class CustomArcView extends RelativeLayout {

  private Path path;
  private Paint paint;

  public CustomArcView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    TypedValue value = new TypedValue();
    getContext().getTheme()
        .resolveAttribute(R.attr.backgroundSecondary, value, true);
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(getContext().getResources()
        .getColor(value.resourceId));
    path = new Path();

    float horizontalOffset = w * .8f;
    float top = -h * .8f;
    float bottom = h;

    RectF ovalRect = new RectF(-horizontalOffset, top, w + horizontalOffset, bottom);
    path.lineTo(ovalRect.left, top);
    path.arcTo(ovalRect, 0, 300, false);
    path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
  }

  @Override protected void onDraw(Canvas canvas) {
    if (path != null) canvas.drawPath(path, paint);
    super.onDraw(canvas);
  }
}