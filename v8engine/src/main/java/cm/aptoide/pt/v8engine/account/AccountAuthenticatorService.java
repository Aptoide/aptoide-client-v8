/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.account;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.account.LoginActivity;

public class AccountAuthenticatorService extends Service {

  @Override public IBinder onBind(Intent intent) {
    final AccountAuthenticator authenticator = new AccountAuthenticator(this,
        ((V8Engine) getApplicationContext()).getAccountManager(), CrashReport.getInstance(),
        AccountManager.get(getApplicationContext()),
        new Intent(getApplicationContext(), LoginActivity.class));
    return authenticator.getIBinder();
  }
}
