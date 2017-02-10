/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/01/2017.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import android.content.IntentFilter;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.utils.BroadcastRegisterOnSubscribe;
import cm.aptoide.pt.v8engine.activity.AccountNavigator;
import javax.security.auth.login.LoginException;
import rx.Observable;

/**
 * Created by marcelobenites on 06/01/17.
 */
public class Payer {

  public final Context context;
  private final AptoideAccountManager accountManager;
  private final AccountNavigator accountNavigator;

  public Payer(Context context, AptoideAccountManager accountManager,
      AccountNavigator accountNavigator) {
    this.context = context;
    this.accountManager = accountManager;
    this.accountNavigator = accountNavigator;
  }

  public String getId() {
    return accountManager.getUserEmail();
  }

  public boolean isLoggedIn() {
    return accountManager.isLoggedIn();
  }

  public Observable<Void> login() {
    return Observable.fromCallable(() -> {
      if (isLoggedIn()) {
        return null;
      }
      IntentFilter loginFilter = new IntentFilter(AptoideAccountManager.LOGIN);
      loginFilter.addAction(AptoideAccountManager.LOGIN_CANCELLED);
      loginFilter.addAction(AptoideAccountManager.LOGOUT);
      return loginFilter;
    }).flatMap(intentFilter -> {
      if (intentFilter == null) {
        return Observable.just(null);
      }
      return Observable.create(new BroadcastRegisterOnSubscribe(context, intentFilter, null, null))
          .doOnSubscribe(() -> {
            accountNavigator.navigateToAccountView(false);
          })
          .flatMap(intent -> {
            if (AptoideAccountManager.LOGIN.equals(intent.getAction())) {
              return Observable.just(null);
            } else if (AptoideAccountManager.LOGIN_CANCELLED.equals(intent.getAction())) {
              return Observable.error(new LoginException("User cancelled login."));
            } else if (AptoideAccountManager.LOGOUT.equals(intent.getAction())) {
              return Observable.error(new LoginException("User logged out."));
            }
            return Observable.empty();
          });
    });
  }
}
