/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.account;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.account.LoginActivity;

public class AccountAuthenticatorService extends Service {

  @Override public IBinder onBind(Intent intent) {
    final AccountAuthenticator authenticator = new AccountAuthenticator(this,
        ((AptoideApplication) getApplicationContext()).getAccountManager(),
        CrashReport.getInstance(), AccountManager.get(getApplicationContext()),
        new Intent(getApplicationContext(), LoginActivity.class));
    return authenticator.getIBinder();
  }
}
