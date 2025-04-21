package com.aptoide.android.aptoidegames

import android.os.Build
import cm.aptoide.pt.extensions.isMIUI
import cm.aptoide.pt.extensions.isMiuiOptimizationDisabled

object Platform {

  val shouldUseLegacyInstaller: Boolean
    get() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.R && isMIUI()
      && !isMiuiOptimizationDisabled()

  val isHmd: Boolean = BuildConfig.MARKET_NAME == "aptoide-games-hmd"

  val isHmdDevice: Boolean = Build.MANUFACTURER.lowercase().contains("hmd")
    && (Build.MODEL.lowercase().contains("fusion") || Build.MODEL.lowercase()
    .contains("nighthawk"))
}
