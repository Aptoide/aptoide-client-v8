package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics;

import cm.aptoide.accountmanager.AccountAnalytics;

import static cm.aptoide.pt.v8engine.analytics.Analytics.AccountEvents;

public class AccountEventsAnalytcs implements AccountAnalytics {
  @Override public void login(String email) {
    AccountEvents.login(email);
  }

  @Override public void signUp() {
    AccountEvents.signUp();
  }
}
