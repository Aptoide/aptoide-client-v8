/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.navigation;

import android.content.Context;
import android.content.Intent;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.activity.LoginActivity;
import cm.aptoide.pt.v8engine.fragment.implementations.LoginSignUpFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.MyAccountFragment;

/**
 * Created by marcelobenites on 09/02/17.
 */
public class AccountNavigator {

  private final Context context;
  private final NavigationManagerV4 navigationManager;
  private final AptoideAccountManager accountManager;

  public AccountNavigator(Context context, NavigationManagerV4 navigationManager,
      AptoideAccountManager accountManager) {
    this.context = context;
    this.navigationManager = navigationManager;
    this.accountManager = accountManager;
  }

  public void navigateToAccountView() {
    if (accountManager.isLoggedIn()) {
      navigationManager.navigateTo(MyAccountFragment.newInstance());
    } else {
      navigationManager.navigateTo(LoginSignUpFragment.newInstance(false, false, false));
    }
  }

  public void navigateToLoginView() {
    context.startActivity(new Intent(context, LoginActivity.class));
  }
}
