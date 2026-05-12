package com.aptoide.android.aptoidegames.app_open_ads

import android.content.Context
import android.telephony.TelephonyManager
import java.util.Locale

class AppOpenGeoProvider(private val context: Context) {

  fun getGeo(): String {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    return listOf(
      telephonyManager?.networkCountryIso,
      telephonyManager?.simCountryIso,
      context.resources.configuration.locales[0]?.country,
    )
      .firstOrNull { !it.isNullOrBlank() }
      ?.uppercase(Locale.US)
      ?: UNKNOWN_GEO
  }

  companion object {
    const val UNKNOWN_GEO = "UNKNOWN"
  }
}
