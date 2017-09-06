package cm.aptoide.pt.account;

import cm.aptoide.accountmanager.AccountValidationException;

public class FacebookAccountException extends AccountValidationException {

  public static final int FACEBOOK_DENIED_CREDENTIALS = 1;
  public static final int FACEBOOK_API_INVALID_RESPONSE = 2;

  public FacebookAccountException(int code) {
    super(code);
  }
}
