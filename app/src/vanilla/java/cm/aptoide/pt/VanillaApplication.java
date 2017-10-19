/*
 * Copyright (c) 2016.
 * Modified on 01/07/2016.
 */

package cm.aptoide.pt;

import cm.aptoide.pt.account.LoginPreferences;
import cm.aptoide.pt.view.configuration.FragmentProvider;
import cm.aptoide.pt.view.configuration.implementation.VanillaFragmentProvider;
import com.google.android.gms.common.GoogleApiAvailability;

public class VanillaApplication extends AptoideApplication {

  private ApplicationPreferences appPreferences;

  @Override public LoginPreferences getLoginPreferences() {
    return new LoginPreferences(this, GoogleApiAvailability.getInstance());
  }

  @Override public ApplicationPreferences getApplicationPreferences() {
    if (appPreferences == null) {
      appPreferences = new VanillaApplicationPreferences();
    }
    return appPreferences;
  }

  @Override public FragmentProvider createFragmentProvider() {
    return new VanillaFragmentProvider();
  }
}
