/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.pt.account;

import android.content.Context;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LoginPreferences {

  private final Context context;
  private final GoogleApiAvailability googleApiAvailability;

  public LoginPreferences(Context context, GoogleApiAvailability googleApiAvailability) {
    this.context = context;
    this.googleApiAvailability = googleApiAvailability;
  }

  public boolean isGoogleLoginEnabled() {
    return googleApiAvailability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
  }

  public boolean isFacebookLoginEnabled() {
    return true;
  }
}