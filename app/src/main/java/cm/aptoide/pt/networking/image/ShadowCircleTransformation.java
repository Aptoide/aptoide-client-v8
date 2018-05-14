/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/07/2016.
 */

package cm.aptoide.pt.networking.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.view.View;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by marcelobenites on 7/26/16.
 */
public class ShadowCircleTransformation extends BitmapTransformation {
  @ColorInt private final int shadowColor;
  private final float strokeSize;
  private final float spaceBetween;

  public ShadowCircleTransformation(Context context, View view) {
    super(context);
    // When hardware acceleration is setShadowLayer will only work for text views. We need to disable for the view
    // to make sure it will work. There is a performance penalty by doing that.
    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    shadowColor = Color.WHITE;
    strokeSize = 0.08f;
    spaceBetween = 0f;
  }

  public ShadowCircleTransformation(Context context) {
    super(context);
    // When hardware acceleration is setShadowLayer will only work for text views. We need to disable for the view
    // to make sure it will work. There is a performance penalty by doing that.
    shadowColor = Color.TRANSPARENT;
    strokeSize = 0.08f;
    spaceBetween = 0f;
  }

  public ShadowCircleTransformation(Context context, View view, @ColorInt int shadowColor,
      float spaceBetween, float strokeSize) {
    super(context);
    this.strokeSize = strokeSize;
    // When hardware acceleration is setShadowLayer will only work for text views. We need to disable for the view
    // to make sure it will work. There is a performance penalty by doing that.
    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    this.shadowColor = shadowColor;
    this.spaceBetween = spaceBetween;
  }

  public ShadowCircleTransformation(Context context, View view, @ColorInt int shadowColor) {
    super(context);
    // When hardware acceleration is setShadowLayer will only work for text views. We need to disable for the view
    // to make sure it will work. There is a performance penalty by doing that.
    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    this.shadowColor = shadowColor;
    strokeSize = 0.08f;
    spaceBetween = .95f;
  }

  public ShadowCircleTransformation(Context context, View view, float strokeSize) {
    super(context);
    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    this.strokeSize = strokeSize;
    shadowColor = Color.WHITE;
    spaceBetween = .95f;
  }

  @Override
  protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
    return circleCrop(pool, toTransform);
  }

  @Nullable private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
    if (source == null) return null;

    int size = Math.min(source.getWidth(), source.getHeight());
    int x = (source.getWidth() - size) / 2;
    int y = (source.getHeight() - size) / 2;

    Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

    Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
    if (result == null) {
      result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
    }

    Canvas canvas = new Canvas(result);

    float r = size / 2f;
    Paint strokePaint = new Paint();
    strokePaint.setColor(shadowColor);
    strokePaint.setStyle(Paint.Style.FILL);
    float shadowRadius = size * 0.02f;
    strokePaint.setShadowLayer(shadowRadius, 0.0f, 0.0f, Color.GRAY);
    strokePaint.setAntiAlias(true);
    canvas.drawCircle(r, r, r - shadowRadius, strokePaint);

    strokePaint.setColor(Color.WHITE);
    canvas.drawCircle(r, r, (r - shadowRadius) * spaceBetween, strokePaint);
    Paint paint = new Paint();
    paint.setShader(
        new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
    paint.setAntiAlias(true);
    canvas.drawCircle(r, r, r - (size * strokeSize), paint);

    return result;
  }

  @Override public String getId() {
    return getClass().getName();
  }
}
