/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.pt.account;

import cm.aptoide.pt.remotebootconfig.datamodel.BootConfig;

public class LoginPreferences {

  private final BootConfig bootConfig;

  public LoginPreferences(BootConfig bootConfig) {
    this.bootConfig = bootConfig;
  }

  public boolean isGoogleLoginEnabled() {
    return false;
  }

  public boolean isFacebookLoginEnabled() {
    return bootConfig.getPartner()
        .getSocial()
        .getLogin()
        .isFacebook();
  }
}