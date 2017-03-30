package cm.aptoide.pt.v8engine.customviews;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import cm.aptoide.pt.utils.AptoideUtils;

public class CircleView extends View {
  public static final Property<CircleView, Float> INNER_CIRCLE_RADIUS_PROGRESS =
      new Property<CircleView, Float>(Float.class, "innerCircleRadiusProgress") {
        @Override public void set(CircleView object, Float value) {
          object.setInnerCircleRadiusProgress(value);
        }

        @Override public Float get(CircleView object) {
          return object.getInnerCircleRadiusProgress();
        }
      };
  private static final int START_COLOR = 0xFFFF5722;
  private static final int END_COLOR = 0xFFFFC107;
  public static final Property<CircleView, Float> OUTER_CIRCLE_RADIUS_PROGRESS =
      new Property<CircleView, Float>(Float.class, "outerCircleRadiusProgress") {
        @Override public Float get(CircleView object) {
          return object.getOuterCircleRadiusProgress();
        }

        @Override public void set(CircleView object, Float value) {
          object.setOuterCircleRadiusProgress(value);
        }
      };
  private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
  private Paint circlePaint = new Paint();
  private Paint maskPaint = new Paint();
  private Bitmap tempBitmap;
  private Canvas tempCanvas;
  private float outerCircleRadiusProgress = 0f;
  private float innerCircleRadiusProgress = 0f;
  private int maxCircleSize;

  public CircleView(Context context) {
    super(context);
    init();
  }

  public CircleView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    circlePaint.setStyle(Paint.Style.FILL);
    maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    maxCircleSize = w / 2;
    tempBitmap = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);
    tempCanvas = new Canvas(tempBitmap);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    tempCanvas.drawColor(0xffffff, PorterDuff.Mode.CLEAR);
    tempCanvas.drawCircle(getWidth() / 2, getHeight() / 2,
        outerCircleRadiusProgress * maxCircleSize, circlePaint);
    tempCanvas.drawCircle(getWidth() / 2, getHeight() / 2,
        innerCircleRadiusProgress * maxCircleSize, maskPaint);
    canvas.drawBitmap(tempBitmap, 0, 0, null);
  }

  public float getInnerCircleRadiusProgress() {
    return innerCircleRadiusProgress;
  }

  public void setInnerCircleRadiusProgress(float innerCircleRadiusProgress) {
    this.innerCircleRadiusProgress = innerCircleRadiusProgress;
    postInvalidate();
  }

  public float getOuterCircleRadiusProgress() {
    return outerCircleRadiusProgress;
  }

  public void setOuterCircleRadiusProgress(float outerCircleRadiusProgress) {
    this.outerCircleRadiusProgress = outerCircleRadiusProgress;
    updateCircleColor();
    postInvalidate();
  }

  private void updateCircleColor() {
    float colorProgress = (float) AptoideUtils.MathU.clamp(outerCircleRadiusProgress, 0.5, 1);
    colorProgress =
        (float) AptoideUtils.MathU.mapValueFromRangeToRange(colorProgress, 0.5f, 1f, 0f, 1f);
    this.circlePaint.setColor(
        (Integer) argbEvaluator.evaluate(colorProgress, START_COLOR, END_COLOR));
  }
}
