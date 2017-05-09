package cm.aptoide.pt.spotandshareandroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by filipegoncalves on 24-08-2016.
 */
public class HighwayRadarScan extends View {

  private static final int DEFAULT_WIDTH = 300;
  private static final int DEFAULT_HEIGHT = 300;
  private int screenWidth;
  private int screenHeight;
  private int cX;
  private int cY;
  private int raio;
  private int start;

  private int circleColor;
  private int radarColor;
  private int tailColor;
  private boolean enableAnimation;
  private Paint circle;
  private Paint radar;
  private Matrix matrix;
  private Handler handler = new Handler();
  private Runnable runnable = new Runnable() {
    @Override public void run() {
      if (enableAnimation) {
        start = start + 2;
        matrix = new Matrix();
        matrix.postRotate(start, cX, cY);
        postInvalidate();
      }
      handler.postDelayed(runnable, 10);
    }
  };

  public HighwayRadarScan(Context context) {
    super(context);

    init(null, context);
  }

  public HighwayRadarScan(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);

    init(attributeSet, context);
  }

  public HighwayRadarScan(Context context, AttributeSet attributeSet, int defStyleAttr) {
    super(context, attributeSet, defStyleAttr);

    init(attributeSet, context);
  }

  private void init(AttributeSet attributeSet, Context context) {

    circleColor = Color.parseColor("#cecece");
    radarColor = Color.parseColor("#fafafa");
    tailColor = Color.parseColor("#efefef");
    enableAnimation = true;
    if (attributeSet != null) {
      TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.HighwayRadarScan);
      circleColor =
          ta.getColor(R.styleable.HighwayRadarScan_circleColor, Color.parseColor("#cecece"));
      radarColor =
          ta.getColor(R.styleable.HighwayRadarScan_radarColor, Color.parseColor("#fafafa"));
      tailColor = ta.getColor(R.styleable.HighwayRadarScan_tailColor, Color.parseColor("#efefef"));
      enableAnimation = ta.getBoolean(R.styleable.HighwayRadarScan_enableAnimation, true);
      ta.recycle();
    }

    setUpPaint();

    screenWidth = converterDipToPx(context, DEFAULT_WIDTH);
    screenHeight = converterDipToPx(context, DEFAULT_HEIGHT);

    final Handler handler = new Handler();

    matrix = new Matrix();
    handler.post(runnable);
  }

  private void setUpPaint() {
    circle = new Paint();
    radar = new Paint();

    circle.setColor(circleColor);
    radar.setColor(radarColor);
    circle.setStyle(Paint.Style.STROKE);
    circle.setStrokeWidth(2);
    circle.setAntiAlias(true);//imp
    radar.setAntiAlias(true);
  }

  private int converterDipToPx(Context context, float dipValue) {

    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
  }

  @Override
  protected void onSizeChanged(int actualWidth, int actualHeight, int oldWidth, int oldHeight) {
    super.onSizeChanged(actualWidth, actualHeight, oldWidth, oldHeight);

    cX = actualWidth / 2;
    cY = actualHeight / 2;
    raio = Math.min(actualWidth, actualHeight);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawCircle(cX, cY, raio / 7, circle);
    canvas.drawCircle(cX, cY, raio / 4, circle);
    canvas.drawCircle(cX, cY, raio / 3, circle);
    canvas.drawCircle(cX, cY, 3 * raio / 7, circle);

    Shader shader =
        new SweepGradient(cX, cY, Color.parseColor("#00fafafa"), Color.parseColor("#ffefefef"));
    radar.setShader(shader);
    canvas.concat(matrix);
    canvas.drawCircle(cX, cY, 3 * raio / 7, radar);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int resultWidth = 0;
    int resultHeight = 0;
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    if (widthMode == MeasureSpec.EXACTLY) {
      resultWidth = widthSize;
    } else if (widthMode == MeasureSpec.AT_MOST) {
      resultWidth = Math.min(resultWidth, widthSize);
    } else {
      resultWidth = screenWidth;
    }

    if (heightMode == MeasureSpec.EXACTLY) {
      resultHeight = heightSize;
    } else if (heightMode == MeasureSpec.AT_MOST) {
      resultHeight = Math.min(resultHeight, heightSize);
    } else {
      resultHeight = screenHeight;
    }

    setMeasuredDimension(resultWidth, resultHeight);
  }
}
