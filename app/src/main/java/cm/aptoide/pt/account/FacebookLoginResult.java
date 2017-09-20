package cm.aptoide.pt.account;

import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

public class FacebookLoginResult {

  public static final int STATE_SUCCESS = 0;
  public static final int STATE_CANCELLED = 1;
  public static final int STATE_ERROR = 99;

  private final LoginResult result;
  private final int state;
  private final FacebookException error;

  public FacebookLoginResult(LoginResult result, int state, FacebookException error) {
    this.result = result;
    this.state = state;
    this.error = error;
  }

  public LoginResult getResult() {
    return result;
  }

  public int getState() {
    return state;
  }

  public FacebookException getError() {
    return error;
  }
}
