/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 20/10/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.accountmanager.AptoideAccountManager;
import javax.security.auth.login.LoginException;
import okio.Buffer;

/**
 * Created by marcelobenites on 20/10/16.
 */
public class AptoideUserAuthorization implements UserAuthorization {

  @Override public String getUserAuthorization() throws LoginException {
    final Buffer buffer = new Buffer();
    try {
      String userEmail = AptoideAccountManager.getUserEmail();
      if (userEmail != null) {
        buffer.write(userEmail.getBytes());
        return buffer.md5().hex().substring(0, 30);
      }
      throw new LoginException("Aptoide user not logged in. Can not generate user authorization.");
    } finally {
      buffer.close();
    }
  }
}
