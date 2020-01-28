package cm.aptoide.pt.account.view;

import cm.aptoide.pt.account.view.exception.InvalidImageException;
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
  private final Scheduler scheduler;
  private final ImageInfoProvider imageInfoProvider;

  public ImageValidator(Scheduler scheduler, ImageInfoProvider imageInfoProvider) {
    this.scheduler = scheduler;
    this.imageInfoProvider = imageInfoProvider;
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

  private ImageInfo getInfo(String imagePath) throws InvalidImageException {
    ImageInfo info = imageInfoProvider.getInfo(imagePath);
    if (info == null) {
      final List<InvalidImageException.ImageError> errors = new ArrayList<>();
      errors.add(InvalidImageException.ImageError.ERROR_DECODING);
      throw new InvalidImageException(errors);
    }
    return info;
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
