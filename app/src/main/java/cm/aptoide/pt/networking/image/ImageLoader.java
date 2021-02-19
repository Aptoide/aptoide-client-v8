package cm.aptoide.pt.networking.image;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.palette.graphics.Palette;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

/**
 * Created by neuro on 24-05-2016.
 */
public class ImageLoader {

  private static final String TAG = ImageLoader.class.getName();

  private final WeakReference<Context> weakContext;
  private final Resources resources;
  private final WindowManager windowManager;

  private ImageLoader(Context context) {
    this.weakContext = new WeakReference<>(context);
    this.resources = context.getResources();
    this.windowManager = ((WindowManager) context.getSystemService(Service.WINDOW_SERVICE));
  }

  public static ImageLoader with(Context context) {
    return new ImageLoader(context);
  }

  /**
   * Cancel the image loading request
   *
   * @param target Previously returned {@link Target} from {@link ImageLoader}.load...
   */
  public static void cancel(Context context, View target) {
    Glide.with(context)
        .clear(target);
  }

  /**
   * Cancel the image loading request
   *
   * @param target Previously returned {@link Target} from {@link ImageLoader}.load...
   */
  public static <R> void cancel(Context context, FutureTarget<R> target) {
    Glide.with(context)
        .clear(target);
  }

  /**
   * Cancel the image loading request
   *
   * @param target Previously returned {@link Target} from {@link ImageLoader}.load...
   */
  public static <R> void cancel(Context context, Target<R> target) {
    Glide.with(context)
        .clear(target);
  }

