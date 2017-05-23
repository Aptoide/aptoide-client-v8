package cm.aptoide.accountmanager;

import cm.aptoide.pt.logger.Logger;

/**
 * Created by trinkes on 22/05/2017.
 */
class LogAnalytics implements AccountAnalytics {
  private static final String TAG = LogAnalytics.class.getSimpleName();

  @Override public void login(String email) {
    Logger.d(TAG, "login() called with: " + "email = [" + email + "]");
  }

  @Override public void signUp() {
    Logger.d(TAG, "signUp() called");
  }
}
