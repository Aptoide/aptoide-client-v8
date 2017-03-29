/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.V8Engine;

import static cm.aptoide.pt.preferences.Application.getContext;

/**
 * Created by brutus on 11-12-2013.
 */
public class AccountAuthenticatorService extends Service {

  @Override public IBinder onBind(Intent intent) {
    final AccountAuthenticator authenticator = new AccountAuthenticator(this,
        ((V8Engine) getContext().getApplicationContext()).getAccountManager(),
        CrashReport.getInstance());
    return authenticator.getIBinder();
  }
}
