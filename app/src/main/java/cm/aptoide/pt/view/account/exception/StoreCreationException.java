package cm.aptoide.pt.view.account.exception;

public class StoreCreationException extends Exception {

  private final String errorCode;

  public StoreCreationException() {
    errorCode = null;
  }

  public StoreCreationException(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public boolean hasErrorCode() {
    return errorCode != null;
  }
}
