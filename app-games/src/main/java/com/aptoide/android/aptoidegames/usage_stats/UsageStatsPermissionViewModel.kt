package com.aptoide.android.aptoidegames.usage_stats

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import android.provider.Settings
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.installer.platform.UserActionLauncher
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class UsageStatsPermissionViewModel @Inject constructor(
  private val userActionLauncher: UserActionLauncher,
  @ApplicationContext private val context: Context
) : ViewModel() {

  suspend fun requestUsageStatsPermission() = if (getGrantStatus() == false) {
    userActionLauncher.launchIntent(
      Intent(
        Settings.ACTION_USAGE_ACCESS_SETTINGS
      )
    )
  } else {
    true
  }

  suspend fun requestOverlayPermission() {
    if (!Settings.canDrawOverlays(context)) {
      userActionLauncher.launchIntent(
        Intent(
          Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
          ("package:" + context.packageName).toUri()
        )
      )
    }
  }

  fun getGrantStatus(): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

    val mode = appOps.checkOpNoThrow(
      AppOpsManager.OPSTR_GET_USAGE_STATS,
      Process.myUid(), context.packageName
    )

    if (mode == AppOpsManager.MODE_DEFAULT) {
      return (context.checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED)
    } else {
      return (mode == AppOpsManager.MODE_ALLOWED)
    }
  }
}

fun Context.hasUsageStatsPermission(): Boolean {
  val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

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
