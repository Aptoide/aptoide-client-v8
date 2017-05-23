package cm.aptoide.pt.v8engine.account;

import cm.aptoide.accountmanager.AccountAnalytics;
import cm.aptoide.pt.logger.Logger;

/**
 * Created by trinkes on 22/05/2017.
 */
public class AccountEventsAnalytics implements AccountAnalytics {
  private static final String TAG = AccountEventsAnalytics.class.getSimpleName();

  @Override public void login(String email) {
    Logger.d(TAG, "login() called with: " + "email = [" + email + "]");
  }

  @Override public void signUp() {
    Logger.d(TAG, "signUp() called");
  }
}
