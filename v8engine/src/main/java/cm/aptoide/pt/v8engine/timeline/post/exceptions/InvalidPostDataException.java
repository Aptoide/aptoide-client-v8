package cm.aptoide.pt.v8engine.timeline.post.exceptions;

/**
 * Created by trinkes on 17/07/2017.
 */

public class InvalidPostDataException extends Exception {
  private final ErrorCode errorCode;

  public InvalidPostDataException(ErrorCode errorCode) {

    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public enum ErrorCode {
    INVALID_TEXT, INVALID_PACKAGE
  }
}
