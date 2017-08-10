package cm.aptoide.pt.account;

import cm.aptoide.accountmanager.AccountAnalytics;
import cm.aptoide.pt.logger.Logger;

/**
 * Created by trinkes on 22/05/2017.
 */
public class LogAccountAnalytics implements AccountAnalytics {
  private static final String TAG = LogAccountAnalytics.class.getSimpleName();

  @Override public void login(String email) {
    Logger.d(TAG, "login() called with: " + "email = [" + email + "]");
  }

  @Override public void signUp() {
    Logger.d(TAG, "signUp() called");
  }
}
