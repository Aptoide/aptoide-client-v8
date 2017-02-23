/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.navigation;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.fragment.implementations.JoinCommunityFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.MyAccountFragment;

/**
 * Created by marcelobenites on 09/02/17.
 */
public class AccountNavigator {

  private final NavigationManagerV4 navigationManager;
  private final AptoideAccountManager accountManager;

  public AccountNavigator(NavigationManagerV4 navigationManager,
      AptoideAccountManager accountManager) {
    this.navigationManager = navigationManager;
    this.accountManager = accountManager;
  }

  public void navigateToAccountView() {
    if (accountManager.isLoggedIn()) {
      navigationManager.navigateTo(MyAccountFragment.newInstance());
    } else {
      navigationManager.navigateTo(JoinCommunityFragment.newInstance(true));
    }
  }
}
