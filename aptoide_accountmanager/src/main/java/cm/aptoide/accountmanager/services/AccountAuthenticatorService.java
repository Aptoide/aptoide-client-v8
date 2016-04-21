package cm.aptoide.accountmanager.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import cm.aptoide.accountmanager.AccountAuthenticator;

/**
 * Created by brutus on 11-12-2013.
 */
public class AccountAuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        AccountAuthenticator authenticator = new AccountAuthenticator(this);
        return authenticator.getIBinder();
    }

}
