package cm.aptoide.pt.v8engine.view.account.store;

class StoreValidationException extends Throwable {

  public static final int EMPTY_NAME = 0;
  public static final int EMPTY_AVATAR = 1;

  private final int errorCode;

  public StoreValidationException(int errorCode) {
    this.errorCode = errorCode;
  }

  public int getErrorCode() {
    return errorCode;
  }
}
