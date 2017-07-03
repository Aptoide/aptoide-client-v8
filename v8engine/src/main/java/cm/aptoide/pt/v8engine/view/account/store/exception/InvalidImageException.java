package cm.aptoide.pt.v8engine.view.account.store.exception;

import java.util.List;

public class InvalidImageException extends Exception {

  private final String errorCode;
  private final List<ImageError> imageErrors;

  public InvalidImageException(List<ImageError> imageErrors, String errorCode) {
    this.errorCode = errorCode;
    this.imageErrors = imageErrors;
  }

  public InvalidImageException(List<ImageError> imageErrors) {
    this.imageErrors = imageErrors;
    errorCode = null;
  }

  public List<ImageError> getImageErrors() {
    return imageErrors;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public enum ImageError {
    ERROR_DECODING, MIN_HEIGHT, MAX_HEIGHT, MIN_WIDTH, MAX_WIDTH, MAX_IMAGE_SIZE, API_ERROR
  }
}
