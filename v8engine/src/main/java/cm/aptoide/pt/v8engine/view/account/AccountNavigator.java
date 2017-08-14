/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.view.account;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;

public class AccountNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final AptoideAccountManager accountManager;

  public AccountNavigator(FragmentNavigator fragmentNavigator,
      AptoideAccountManager accountManager) {
    this.fragmentNavigator = fragmentNavigator;
    this.accountManager = accountManager;
  }

  public void navigateToAccountView(Analytics.Account.AccountOrigins accountOrigins) {
    if (accountManager.isLoggedIn()) {
      fragmentNavigator.navigateTo(MyAccountFragment.newInstance());
    } else {
      Analytics.Account.enterAccountScreen(accountOrigins);
      fragmentNavigator.navigateTo(LoginSignUpFragment.newInstance(false, false, false));
    }
  }
}
