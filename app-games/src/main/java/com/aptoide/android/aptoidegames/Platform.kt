package com.aptoide.android.aptoidegames

import android.os.Build
import cm.aptoide.pt.extensions.isMIUI
import cm.aptoide.pt.extensions.isMiuiOptimizationDisabled

object Platform {

  val shouldUseLegacyInstaller: Boolean
    get() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.R && isMIUI()
      && !isMiuiOptimizationDisabled()
}