  /**
   * Blocking call to load a bitmap.
   *
   * @param uri Path for the bitmap to be loaded.
   *
   * @return Loaded bitmap or null.
   */
  @WorkerThread public @Nullable Bitmap loadBitmap(String uri) {
    Context context = weakContext.get();
    if (context != null) {
      try {
        return Glide.
            with(context)
            .asBitmap()
            .load(uri)
            .submit()
            .get();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    } else {
      Log.e(TAG, "::loadBitmap() Context is null");
    }
    return null;
  }

  /**
   * Mutates URL to append "_50x50" to load an avatar image from an image URL.
   *
   * @param url original image URL
   * @param imageView destination container for the image
   * @param placeHolderDrawableId placeholder while the image is loading or when is not loaded
   */
  public Target<Drawable> loadWithCircleTransformAndPlaceHolderAvatarSize(String url,
      ImageView imageView, @DrawableRes int placeHolderDrawableId) {
    return loadWithCircleTransformAndPlaceHolder(
        AptoideUtils.IconSizeU.generateStringAvatar(url, resources, windowManager), imageView,
        placeHolderDrawableId);
  }

  public Target<Drawable> loadWithCircleTransformAndPlaceHolder(String url, ImageView imageView,
      @DrawableRes int placeHolderDrawableId) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().transform(new CircleCrop())
              .placeholder(placeHolderDrawableId))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithCircleTransformAndPlaceHolder() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithShadowCircleTransform(String url, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().transform(new ShadowCircleTransformation(context, imageView)))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithShadowCircleTransformWithPlaceholder(String url,
      ImageView imageView, @DrawableRes int drawablePlaceholder) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().transform(new ShadowCircleTransformation(context))
              .placeholder(drawablePlaceholder))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithShadowCircleTransform(String url, ImageView imageView,
      @ColorInt int color, float spaceBetween, float strokeSize) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().transform(
              new ShadowCircleTransformation(context, imageView, color, spaceBetween, strokeSize)))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithShadowCircleTransform(@DrawableRes int drawableId,
      ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(drawableId)
          .apply(getRequestOptions().transform(new ShadowCircleTransformation(context, imageView)))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithShadowCircleTransform(String url, ImageView imageView,
      @ColorInt int shadowColor) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(AptoideUtils.IconSizeU.generateSizeStoreString(url, resources, windowManager))
          .apply(getRequestOptions().transform(
              new ShadowCircleTransformation(context, imageView, shadowColor)))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithShadowCircleTransformWithPlaceholder(String url,
      ImageView imageView, float strokeSize, @AttrRes int placeHolderDrawable) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(AptoideUtils.IconSizeU.generateSizeStoreString(url, resources, windowManager))
          .apply(getRequestOptions().transform(
              new ShadowCircleTransformation(context, imageView, strokeSize))
              .placeholder(getAttrDrawable(placeHolderDrawable)))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithShadowCircleTransform(@DrawableRes int drawableId,
      ImageView imageView, @ColorInt int shadowColor) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(drawableId)
          .apply(getRequestOptions().transform(
              new ShadowCircleTransformation(context, imageView, shadowColor)))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public NotificationTarget loadImageToNotification(NotificationTarget notificationTarget,
      String url) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context.getApplicationContext())
          .asBitmap()
          .load(url)
          .apply(getRequestOptions())
          .into(notificationTarget);
    } else {
      Log.e(TAG, "::loadImageToNotification() Context is null");
    }
    return notificationTarget;
  }

  public Target<Drawable> load(@DrawableRes int drawableId, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(drawableId)
          .apply(getRequestOptions())
          .into(imageView);
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadScreenshotToThumb(String url, String orientation,
      @AttrRes int loadingPlaceHolder, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(
              AptoideUtils.IconSizeU.screenshotToThumb(url, orientation, windowManager, resources))
          .apply(getRequestOptions().placeholder(getAttrDrawable(loadingPlaceHolder)))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadScreenshotToThumb() Context is null");
    }
    return null;
  }

  public Target<Drawable> load(String url, @AttrRes int loadingPlaceHolder, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().placeholder(getAttrDrawable(loadingPlaceHolder)))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    return null;
  }

  public void loadWithPalettePlaceholder(String url, BitmapDrawable paletteSrcImage,
      @ColorInt int defaultColor, ImageView targetImageView) {
    Palette.from(paletteSrcImage.getBitmap())
        .maximumColorCount(6)
        .generate(palette -> loadWithColorPlaceholder(url, palette.getDominantColor(defaultColor),
            targetImageView));
  }

  public Target<Drawable> loadWithColorPlaceholder(String url, @ColorInt int colorInt,
      ImageView imageView) {
    Context context = weakContext.get();

    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().placeholder(new ColorDrawable(colorInt)))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithColorAttrPlaceholder(String url, @AttrRes int colorResource,
      ImageView imageView) {
    Context context = weakContext.get();

    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().placeholder(new ColorDrawable(getAttrColor(colorResource))))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    return null;
  }

  public Target<Drawable> load(String url, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      String newImageUrl = AptoideUtils.IconSizeU.getNewImageUrl(url, resources, windowManager);
      if (newImageUrl != null) {
        Uri uri = Uri.parse(newImageUrl);
        return Glide.with(context)
            .load(uri)
            .apply(getRequestOptions())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView);
      } else {
        Log.e(TAG, "newImageUrl is null");
      }
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    return null;
  }

  /**
   * Loads a Drawable resource from the app bundle.
   *
   * @param drawableId drawable id
   *
   * @return {@link Drawable} with the passing drawable id or null if id = 0
   */
  public Drawable load(@DrawableRes int drawableId) {
    if (drawableId == 0) {
      return null;
    }

    Context context = weakContext.get();
    if (context != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        return context.getResources()
            .getDrawable(drawableId, context.getTheme());
      }
      return context.getResources()
          .getDrawable(drawableId);
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    return null;
  }

  /**
   * Blocking call to load a bitmap.
   *
   * @return Loaded bitmap or null.
   */
  @WorkerThread public @Nullable Bitmap load(String apkIconPath) {
    Context context = weakContext.get();
    if (context != null) {
      try {
        return Glide.
            with(context)
            .asBitmap()
            .load(apkIconPath)
            .submit()
            .get();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadUsingCircleTransform(@DrawableRes int drawableId,
      ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(drawableId)
          .apply(getRequestOptions().transform(new CircleCrop()))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadUsingCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadUsingCircleTransform(@NonNull String url,
      @NonNull ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().transform(new CircleCrop()))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadUsingCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadUsingCircleTransformAndPlaceholder(String url, ImageView imageView,
      @DrawableRes int defaultImagePlaceholder) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().transform(new CircleCrop())
              .placeholder(defaultImagePlaceholder))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(imageView);
    } else {
      Log.e(TAG, "::loadUsingCircleTransformAndPlaceholder() Context is null");
    }
    return null;
  }

  public void loadWithRoundCorners(String image, int radius, ImageView previewImage,
      @AttrRes int placeHolderDrawableId) {
    Context context = weakContext.get();
    if (context != null) {
      Glide.with(context)
          .load(image)
          .apply(getRequestOptions().centerCrop()
              .placeholder(getAttrDrawable(placeHolderDrawableId))
              .transform(new CenterInside(), new RoundedCorners(radius)))
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(previewImage);
    }
  }

  public Target<Drawable> loadWithRoundCorners(String image, int radius, ImageView previewImage,
      @AttrRes int placeHolderDrawableId, RequestListener<Drawable> requestListener) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(image)
          .apply(getRequestOptions().centerCrop()
              .placeholder(getAttrDrawable(placeHolderDrawableId))
              .transform(new CenterCrop(), new RoundedCorners(radius)))
          .listener(requestListener)
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(previewImage);
    }
    return null;
  }

  public Target<Drawable> loadWithColorPlaceholderRoundCorners(String image, int radius,
      ImageView previewImage, @AttrRes int colorResource,
      RequestListener<Drawable> requestListener) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(image)
          .apply(getRequestOptions().centerCrop()
              .placeholder(new ColorDrawable(getAttrColor(colorResource)))
              .transform(new CenterCrop(), new RoundedCorners(radius)))
          .listener(requestListener)
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(previewImage);
    }
    return null;
  }


  public void loadIntoTarget(String imageUrl, SimpleTarget<Drawable> simpleTarget) {
    Context context = weakContext.get();
    if (context != null) {
      Glide.with(context)
          .load(imageUrl)
          .apply(getRequestOptions())
          .transition(DrawableTransitionOptions.withCrossFade())
          .into(simpleTarget);
    }
  }

  @SuppressLint("CheckResult") @NonNull private RequestOptions getRequestOptions() {
    RequestOptions requestOptions = new RequestOptions();
    DecodeFormat decodeFormat;
    if (Build.VERSION.SDK_INT >= 26) {
      decodeFormat = DecodeFormat.PREFER_ARGB_8888;
      requestOptions.disallowHardwareConfig();
    } else {
      decodeFormat = DecodeFormat.PREFER_RGB_565;
    }
    return requestOptions.format(decodeFormat)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
  }

  private @DrawableRes int getAttrDrawable(@AttrRes int attrId) {
    Context context = weakContext.get();
    if (context != null) {
      TypedValue value = new TypedValue();
      weakContext.get()
          .getTheme()
          .resolveAttribute(attrId, value, true);
      return value.resourceId;
    }
    return R.drawable.placeholder_square;
  }

  private @DrawableRes int getAttrColor(@AttrRes int attrId) {
    Context context = weakContext.get();
    if (context != null) {
      TypedValue value = new TypedValue();
      weakContext.get()
          .getTheme()
          .resolveAttribute(attrId, value, true);
      return value.data;
    }
    return 0;
  }
}
