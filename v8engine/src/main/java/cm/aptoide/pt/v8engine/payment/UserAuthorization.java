/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 20/10/2016.
 */

package cm.aptoide.pt.v8engine.payment;

import javax.security.auth.login.LoginException;

/**
 * Created by marcelobenites on 20/10/16.
 */
public interface UserAuthorization {

  String getUserAuthorization() throws LoginException;

}
