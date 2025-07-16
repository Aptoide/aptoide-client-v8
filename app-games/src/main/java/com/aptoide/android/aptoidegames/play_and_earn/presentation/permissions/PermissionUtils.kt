package com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Context.APP_OPS_SERVICE
import android.content.pm.PackageManager
import android.os.Process
import android.provider.Settings

fun Context.hasUsageStatsPermissionStatus(appOpsManager: AppOpsManager? = null): Boolean {
  val appOps = appOpsManager ?: (getSystemService(APP_OPS_SERVICE) as AppOpsManager)

  val mode = appOps.checkOpNoThrow(
    AppOpsManager.OPSTR_GET_USAGE_STATS,
    Process.myUid(), packageName
  )

  if (mode == AppOpsManager.MODE_DEFAULT) {
    return (checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED)
  } else {
    return (mode == AppOpsManager.MODE_ALLOWED)
  }
}

fun Context.hasOverlayPermission() = Settings.canDrawOverlays(this)
