package cm.aptoide.pt.account;

public class FacebookSignUpException extends Exception {

  public static final int MISSING_REQUIRED_PERMISSIONS = 1;
  public static final int USER_CANCELLED = 2;
  public static final int ERROR = 99;

  private final int code;
  private final String facebookMessage;

  public FacebookSignUpException(int code, String message) {
    this.code = code;
    facebookMessage = message;
  }

  public String getFacebookMessage() {
    return facebookMessage;
  }

  public int getCode() {
    return code;
  }
}
