/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 22/08/2016.
 */

package cm.aptoide.pt.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.widget.ImageView;
import cm.aptoide.pt.preferences.Application;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.NotificationTarget;
import java.util.concurrent.ExecutionException;

/**
 * Created by neuro on 24-05-2016.
 */
public class ImageLoader {

	static {
		GlideBuilder builder = new GlideBuilder(Application.getContext());
		builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
	}

	public static void load(String url, @DrawableRes int loadingPlaceHolder, ImageView imageView) {
		Glide.with(Application.getContext()).load(url).placeholder(loadingPlaceHolder).into(imageView);
	}

	public static void load(String url, @DrawableRes ImageView imageView) {
		Glide.with(Application.getContext()).load(url).into(imageView);
	}

	public static void load(@DrawableRes int drawableId, ImageView imageView) {
		Glide.with(Application.getContext()).load(drawableId).into(imageView);
	}

	public static void loadWithCircleTransformAndPlaceHolder(String url, @DrawableRes ImageView imageView, @DrawableRes int placeHolderDrawableId) {
		Glide.with(Application.getContext())
				.load(url)
				.transform(new CircleTransform(Application.getContext()))
				.placeholder(placeHolderDrawableId)
				.into(imageView);
	}

	public static void loadWithCircleTransform(@DrawableRes int drawableId, ImageView imageView) {
		Glide.with(Application.getContext())
				.fromResource()
				.load(drawableId)
				.transform(new CircleTransform(Application.getContext()))
				.into(imageView);
	}

	public static void loadWithCircleTransform(String url, @DrawableRes ImageView imageView) {
		Glide.with(Application.getContext())
				.load(url)
				.transform(new CircleTransform(Application.getContext()))
				.into(imageView);
	}

	public static void loadWithShadowCircleTransform(String url, @DrawableRes ImageView imageView) {
		Glide.with(Application.getContext())
				.load(url)
				.transform(new ShadowCircleTransformation(Application.getContext(), imageView))
				.into(imageView);
	}

	public static void loadWithShadowCircleTransform(@DrawableRes int drawableId, @DrawableRes ImageView imageView) {
		Glide.with(Application.getContext())
				.fromResource()
				.load(drawableId)
				.transform(new ShadowCircleTransformation(Application.getContext(), imageView))
				.into(imageView);
	}

	public static void loadImageToNotification(NotificationTarget notificationTarget, String url) {
		Glide.with(Application.getContext().getApplicationContext()).load(url).asBitmap().into(notificationTarget);
	}

	@WorkerThread public static @Nullable Bitmap loadBitmap(Context context, String apkIconPath) {
		try {
			return Glide.
					with(context).
					load(apkIconPath).
					asBitmap().
					into(-1, -1). // full size
					get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
}
