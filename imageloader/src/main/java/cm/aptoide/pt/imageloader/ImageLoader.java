package cm.aptoide.pt.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.utils.AptoideUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.Target;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

/**
 * Created by neuro on 24-05-2016.
 */
public class ImageLoader {

  private static final String TAG = ImageLoader.class.getName();

  private final WeakReference<Context> weakContext;

  private ImageLoader(Context context) {
    this.weakContext = new WeakReference<>(context);
  }

  @Partners public static ImageLoader with(Context context) {
    return new ImageLoader(context);
  }

  /**
   * Cancel the image loading request
   *
   * @param target Previously returned {@link Target} from {@link ImageLoader}.load...
   */
  public static void cancel(View target) {
    Glide.clear(target);
  }

  /**
   * Cancel the image loading request
   *
   * @param target Previously returned {@link Target} from {@link ImageLoader}.load...
   */
  public static <R> void cancel(FutureTarget<R> target) {
    Glide.clear(target);
  }

  /**
   * Cancel the image loading request
   *
   * @param target Previously returned {@link Target} from {@link ImageLoader}.load...
   */
  public static <R> void cancel(Target<R> target) {
    Glide.clear(target);
  }

  /**
   * Blocking call to load a bitmap.
   *
   * @param apkIconPath Path for the bitmap to be loaded.
   *
   * @return Loaded bitmap or null.
   */
  @WorkerThread public @Nullable Bitmap loadBitmap(String apkIconPath) {
    Context context = weakContext.get();
    if (context != null) {
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
  public Target<GlideDrawable> loadWithCircleTransformAndPlaceHolderAvatarSize(String url,
      ImageView imageView, @DrawableRes int placeHolderDrawableId) {
    return loadWithCircleTransformAndPlaceHolder(AptoideUtils.IconSizeU.generateStringAvatar(url),
        imageView, placeHolderDrawableId);
  }

  public Target<GlideDrawable> loadWithCircleTransformAndPlaceHolder(String url,
      ImageView imageView, @DrawableRes int placeHolderDrawableId) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .transform(new CircleTransform(context))
          .placeholder(placeHolderDrawableId)
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithCircleTransformAndPlaceHolder() Context is null");
    }
    return null;
  }

  public Target<GlideDrawable> loadWithCircleTransform(Uri url, ImageView imageView,
      boolean cache) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url.toString())
          .transform(new CircleTransform(context))
          .skipMemoryCache(!cache)
          .diskCacheStrategy(cache ? DiskCacheStrategy.RESULT : DiskCacheStrategy.NONE)
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithCircleTransform() Context is null");
    }
    return null;
  }

  public Target<GlideDrawable> loadWithShadowCircleTransform(String url, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(url)
          .transform(new ShadowCircleTransformation(context, imageView))
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<GlideDrawable> loadWithShadowCircleTransform(@DrawableRes int drawableId,
      ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .fromResource()
          .load(drawableId)
          .transform(new ShadowCircleTransformation(context, imageView))
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<GlideDrawable> loadWithShadowCircleTransform(String url, ImageView imageView,
      @ColorInt int shadowColor) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(AptoideUtils.IconSizeU.generateSizeStoreString(url))
          .transform(new ShadowCircleTransformation(context, imageView, shadowColor))
          .into(imageView);
    } else {
      Log.e(TAG, "::loadWithShadowCircleTransform() Context is null");
    }
    return null;
  }

  public Target<GlideDrawable> loadWithShadowCircleTransform(@DrawableRes int drawableId,
      ImageView imageView, @ColorInt int shadowColor) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .fromResource()
          .load(drawableId)
          .transform(new ShadowCircleTransformation(context, imageView, shadowColor))
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
          .load(AptoideUtils.IconSizeU.generateStringNotification(url))
          .asBitmap()
          .into(notificationTarget);
    } else {
      Log.e(TAG, "::loadImageToNotification() Context is null");
    }
    return notificationTarget;
  }

  public Target<GlideDrawable> load(@DrawableRes int drawableId, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context).load(drawableId).into(imageView);
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    return null;
  }

  public Target<GlideDrawable> loadScreenshotToThumb(String url, String orientation,
      @DrawableRes int loadingPlaceHolder, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .load(AptoideUtils.IconSizeU.screenshotToThumb(url, orientation))
          .placeholder(loadingPlaceHolder)
          .into(imageView);
    } else {
      Log.e(TAG, "::loadScreenshotToThumb() Context is null");
    }
    return null;
  }

  public Target<GlideDrawable> load(String url, @DrawableRes int loadingPlaceHolder,
      ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context).load(url).placeholder(loadingPlaceHolder).into(imageView);
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    return null;
  }

  public Target<GlideDrawable> load(String url, ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context).load(AptoideUtils.IconSizeU.getNewImageUrl(url)).into(imageView);
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
        return context.getResources().getDrawable(drawableId, context.getTheme());
      }
      return context.getResources().getDrawable(drawableId);
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
    } else {
      Log.e(TAG, "::load() Context is null");
    }
    return null;
  }

  public Target<GlideDrawable> loadUsingCircleTransform(@DrawableRes int drawableId,
      ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context)
          .fromResource()
          .load(drawableId)
          .transform(new CircleTransform(context))
          .into(imageView);
    } else {
      Log.e(TAG, "::loadUsingCircleTransform() Context is null");
    }
    return null;
  }

  public Target<GlideDrawable> loadUsingCircleTransform(@NonNull Uri url,
      @NonNull ImageView imageView) {
    return loadUsingCircleTransform(url.toString(), imageView);
  }

  public Target<GlideDrawable> loadUsingCircleTransform(@NonNull String url,
      @NonNull ImageView imageView) {
    Context context = weakContext.get();
    if (context != null) {
      return Glide.with(context).load(url).transform(new CircleTransform(context)).into(imageView);
    } else {
      Log.e(TAG, "::loadUsingCircleTransform() Context is null");
    }
    return null;
  }
}
