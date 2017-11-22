package cm.aptoide.pt.account;

public class GoogleSignUpException extends Exception {

  private final String error;

  public GoogleSignUpException(String error) {
    this.error = error;
  }

  public String getError() {
    return error;
  }
}