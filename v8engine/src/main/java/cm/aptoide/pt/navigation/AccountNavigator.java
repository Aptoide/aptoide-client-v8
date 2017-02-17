/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.fragment.implementations.LoginSignUpFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.MyAccountFragment;
import cm.aptoide.pt.v8engine.view.MainActivity;

/**
 * Created by marcelobenites on 09/02/17.
 */
public class AccountNavigator {

  private final Context context;
  private final AptoideAccountManager accountManager;

  public AccountNavigator(Context context, AptoideAccountManager accountManager) {
    this.context = context;
    this.accountManager = accountManager;
  }

  public void navigateToAccountView() {
    navigateToAccountView(null);
  }

  // FIXME: 16/2/2017 sithengineer
  public void navigateToAccountView(boolean useSkip) {
    Bundle extras = new Bundle();
    //extras.putBoolean(LoginSignUpFragment.SKIP_BUTTON, useSkip);
    navigateToAccountView(extras);
  }

  private void navigateToAccountView(@Nullable Bundle extras) {
    if (accountManager.isLoggedIn()) {
      context.startActivity(new Intent(context, MyAccountFragment.class));
    } else {
      final Intent intent = new Intent(context, MainActivity.class);
      intent.putExtra(MainActivity.FRAGMENT, LoginSignUpFragment.class.getName());
      if (extras != null) {
        intent.putExtras(extras);
      }
      context.startActivity(intent);
    }
  }
}
