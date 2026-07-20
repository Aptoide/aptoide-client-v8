package com.aptoide.android.aptoidegames.app_open_ads

import android.content.Context
import com.aptoide.android.aptoidegames.device_info.DeviceCountryProvider

class AppOpenGeoProvider(private val context: Context) {

  fun getGeo(): String = DeviceCountryProvider(context).getCountry() ?: UNKNOWN_GEO

  companion object {
    const val UNKNOWN_GEO = "UNKNOWN"
  }
}
