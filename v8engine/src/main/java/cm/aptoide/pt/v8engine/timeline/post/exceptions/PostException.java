package cm.aptoide.pt.v8engine.timeline.post.exceptions;

/**
 * Created by trinkes on 17/07/2017.
 */

public class PostException extends Exception {
  private final ErrorCode errorCode;

  public PostException(ErrorCode errorCode) {

    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public enum ErrorCode {
    INVALID_TEXT, INVALID_PACKAGE, NO_LOGIN, NO_APP_FOUND
  }
}
