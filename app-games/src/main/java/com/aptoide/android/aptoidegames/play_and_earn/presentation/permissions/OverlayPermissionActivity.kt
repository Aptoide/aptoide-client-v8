package com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions

import android.app.AppOpsManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OverlayPermissionActivity : AppCompatActivity() {

  private lateinit var appOps: AppOpsManager

  private var permissionCheckJob: Job? = null
  private val checkInterval = 600L

  val overlayPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      bringAppToForeground()
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager

    startPermissionCheck()

    overlayPermissionLauncher.launch(
      Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        ("package:$packageName").toUri()
      )
    )
  }

  fun launchPermission() {
    permissionCheckJob?.cancel()
    startActivity(Intent(applicationContext, UsageStatsPermissionActivity::class.java))
    finish()
  }

  private fun startPermissionCheck() {
    permissionCheckJob = lifecycleScope.launch(Dispatchers.Main.immediate) {
      while (true) {
        if (hasOverlayPermission()) {
          if (hasUsageStatsPermissionStatus(appOps)) {
            bringAppToForeground()
            break
          } else {
            launchPermission()
            break
          }
        }
        delay(checkInterval)
      }
    }
  }

  private fun bringAppToForeground() {
    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
    launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(launchIntent)
    finish()
  }

  override fun onDestroy() {
    super.onDestroy()
    permissionCheckJob?.cancel()
  }
}