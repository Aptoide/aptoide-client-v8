/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 20/10/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.util.UserCompleteData;
import javax.security.auth.login.LoginException;
import okio.ByteString;

/**
 * Created by marcelobenites on 20/10/16.
 */
public class AptoideUserAuthorization implements UserAuthorization {

  @Override public String getUserAuthorization() throws LoginException {
    if (AptoideAccountManager.isLoggedIn()) {
      UserCompleteData userInfo = AptoideAccountManager.getUserData();
      return ByteString.of(userInfo.getId().getBytes()).base64();
    }
    throw new LoginException("Aptoide user not logged in. Can not generate user authorization.");
  }
}