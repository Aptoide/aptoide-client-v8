package cm.aptoide.pt.shareappsandroid;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;

/**
 * Created by filipegoncalves on 12-10-2016.
 */

public class HighwayRadarLowElement extends TextView {
  /**
   * class that represents the low version elements. This is destined for the devices with API
   * version <11
   */

  public static final int MODE_IN = 1;
  public static final int MODE_OUT = 2;
  private int mode = MODE_OUT;
  private int effectColor = Color.rgb(0x33, 0x99, 0xcc);
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

  public HighwayRadarLowElement(Context context) {
    super(context);
    initPaint();
  }

  private void initPaint() {
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(effectColor);
  }

  public void setEffectColor(int color) {
    this.effectColor = color;
  }

  public void setMode(int mode) {
    this.mode = mode;
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
  }

  @Override public void onDraw(Canvas canvas) {
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
    //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
