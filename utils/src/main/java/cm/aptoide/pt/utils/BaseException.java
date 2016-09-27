package cm.aptoide.pt.utils;

/**
 * created by SithEngineer
 */
public abstract class BaseException extends Exception {
  public BaseException() {}

  public BaseException(String detailMessage) {
    super(detailMessage);
  }

  public BaseException(Throwable throwable) {
    super(throwable);
  }
}
