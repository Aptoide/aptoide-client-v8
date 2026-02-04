package com.aptoide.android.aptoidegames.device_info.analytics

import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import javax.inject.Inject

class DeviceInfoAnalytics @Inject constructor(
  private val genericAnalytics: GenericAnalytics,
) {

  fun sendDeviceEmulator() = genericAnalytics.logEvent(
    name = "emulator_device_detected",
    params = null
  )

  fun sendDeviceRooted() = genericAnalytics.logEvent(
    name = "rooted_device_detected",
    params = null
  )
}
