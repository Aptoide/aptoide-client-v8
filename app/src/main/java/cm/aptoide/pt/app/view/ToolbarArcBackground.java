package cm.aptoide.pt.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import cm.aptoide.pt.R;

/**
 * Background arc used in the transaction screen toolbar layout.
 */
public class ToolbarArcBackground extends View {
  /**
   * The scale factor applied on the arc curvature, related with the size of the toolbar layout
   * when scrolled
   */
  private float scale = 0.0f;
  /**
   * Measurement for the space over the screen size that the view is drawn. Used on both sides of
   * the drawable
   */
  private float extenderOverBoundary = 250.0f;
  /**
   * Size of the line used to draw the arc. This is what makes the arc to be beyond the toolbar
   * layout bottom
   */
  private float strokeWidth = 200.0f;
  /** The paint class used to draw the arc. */
  private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  /** The rectangle where the arc is drawn */
  private RectF rectF = new RectF();

  public ToolbarArcBackground(Context context) {
    super(context);
    paint.setColor(context.getResources()
        .getColor(R.color.white));
  }

  public ToolbarArcBackground(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    paint.setColor(context.getResources()
        .getColor(R.color.white));
  }

  public ToolbarArcBackground(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    paint.setColor(context.getResources()
        .getColor(R.color.white));
  }

  /**
   * Method used to set a new scale on the arc.
   *
   * @param scale Value from 0 to 1 that sets the scale of the arc.
   */
  public void setScale(float scale) {
    this.scale = (scale < 0) ? 0f : scale;
    invalidate();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(strokeWidth);

    rectF = new RectF(-extenderOverBoundary, (getHeight() + (strokeWidth / 2)) * scale,
        getWidth() + extenderOverBoundary, getHeight() + (strokeWidth / 2));
    canvas.drawArc(rectF, 0f, 180f, false, paint);
  }
}
