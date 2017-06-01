package cm.aptoide.pt.v8engine.spotandshare.view.radar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import cm.aptoide.pt.spotandshareandroid.R;

/**
 * Created by filipegoncalves on 24-08-2016.
 */
public class RadarProgress extends View {

  private RectF rect = new RectF(0, 0, 200, 200);
  private float initialAngle = 0.0f;
  private int raio;
  private Paint paint;

  public RadarProgress(Context context) {
    super(context);
  }

  public RadarProgress(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RadarProgress(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setStrokeWidth(8);
    paint.setStyle(Paint.Style.STROKE);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    stopRotation();
    return super.onTouchEvent(event);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    //    int[] f = { Color.parseColor("#00A8D7A7"), Color.parseColor("#ffA8D7A7") };
    int[] f = { R.color.dark_blue, R.color.dark_blue };
    float[] p = { 0.0f, 1.0f };
    SweepGradient sweepGradient = new SweepGradient(rect.centerX(), rect.centerX(), f, p);
    paint.setShader(sweepGradient);
    Matrix matrix = new Matrix();
    matrix.postRotate(initialAngle, rect.centerX(), rect.centerY());
    canvas.concat(matrix);
    canvas.drawArc(rect, 0, 360, true, paint);
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    stopRotation();
    startRotation(1 * 1000);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    stopRotation();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);
    raio = Math.min(width, height);
    setMeasuredDimension(width, height);
    rect.set(17, 17, width - 17, height - 17);
  }

  private void startRotation(long duration) {
    CustomAnimation animation = new CustomAnimation();
    animation.setDuration(duration);
    animation.setRepeatCount(Animation.INFINITE);
    animation.setInterpolator(new LinearInterpolator());
    animation.setMyCustomListener(new CustomAnimation.myCustomListener() {
      @Override public void applyTans(float time) {
        initialAngle = 360 * time;
        invalidate();
      }
    });
    startAnimation(animation);
  }

  private void stopRotation() {
    clearAnimation();
  }
}
