package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics;

import cm.aptoide.accountmanager.Analytics;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AccountEvents;

/**
 * Created by trinkes on 11/21/16.
 */

public class AccountAnalytcs implements Analytics {
  @Override public void login(String action) {
    AccountEvents.login(action);
  }

  @Override public void signUp() {
    AccountEvents.signUp();
  }
}
