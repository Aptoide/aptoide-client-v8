/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/07/2016.
 */

package cm.aptoide.pt.imageloader;

import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;

import cm.aptoide.pt.preferences.Application;

/**
 * Created by neuro on 24-05-2016.
 */
public class ImageLoader {

	// TODO: 18/07/16 sithengineer add placeholders in image loading

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
}
