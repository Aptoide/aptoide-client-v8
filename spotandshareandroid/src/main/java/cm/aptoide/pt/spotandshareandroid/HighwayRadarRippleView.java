package cm.aptoide.pt.spotandshareandroid;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

/**
 * Created by filipegoncalves on 30-08-2016.
 */
public class HighwayRadarRippleView extends TextView {

  public static final int MODE_IN = 1;
  public static final int MODE_OUT = 2;
  private int mode = MODE_OUT;
  private int effectColor = Color.parseColor("#aeaeae");
  private int minumumSize = 200;
  private int currentProgress = 0;
  private int numberOfRipples = 4;
  private int time = 1000 * 1000;
  private boolean isRunning = false;
  private int constant = 30;
  private int cX;
  private int cY;
  private int raio;
  private Paint paint;
  private ObjectAnimator objectAnimator;
  private String keyword;
  private String hotspotName;
  private TypeEvaluator mProgressEvaluator = new TypeEvaluator() {

    @Override public Object evaluate(float fraction, Object startValue, Object endValue) {
      fraction = (fraction * time / constant) % 100;
      return fraction;
    }
  };

  public HighwayRadarRippleView(Context context) {
    super(context);

    initPaint();
    initAnimation();
  }

  public HighwayRadarRippleView(Context context, AttributeSet attrs) {
    super(context, attrs);

    initPaint();
    initAnimation();
  }

  public HighwayRadarRippleView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    initPaint();
    initAnimation();
  }

  private void initPaint() {
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(effectColor);
  }

  private void initAnimation() {
    objectAnimator = ObjectAnimator.ofInt(this, "currentProgress", 0, 100);
    objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
    objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
    objectAnimator.setInterpolator(new LinearInterpolator());
    objectAnimator.setEvaluator(mProgressEvaluator);
    objectAnimator.setDuration(time);
  }

  public void setEffectColor(int color) {
    this.effectColor = color;
    paint.setColor(color);
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public void startRippleAnimation() {
    if (!isRunning) {
      objectAnimator.start();
      isRunning = true;
    }
  }

  public int getCurrentProgress() {
    return currentProgress;
  }

  public void setCurrentProgress(int currentProgress) {
    this.currentProgress = currentProgress;
    this.invalidate();
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (isRippleAnimationRunning()) {
      stopRippleAnimation();
    }
  }

  public boolean isRippleAnimationRunning() {
    return isRunning;
  }

  public void stopRippleAnimation() {
    if (isRunning) {
      objectAnimator.end();
      isRunning = false;
    }
  }

  @Override public void onDraw(Canvas canvas) {
    this.invalidate();
    for (int i = 0; i < numberOfRipples; i++) {
      int progress = (currentProgress + i * 100 / (numberOfRipples)) % 100;
      if (mode == 1) {
        progress = 100 - progress;
      }

      paint.setAlpha(255 - 255 * progress / 100);
      canvas.drawCircle(cX, cY, raio * progress / 100, paint);
    }
    super.onDraw(canvas);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int resultWidth = 0;
    int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
    int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
    if (modeWidth == MeasureSpec.EXACTLY) {
      resultWidth = sizeWidth;
    } else {
      resultWidth = minumumSize;
      if (modeWidth == MeasureSpec.AT_MOST) {
        resultWidth = Math.min(resultWidth, sizeWidth);
      }
    }

    int resultHeight = 0;
    int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
    int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
    if (modeHeight == MeasureSpec.EXACTLY) {
      resultHeight = sizeHeight;
    } else {
      resultHeight = minumumSize;
      if (modeHeight == MeasureSpec.AT_MOST) {
        resultHeight = Math.min(resultHeight, sizeHeight);
      }
    }

    cX = resultWidth / 2;
    cY = resultHeight / 2;
    raio = Math.max(resultWidth, resultHeight) / 2;

    System.out.println("ripple out view radius = "
        + raio
        + "; width ="
        + resultWidth
        + "; height = "
        + resultHeight);

    setMeasuredDimension(resultWidth, resultHeight);
  }
}
