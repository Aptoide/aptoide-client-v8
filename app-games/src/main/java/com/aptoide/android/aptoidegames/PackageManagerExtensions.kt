package com.aptoide.android.aptoidegames

import android.content.pm.PackageManager
import android.os.Build

/**
 * Returns the package name of the installer that installed [packageName], handling the
 * getInstallSourceInfo (API 30+) vs deprecated getInstallerPackageName split. Returns null when the
 * installer is unknown or the package cannot be resolved.
 */
fun PackageManager.getInstallerPackageNameCompat(packageName: String): String? = runCatching {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    getInstallSourceInfo(packageName).installingPackageName
  } else {
    @Suppress("DEPRECATION")
    getInstallerPackageName(packageName)
  }
}.getOrNull()
