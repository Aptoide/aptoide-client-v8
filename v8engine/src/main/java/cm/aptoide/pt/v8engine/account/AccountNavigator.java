/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.account;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.navigation.ActivityNavigator;
import cm.aptoide.pt.navigation.FragmentNavigator;
import cm.aptoide.pt.v8engine.activity.LoginActivity;
import cm.aptoide.pt.v8engine.fragment.implementations.LoginSignUpFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.MyAccountFragment;

/**
 * Created by marcelobenites on 09/02/17.
 */
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

  public void navigateToAccountView() {
    if (accountManager.isLoggedIn()) {
      fragmentNavigator.navigateTo(MyAccountFragment.newInstance());
    } else {
      fragmentNavigator.navigateTo(LoginSignUpFragment.newInstance(false, false, false));
    }
  }

  public void navigateToLoginView() {
    activityNavigator.navigateTo(LoginActivity.class);
  }
}
