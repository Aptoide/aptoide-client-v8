package com.aptoide.android.aptoidegames

import android.os.Build
import cm.aptoide.pt.extensions.isMIUI
import cm.aptoide.pt.extensions.isMiuiOptimizationDisabled

object Platform {

  val shouldUseLegacyInstaller: Boolean
    get() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.R && isMIUI()
      && !isMiuiOptimizationDisabled()

  val isHMD: Boolean
    get() = BuildConfig.MARKET_NAME == "aptoide-games-hmd"
  
  val isHMDdevice: Boolean
    get() = Build.MANUFACTURER.lowercase().contains("hmd")
      && (Build.MODEL.lowercase().contains("fusion") || Build.MODEL.lowercase()
      .contains("nighthawk"))
}
