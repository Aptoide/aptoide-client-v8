package com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Context.APP_OPS_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

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

/** Both permissions the usage-tracking mode needs: usage access and drawing over other apps. */
fun Context.hasPaEPermissions(): Boolean =
  hasUsageStatsPermissionStatus() && hasOverlayPermission()

/**
 * [hasPaEPermissions], read synchronously at first and again on every resume: the user may have
 * changed them in Android settings or in the permission screens meanwhile.
 */
@Composable
fun rememberHasPaEPermissions(): Boolean {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  var granted by remember { mutableStateOf(context.hasPaEPermissions()) }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) granted = context.hasPaEPermissions()
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  return granted
}

fun requiresRestrictedSettings() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
