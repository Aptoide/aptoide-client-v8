package cm.aptoide.pt.account;

public class GoogleSignUpException extends Exception {

  private final String error;
  private final int statusCode;

  public GoogleSignUpException(String error, int statusCode) {
    this.error = error;
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getError() {
    return error;
  }
}