/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.pt.v8engine.account;

import android.content.Context;
import cm.aptoide.pt.preferences.AptoidePreferencesConfiguration;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LoginPreferences {

  private final Context context;
  private final AptoidePreferencesConfiguration configuration;
  private final GoogleApiAvailability googleApiAvailability;

  public LoginPreferences(Context context,
      AptoidePreferencesConfiguration configuration, GoogleApiAvailability googleApiAvailability) {
    this.context = context;
    this.configuration = configuration;
    this.googleApiAvailability = googleApiAvailability;
  }

  public boolean isGoogleLoginEnabled() {
    return googleApiAvailability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        && configuration.isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.GOOGLE);
  }

  public boolean isFacebookLoginEnabled() {
    return configuration.isLoginAvailable(AptoidePreferencesConfiguration.SocialLogin.FACEBOOK);
  }
}