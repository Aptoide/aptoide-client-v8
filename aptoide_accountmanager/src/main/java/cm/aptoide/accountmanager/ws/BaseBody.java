/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.accountmanager.ws;

import java.util.HashMap;

/**
 * Created by neuro on 18-05-2016.
 */
public class BaseBody extends HashMap<String, String> {

  public void setAccess_token(String token) {
    put("access_token", token);
  }
}
