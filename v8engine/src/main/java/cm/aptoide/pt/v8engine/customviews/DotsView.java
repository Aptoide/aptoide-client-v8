package cm.aptoide.pt.v8engine.customviews;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import cm.aptoide.pt.utils.AptoideUtils;

public class DotsView extends View {
  private static final int DOTS_COUNT = 7;
  private static final int OUTER_DOTS_POSITION_ANGLE = 360 / DOTS_COUNT;

  private static final int COLOR_1 = 0xFFFFC107;
  private static final int COLOR_2 = 0xFFFF9800;
  private static final int COLOR_3 = 0xFFFF5722;
  private static final int COLOR_4 = 0xFFF44336;
  public static final Property<DotsView, Float> DOTS_PROGRESS =
      new Property<DotsView, Float>(Float.class, "dotsProgress") {
        @Override public Float get(DotsView object) {
          return object.getCurrentProgress();
        }

        @Override public void set(DotsView object, Float value) {
          object.setCurrentProgress(value);
        }
      };
  private final Paint[] circlePaints = new Paint[4];
  private int centerX;
  private int centerY;
  private float maxOuterDotsRadius;
  private float maxInnerDotsRadius;
  private float maxDotSize;
  private float currentProgress = 0;
  private float currentRadius1 = 0;
  private float currentDotSize1 = 0;
  private float currentDotSize2 = 0;
  private float currentRadius2 = 0;
  private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

  public DotsView(Context context) {
    super(context);
    init();
  }

  public DotsView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public DotsView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    for (int i = 0; i < circlePaints.length; i++) {
      circlePaints[i] = new Paint();
      circlePaints[i].setStyle(Paint.Style.FILL);
    }
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    centerX = w / 2;
    centerY = h / 2;
    maxDotSize = 4;
    maxOuterDotsRadius = w / (float) 2.3 - maxDotSize * 2;
    maxInnerDotsRadius = 0.8f * maxOuterDotsRadius;
  }

  @Override protected void onDraw(Canvas canvas) {
    drawOuterDotsFrame(canvas);
    drawInnerDotsFrame(canvas);
  }

  private void drawOuterDotsFrame(Canvas canvas) {
    for (int i = 0; i < DOTS_COUNT; i++) {
      int cX = (int) (centerX + currentRadius1 * Math.cos(
          i * OUTER_DOTS_POSITION_ANGLE * Math.PI / 180));
      int cY = (int) (centerY + currentRadius1 * Math.sin(
          i * OUTER_DOTS_POSITION_ANGLE * Math.PI / 180));
      canvas.drawCircle(cX, cY, currentDotSize1, circlePaints[i % circlePaints.length]);
    }
  }

  private void drawInnerDotsFrame(Canvas canvas) {
    for (int i = 0; i < DOTS_COUNT; i++) {
      int cX = (int) (centerX + currentRadius2 * Math.cos(
          (i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180));
      int cY = (int) (centerY + currentRadius2 * Math.sin(
          (i * OUTER_DOTS_POSITION_ANGLE - 10) * Math.PI / 180));
      canvas.drawCircle(cX, cY, currentDotSize2, circlePaints[(i + 1) % circlePaints.length]);
    }
  }

  public float getCurrentProgress() {
    return currentProgress;
  }

  public void setCurrentProgress(float currentProgress) {
    this.currentProgress = currentProgress;

    updateInnerDotsPosition();
    updateOuterDotsPosition();
    updateDotsPaints();
    updateDotsAlpha();

    postInvalidate();
  }

  private void updateInnerDotsPosition() {
    if (currentProgress < 0.3f) {
      this.currentRadius2 =
          (float) AptoideUtils.MathU.mapValueFromRangeToRange(currentProgress, 0, 0.3f, 0.f, maxInnerDotsRadius);
    } else {
      this.currentRadius2 = maxInnerDotsRadius;
    }

    if (currentProgress < 0.2) {
      this.currentDotSize2 = maxDotSize;
    } else if (currentProgress < 0.5) {
      this.currentDotSize2 =
          (float) AptoideUtils.MathU.mapValueFromRangeToRange(currentProgress, 0.2f, 0.5f, maxDotSize,
              0.3 * maxDotSize);
    } else {
      this.currentDotSize2 =
          (float) AptoideUtils.MathU.mapValueFromRangeToRange(currentProgress, 0.5f, 1f, maxDotSize * 0.3f, 0);
    }
  }

  private void updateOuterDotsPosition() {
    if (currentProgress < 0.3f) {
      this.currentRadius1 = (float) AptoideUtils.MathU.mapValueFromRangeToRange(currentProgress, 0.0f, 0.3f, 0,
          maxOuterDotsRadius * 0.8f);
    } else {
      this.currentRadius1 = (float) AptoideUtils.MathU.mapValueFromRangeToRange(currentProgress, 0.3f, 1f,
          0.8f * maxOuterDotsRadius, maxOuterDotsRadius);
    }

    if (currentProgress < 0.7) {
      this.currentDotSize1 = maxDotSize;
    } else {
      this.currentDotSize1 =
          (float) AptoideUtils.MathU.mapValueFromRangeToRange(currentProgress, 0.7f, 1f, maxDotSize, 0);
    }
  }

  private void updateDotsPaints() {
    if (currentProgress < 0.5f) {
      float progress = (float) AptoideUtils.MathU.mapValueFromRangeToRange(currentProgress, 0f, 0.5f, 0, 1f);
      circlePaints[0].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_1, COLOR_2));
      circlePaints[1].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_2, COLOR_3));
      circlePaints[2].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_3, COLOR_4));
      circlePaints[3].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_4, COLOR_1));
    } else {
      float progress = (float) AptoideUtils.MathU.mapValueFromRangeToRange(currentProgress, 0.5f, 1f, 0, 1f);
      circlePaints[0].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_2, COLOR_3));
      circlePaints[1].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_3, COLOR_4));
      circlePaints[2].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_4, COLOR_1));
      circlePaints[3].setColor((Integer) argbEvaluator.evaluate(progress, COLOR_1, COLOR_2));
    }
  }

  private void updateDotsAlpha() {
    float progress = (float) AptoideUtils.MathU.clamp(currentProgress, 0.6f, 1f);
    int alpha = (int) AptoideUtils.MathU.mapValueFromRangeToRange(progress, 0.6f, 1f, 255, 0);
    circlePaints[0].setAlpha(alpha);
    circlePaints[1].setAlpha(alpha);
    circlePaints[2].setAlpha(alpha);
    circlePaints[3].setAlpha(alpha);
  }
}
