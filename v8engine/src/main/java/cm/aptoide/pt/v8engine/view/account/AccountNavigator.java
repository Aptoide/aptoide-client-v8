/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.view.account;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.view.navigator.ActivityNavigator;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import rx.Observable;

public class AccountNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final AptoideAccountManager accountManager;
  private final ActivityNavigator activityNavigator;

  public AccountNavigator(FragmentNavigator fragmentNavigator, AptoideAccountManager accountManager,
      ActivityNavigator activityNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.accountManager = accountManager;
    this.activityNavigator = activityNavigator;
  }

  public void navigateToAccountView(Analytics.Account.AccountOrigins accountOrigins) {
    if (accountManager.isLoggedIn()) {
      fragmentNavigator.navigateTo(MyAccountFragment.newInstance());
    } else {
      Analytics.Account.enterAccountScreen(accountOrigins);
      fragmentNavigator.navigateTo(LoginSignUpFragment.newInstance(false, false, false));
    }
  }

  public Observable<Boolean> navigateToLoginViewForResult(int requestCode) {
    return activityNavigator.navigateForResult(LoginActivity.class, requestCode)
        .flatMapSingle(result -> accountManager.accountStatus()
            .first()
            .toSingle())
        .map(account -> account.isLoggedIn());
  }
}
