package cm.aptoide.pt.networking.image;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import cm.aptoide.pt.utils.AptoideUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;
import rx.subjects.PublishSubject;

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
            .apply(getRequestOptions())
            .into(-1, -1). // full size
            get();
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
          .apply(getRequestOptions().transform(new CircleTransform())
              .placeholder(placeHolderDrawableId))
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithCircleTransformAndPlaceHolder() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithCircleTransform(@DrawableRes int drawableId,
      ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(drawableId)
          .apply(getRequestOptions().transform(new CircleTransform()))
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithShadowCircleTransform(String url, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().transform(new ShadowCircleTransformation(context, imageView)))
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithShadowCircleTransformWithPlaceholder(String url,
      ImageView imageView, int drawable) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().transform(new ShadowCircleTransformation(context))
              .placeholder(drawable))
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
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithShadowCircleTransform(String url, ImageView imageView,
      float strokeSize) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(AptoideUtils.IconSizeU.generateSizeStoreString(url, resources, windowManager))
          .apply(getRequestOptions().transform(
              new ShadowCircleTransformation(context, imageView, strokeSize)))
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadWithShadowCircleTransformWithPlaceholder(String url,
      ImageView imageView, float strokeSize, int drawable) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(AptoideUtils.IconSizeU.generateSizeStoreString(url, resources, windowManager))
          .apply(getRequestOptions().transform(
              new ShadowCircleTransformation(context, imageView, strokeSize))
              .placeholder(drawable))
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
      @DrawableRes int loadingPlaceHolder, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(
              AptoideUtils.IconSizeU.screenshotToThumb(url, orientation, windowManager, resources))
          .apply(getRequestOptions().placeholder(loadingPlaceHolder))
          .into(imageView);
    } else {
      Log.e(TAG, "::loadScreenshotToThumb() Context is null");
    }
    return null;
  }

  public Target<Drawable> load(String url, @DrawableRes int loadingPlaceHolder,
      ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().placeholder(loadingPlaceHolder))
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
            .into(imageView);
      } else {
        Log.e(TAG, "newImageUrl is null");
      }
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    return null;
  }

  public Target<Bitmap> loadWithPalette(String url, ImageView imageView,
      PublishSubject<Palette.Swatch> viewPaletteSwatchReceiver) {
    Context context = weakContext.get();
    if (context != null) {
      String newImageUrl = AptoideUtils.IconSizeU.getNewImageUrl(url, resources, windowManager);
      if (newImageUrl != null) {
        Uri uri = Uri.parse(newImageUrl);
        return Glide.with(context)
            .asBitmap()
            .load(uri)
            .apply(getRequestOptions())
            .listener(new RequestListener<Bitmap>() {

              @Override public boolean onLoadFailed(@Nullable GlideException e, Object o,
                  Target<Bitmap> target, boolean b) {
                viewPaletteSwatchReceiver.onNext(null);
                Log.e(TAG, "RequestListener on failed called");
                return false;
              }

              @Override
              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target,
                  DataSource dataSource, boolean b) {
                Palette.from(bitmap)
                    .maximumColorCount(6)
                    .generate(palette -> {
                      Palette.Swatch swatch = palette.getDominantSwatch();
                      if (swatch == null) {
                        Log.e(TAG, "Unable to get palette dominant swatch");
                      }
                      viewPaletteSwatchReceiver.onNext(swatch);
                    });
                return false;
              }
            })
            .into(imageView);
      } else {
        Log.e(TAG, "newImageUrl is null");
      }
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    viewPaletteSwatchReceiver.onNext(null);
    return null;
  }

  public Target<Drawable> loadWithCenterCrop(String url, ImageView imageView) {
    return loadWithoutResizeCenterCrop(
        AptoideUtils.IconSizeU.getNewImageUrl(url, resources, windowManager), imageView);
  }

  public Target<Drawable> loadWithoutResizeCenterCrop(String url, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().centerCrop())
          .into(imageView);
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
            .apply(getRequestOptions())
            .into(-1, -1) // full size
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
          .apply(getRequestOptions().transform(new CircleTransform()))
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
          .apply(getRequestOptions().transform(new CircleTransform()))
          .into(imageView);
    } else {
      Log.e(TAG, "::loadUsingCircleTransform() Context is null");
    }
    return null;
  }

  public Target<Drawable> loadUsingCircleTransformAndPlaceholder(String url, ImageView imageView,
      int defaultImagePlaceholder) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .apply(getRequestOptions().transform(new CircleTransform())
              .placeholder(defaultImagePlaceholder))
          .into(imageView);
    } else {
      Log.e(TAG, "::loadUsingCircleTransformAndPlaceholder() Context is null");
    }
    return null;
  }

  public void loadWithRoundCorners(String image, int radius, int margin, ImageView previewImage) {
    Context context = weakContext.get();

    if (context != null) {
      Glide.with(context)
          .load(image)
          .apply(getRequestOptions().transforms(new CenterCrop(),
              new RoundedCornersTransform(context, radius, margin,
                  RoundedCornersTransform.CornerType.LEFT)))
          .into(previewImage);
    }
  }

  public void loadWithRoundCorners(String image, int radius, ImageView previewImage,
      @DrawableRes int placeHolderDrawableId) {
    Context context = weakContext.get();
    if (context != null) {
      Glide.with(context)
          .load(image)
          .apply(getRequestOptions().centerCrop()
              .placeholder(placeHolderDrawableId)
              .transforms(new CenterCrop(), new RoundedCornersTransform(context, radius, 0,
                  RoundedCornersTransform.CornerType.ALL)))
          .into(previewImage);
    }
  }

  public void loadWithRoundCornersWithoutCache(String image, int radius, ImageView previewImage,
      @DrawableRes int placeHolderDrawableId) {
    Context context = weakContext.get();
    if (context != null) {
      Glide.with(context)
          .load(image)
          .apply(getRequestOptions().centerCrop()
              .diskCacheStrategy(DiskCacheStrategy.NONE)
              .placeholder(placeHolderDrawableId)
              .transforms(new CenterCrop(), new RoundedCornersTransform(context, radius, 0,
                  RoundedCornersTransform.CornerType.ALL)))
          .into(previewImage);
    }
  }

  public void loadIntoTarget(String imageUrl, SimpleTarget<Drawable> simpleTarget) {
    Context context = weakContext.get();
    if (context != null) {
      Glide.with(context)
          .load(imageUrl)
          .apply(getRequestOptions())
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
}
