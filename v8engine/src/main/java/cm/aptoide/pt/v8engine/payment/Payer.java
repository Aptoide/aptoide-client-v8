/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/01/2017.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import cm.aptoide.accountmanager.AptoideAccountManager;
import rx.Observable;

/**
 * Created by marcelobenites on 06/01/17.
 */
public class Payer {

  public final Context context;
  private AptoideAccountManager accountManager;

  public Payer(Context context, AptoideAccountManager accountManager) {
    this.context = context;
    this.accountManager = accountManager;
  }

  public String getId() {
    return accountManager.getUserEmail();
  }

  public boolean isLoggedIn() {
    return accountManager.isLoggedIn();
  }

  public Observable<Void> login() {
    return accountManager.login(context);
  }
}
