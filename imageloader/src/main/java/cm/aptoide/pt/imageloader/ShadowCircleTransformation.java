/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/07/2016.
 */

package cm.aptoide.pt.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by marcelobenites on 7/26/16.
 */
public class ShadowCircleTransformation extends BitmapTransformation {

	public ShadowCircleTransformation(Context context, View view) {
		super(context);
		// When hardware acceleration is setShadowLayer will only work for text views. We need to disable for the view
		// to make sure it will work. There is a performance penalty by doing that.
		view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	@Override
	protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
		return circleCrop(pool, toTransform);
	}

	@Nullable
	private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
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
		strokePaint.setColor(Color.WHITE);
		strokePaint.setStyle(Paint.Style.FILL);
		strokePaint.setShadowLayer(5.0f, 0.0f, 0.0f, Color.GRAY);
		strokePaint.setAntiAlias(true);
		canvas.drawCircle(r, r, r - 5.0f, strokePaint);

		Paint paint = new Paint();
		paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
		paint.setAntiAlias(true);
		canvas.drawCircle(r, r, r - 15f, paint);


		return result;
	}

	@Override
	public String getId() {
		return getClass().getName();
	}
}
