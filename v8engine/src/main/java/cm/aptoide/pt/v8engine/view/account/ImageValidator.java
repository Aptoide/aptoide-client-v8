package cm.aptoide.pt.v8engine.view.account;

import android.graphics.Bitmap;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.view.account.exception.InvalidImageException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Scheduler;

public class ImageValidator {
  private static final int MIN_IMAGE_HEIGHT = 600;
  private static final int MAX_IMAGE_HEIGHT = 10240;
  private static final int MIN_IMAGE_WIDTH = 600;
  private static final int MAX_IMAGE_WIDTH = 10240;
  private static final int MAX_IMAGE_SIZE_IN_BYTES = 5242880;
  private final ImageLoader imageLoader;
  private final Scheduler scheduler;

  public ImageValidator(ImageLoader imageLoader, Scheduler scheduler) {
    this.imageLoader = imageLoader;
    this.scheduler = scheduler;
  }

  /**
   * @return {@link InvalidImageException} if image is not valid.
   */
  public Completable validateOrGetException(String imagePath) {
    return Completable.defer(() -> {
      try {
        validate(imagePath);
        return Completable.complete();
      } catch (InvalidImageException ex) {
        return Completable.error(ex);
      }
    })
        .subscribeOn(scheduler);
  }

  private ImageInfo getInfo(String imagePath) {
    ImageInfo imageInfo = null;
    Bitmap image = imageLoader.load(imagePath);
    //Bitmap image = BitmapFactory.decodeFile(imagePath);
    if (image != null) {
      imageInfo = new ImageInfo(image.getWidth(), image.getHeight(), new File(imagePath).length());
    }
    return imageInfo;
  }

  private void validate(String imagePath) throws InvalidImageException {
    ImageInfo imageInfo = getInfo(imagePath);
    final List<InvalidImageException.ImageError> errors = new ArrayList<>();
    if (imageInfo == null) {
      errors.add(InvalidImageException.ImageError.ERROR_DECODING);
    } else {
      if (imageInfo.getHeight() < MIN_IMAGE_HEIGHT) {
        errors.add(InvalidImageException.ImageError.MIN_HEIGHT);
      }
      if (imageInfo.getWidth() < MIN_IMAGE_WIDTH) {
        errors.add(InvalidImageException.ImageError.MIN_WIDTH);
      }
      if (imageInfo.getHeight() > MAX_IMAGE_HEIGHT) {
        errors.add(InvalidImageException.ImageError.MAX_HEIGHT);
      }
      if (imageInfo.getWidth() > MAX_IMAGE_WIDTH) {
        errors.add(InvalidImageException.ImageError.MAX_WIDTH);
      }
      if (imageInfo.getSize() > MAX_IMAGE_SIZE_IN_BYTES) {
        errors.add(InvalidImageException.ImageError.MAX_IMAGE_SIZE);
      }
    }

    if (!errors.isEmpty()) {
      throw new InvalidImageException(errors);
    }
  }
}
