package cm.aptoide.pt.shareappsandroid;

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

  private int circleColor = Color.parseColor("#e17117");//discutir estas cores
  private int radarColor = Color.parseColor("#99e17117");
  private int tailColor = Color.parseColor("#e17117");
  private Paint circle;
  private Paint radar;
  private Matrix matrix;
  private Handler handler = new Handler();
  private Runnable runnable =
      new Runnable() {//joao disse para meter ca em cima. La em baixo dizia que n tava inciializado
        @Override public void run() {
          start = start + 2;
          matrix = new Matrix();
          matrix.postRotate(start, cX, cY);
          postInvalidate();
          handler.postDelayed(runnable, 10);
        }
      };

  public HighwayRadarScan(Context context) {
    super(context);

    init(null, context);
  }

  private void init(AttributeSet attributeSet, Context context) {

    if (attributeSet != null) {
      TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.radarScanStylable);
      circleColor = ta.getColor(R.styleable.radarScanStylable_circleColor, circleColor);
      radarColor = ta.getColor(R.styleable.radarScanStylable_radarColor, radarColor);
      tailColor = ta.getColor(R.styleable.radarScanStylable_tailColor, tailColor);
      ta.recycle();
    }

    setUpPaint();

    screenWidth = converterDipToPx(context,
        DEFAULT_WIDTH);//obter as cenas do ecra actual, portanto posso tirar o nome default..
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

  public HighwayRadarScan(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);

    init(attributeSet, context);
  }

  public HighwayRadarScan(Context context, AttributeSet attributeSet, int defStyleAttr) {
    super(context, attributeSet, defStyleAttr);

    init(attributeSet, context);
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

//    Shader shader = new SweepGradient(cX, cY, Color.parseColor("#00e17117"), Color.parseColor("#ffe17117"));
    Shader shader = new SweepGradient(cX, cY, Color.parseColor("#00ffa726"), Color.parseColor("#ffffa726"));
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
