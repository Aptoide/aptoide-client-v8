package com.aptoide.android.aptoidegames.analytics

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

fun Context.getGMSValue(): String {
  return if (GoogleApiAvailability.getInstance()
      .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
  ) "Has GMS" else "No GMS"
}
