/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;

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

  public void navigateToAccountView(boolean useSkip) {
    Bundle extras = new Bundle();
    extras.putBoolean(LoginActivity.SKIP_BUTTON, useSkip);
    navigateToAccountView(extras);
  }

  private void navigateToAccountView(@Nullable Bundle extras) {
    if (accountManager.isLoggedIn()) {
      context.startActivity(new Intent(context, MyAccountActivity.class));
    } else {
      final Intent intent = new Intent(context, LoginActivity.class);
      if (extras != null) {
        intent.putExtras(extras);
      }
      //Intent intent = new Intent(applicationContext, CreateStoreActivity.class);
      context.startActivity(intent);
    }
  }
}
