package com.aptoide.android.aptoidegames.installer.analytics

import cm.aptoide.pt.extensions.canBeInstalledSilentlyBy
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.BuildConfig

interface SilentInstallChecker {
  fun canInstallSilently(packageName: String): Boolean = false
}

class SilentInstallCheckerImpl() : SilentInstallChecker {
  lateinit var installManager: InstallManager

  override fun canInstallSilently(packageName: String): Boolean = installManager
    .getApp(packageName)
    .installSourceInfo
    ?.canBeInstalledSilentlyBy(BuildConfig.APPLICATION_ID) == true
}
